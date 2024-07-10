import { StoreOptions, useStore } from "vuex";
import ACCESS_ENUM from "@/access/accessEnum";
import { UserControllerService } from "../../generated";

export default {
  state: {
    loginUser: {
      userName: "未登录",
    },
  },
  getters: {},
  mutations: {
    updateLoginUser(state, payload) {
      state.loginUser = payload;
    },
  },
  actions: {
    async setLoginUser(context, payload) {
      // 从远程请求获取登录信息
      const res = await UserControllerService.getLoginUserUsingGet();
      console.log(res);
      if (res.code === 0) {
        context.commit("updateLoginUser", res.data);
      } else {
        context.commit("updateLoginUser", {
          ...this.state.loginUser,
          userRole: ACCESS_ENUM.NOT_LOGIN,
        });
      }
    },
  },
  modules: {},
} as StoreOptions<any>;
