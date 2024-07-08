import { StoreOptions } from "vuex";

export default {
  state: {
    username: "未登录",
    role: "admin",
  },
  getters: {},
  mutations: {
    updateUser(state, username) {
      state.username = username;
    },
  },
  actions: {
    setUsername(context, username) {
      context.commit("updateUser", username);
    },
  },
  modules: {},
} as StoreOptions<any>;
