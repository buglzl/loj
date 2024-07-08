<template>
  <div id="globalNav">
    <a-row
      align="center"
      style="background-color: black; box-shadow: 2px 2px 2px #888888"
    >
      <a-col flex="auto">
        <a-menu
          mode="horizontal"
          :selectedKeys="selectedKeys"
          @menu-item-click="doMenuClick"
          theme="dark"
        >
          <a-menu-item
            key="0"
            :style="{ padding: 0, marginRight: '38px' }"
            disabled
          >
            <div class="title-bar">
              <img class="logo" src="../assets/logo.png" />
              <div class="title">L OJ</div>
            </div>
          </a-menu-item>
          <a-menu-item v-for="item in routes" :key="item.path">
            {{ item.name }}
          </a-menu-item>
        </a-menu>
      </a-col>
      <a-col flex="100px">
        <div class="loginUser">
          {{ store.state.user?.username ?? "未登录" }}
        </div>
      </a-col>
    </a-row>
  </div>
</template>

<script setup lang="ts">
import { routes } from "@/router/routes";
import { ref } from "vue";
import { useRouter } from "vue-router";
import { useStore } from "vuex";

const router = useRouter();
const store = useStore();

const selectedKeys = ref(["/"]);

setTimeout(() => {
  store.dispatch("setUsername", "lzl");
}, 3000);

router.afterEach((to, from, failure) => {
  selectedKeys.value = [to.path];
});

const doMenuClick = (key: string) => {
  router.push({ path: key });
};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
.title-bar {
  display: flex;
  text-align: center;
}

.logo {
  height: 48px;
  margin-right: 10px;
}

.title-bar .title {
  padding-top: 10px;
  font-size: 25px;
  color: whitesmoke;
}

.loginUser {
  text-align: center;
  color: whitesmoke;
}
</style>
