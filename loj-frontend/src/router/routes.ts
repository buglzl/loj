import { RouteRecordRaw } from "vue-router";
import HomeView from "@/views/HomeView.vue";
import ManageView from "@/views/ManageView.vue";
import NoPriView from "@/views/NoPriView.vue";
import ACCESS_ENUM from "@/access/accessEnum";
import UserLayout from "@/layouts/UserLayout.vue";
import UserLoginView from "@/views/user/UserLoginView.vue";
import UserRegisterView from "@/views/user/UserRegisterView.vue";
import AddQuestionView from "@/views/question/AddQuestionView.vue";
import ManageQuestionView from "@/views/question/ManageQuestionView.vue";
import QuestionsView from "@/views/question/QuestionsView.vue";
import QuestionSubmitView from "@/views/question/QuestionSubmitView.vue";
import ViewQuestionView from "@/views/question/ViewQuestionView.vue";

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
    name: "主页",
    component: HomeView,
  },
  {
    path: "/questions",
    name: "题库",
    component: QuestionsView,
    meta: {
      access: ACCESS_ENUM.NOT_LOGIN,
    },
  },
  {
    path: "/question_submit",
    name: "提交",
    component: QuestionSubmitView,
    meta: {
      hideInMenu: true,
      access: ACCESS_ENUM.NOT_LOGIN,
    },
  },
  {
    path: "/view/question/:id",
    name: "在线做题",
    component: ViewQuestionView,
    props: true,
    meta: {
      hideInMenu: true,
      access: ACCESS_ENUM.USER,
    },
  },
  {
    path: "/add/question",
    name: "创建题目",
    component: AddQuestionView,
    meta: {
      access: ACCESS_ENUM.ADMIN,
    },
  },
  {
    path: "/manage/question",
    name: "管理题目",
    component: ManageQuestionView,
    meta: {
      access: ACCESS_ENUM.ADMIN,
    },
  },
  {
    path: "/update/question",
    name: "更新题目",
    component: AddQuestionView,
    meta: {
      access: ACCESS_ENUM.ADMIN,
      hideInMenu: true,
    },
  },
  // {
  //   path: "/admin",
  //   name: "管理员可见",
  //   meta: {
  //     access: ACCESS_ENUM.ADMIN,
  //     hideInMenu: true,
  //   },
  //   component: ManageView,
  // },
  {
    path: "/noadmin",
    name: "无权限",
    component: NoPriView,
    meta: {
      hideInMenu: true,
    },
  },
];
