import { RouteRecordRaw } from "vue-router";
import HomeView from "@/views/HomeView.vue";
import ManageView from "@/views/ManageView.vue";
import NoPriView from "@/views/NoPriView.vue";

export const routes: Array<RouteRecordRaw> = [
  {
    path: "/",
    name: "题目",
    component: HomeView,
  },
  {
    path: "/admin",
    name: "管理员可见",
    meta: {
      access: "canAdmin",
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
