import { RouteRecordRaw } from "vue-router";
import HomeView from "@/views/HomeView.vue";
import ManageView from "@/views/ManageView.vue";
import NoPriView from "@/views/NoPriView.vue";
import ACCESS_ENUM from "@/access/accessEnum";
import UserLayout from "@/layouts/UserLayout.vue";
import UserLoginView from "@/views/user/UserLoginView.vue";
import UserRegisterView from "@/views/user/UserRegisterView.vue";

export const routes: Array<RouteRecordRaw> = [
  {
    path: "/user",
    name: "用户",
    component: UserLayout,
    meta: {
      hideInMenu: true,
    },
    children: [
      {
        path: "/user/login",
        name: "登录",
        component: UserLoginView,
      },
      {
        path: "/user/register",
        name: "注册",
        component: UserRegisterView,
      },
    ],
  },
  {
    path: "/",
    name: "题目",
    component: HomeView,
  },
  {
    path: "/admin",
    name: "管理员可见",
    meta: {
      access: ACCESS_ENUM.ADMIN,
    },
    component: ManageView,
  },
  {
    path: "/noadmin",
    name: "无权限",
    component: NoPriView,
  },
  {
    path: "/about",
    name: "关于我的",
    // route level code-splitting
    // this generates a separate chunk (about.[hash].js) for this route
    // which is lazy-loaded when the route is visited.
    component: () =>
      import(/* webpackChunkName: "about" */ "../views/AboutView.vue"),
  },
];
