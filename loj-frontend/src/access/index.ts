import router from "@/router";
import store from "@/store";
import ACCESS_ENUM from "@/access/accessEnum";
import checkAccess from "@/access/checkAccess";

router.beforeEach(async (to, from, next) => {
  console.log("用户登录信息", store.state.user.loginUser);

  const loginUser = store.state.user.loginUser;
  // 如果没有登录过，先登录依次，这样就有userRole
  if (!loginUser || !loginUser.userRole) {
    // 等待用户登录成功再执行之后的代码
    await store.dispatch("setLoginUser");
  }

  const needAccess = (to.meta?.access as string) ?? ACCESS_ENUM.NOT_LOGIN;
  // 如果必须要登录
  if (needAccess !== ACCESS_ENUM.NOT_LOGIN) {
    if (!loginUser || !loginUser.userRole) {
      // 则先去登录界面
      console.log("要去登录界面", loginUser);
      next(`/user/login?redirect=${to.fullPath}`);
      return;
    }
    // 如果已经登录了，但是权限不足
    if (!checkAccess(loginUser, needAccess)) {
      console.log("权限不足");
      next("/noadmin");
      return;
    }
  }

  next();
});
