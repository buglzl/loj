<template>
  <div class="home">
    <MdEditor :value="value" :handle-change="onChange" />
    <img alt="Vue logo" src="../assets/logo.png" />
    <HelloWorld msg="Welcome to Your Vue.js + TypeScript App" />
  </div>
</template>

<script setup lang="ts">
import HelloWorld from "@/components/HelloWorld.vue";
import MdEditor from "@/components/MdEditor.vue"; // @ is an alias to /src
import { ref } from "vue";

const value = ref("");
const onChange = (v: string) => {
  value.value = v;
};
</script>
