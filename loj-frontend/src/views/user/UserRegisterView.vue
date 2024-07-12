<template>
  <div id="userRegisterView">
    <h2 style="text-align: center">用户注册</h2>
    <a-form
      :model="form"
      style="max-width: 480px; margin: 0 auto"
      auto-label-width
      label-align="left"
      @submit="handleSubmit"
    >
      <a-form-item field="userAccount" label="账号">
        <a-input v-model="form.userAccount" placeholder="请输入账号" />
      </a-form-item>
      <a-form-item tooltip="密码不少于8位" field="userPassword" label="密码">
        <a-input-password
          v-model="form.userPassword"
          placeholder="请输入密码"
        />
      </a-form-item>
      <a-form-item
        tooltip="再输入一次密码"
        field="checkPassword"
        label="确认密码"
      >
        <a-input-password
          v-model="form.checkPassword"
          placeholder="请确认输入密码"
        />
      </a-form-item>
      <a-form-item>
        <a-button type="primary" html-type="submit" style="width: 120px"
          >注册
        </a-button>
      </a-form-item>
    </a-form>
  </div>
</template>

<script setup lang="ts">
import { reactive } from "vue";
import { useStore } from "vuex";
import { UserControllerService, UserRegisterRequest } from "../../../generated";
import { useRouter } from "vue-router";
import { toNumber } from "@vue/shared";

const store = useStore();
const router = useRouter();

const form = reactive({
  checkPassword: "",
  userAccount: "",
  userPassword: "",
}) as UserRegisterRequest;

const handleSubmit = async () => {
  console.log(form);
  const res = await UserControllerService.userRegisterUsingPost(form);
  console.log("返回结果", res);
  if (res.code === 0) {
    router.push({
      path: "/user/login",
      replace: true,
    });
  } else {
    alert("登录失败. " + res.message);
  }
};
</script>
