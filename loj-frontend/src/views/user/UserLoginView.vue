<template>
  <div id="userLoginView">
    <h2 style="text-align: center">用户登录</h2>
    <a-form
      :model="form"
      style="max-width: 480px; margin: 0 auto"
      auto-label-width
      label-align="left"
      @submit="handleSubmit"
    >
      <a-form-item field="userAccount" label="账号">
        <a-input v-model="form.userAccount" placeholder="请输入登录账号" />
      </a-form-item>
      <a-form-item tooltip="密码不少于8位" field="userPassword" label="密码">
        <a-input-password
          v-model="form.userPassword"
          placeholder="请输入登录密码"
        />
      </a-form-item>
      <div>
        没有账号？
        <a-link @click="toRegister">点我注册</a-link>
      </div>
      <a-form-item>
        <a-button type="primary" html-type="submit" style="width: 240px"
          >登录
        </a-button>
      </a-form-item>
    </a-form>
  </div>
</template>

<script setup lang="ts">
import { reactive } from "vue";
import { useStore } from "vuex";
import { UserControllerService, UserLoginRequest } from "../../../generated";
import { useRouter } from "vue-router";
import { Message } from "@arco-design/web-vue";

const store = useStore();
const router = useRouter();

const form = reactive({
  userAccount: "",
  userPassword: "",
}) as UserLoginRequest;

const toRegister = () => {
  router.push("/user/register");
};

const handleSubmit = async () => {
  const res = await UserControllerService.userLoginUsingPost(form);
  if (res.code === 0) {
    Message.success("登录成功！");
    await store.dispatch("setLoginUser", res.data);
    router.push({
      path: "/",
      replace: true,
    });
  } else {
    Message.error("登录失败. " + res.message);
  }
};
</script>
