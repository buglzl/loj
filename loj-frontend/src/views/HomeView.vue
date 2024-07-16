<template>
  <div class="home">
    <MdEditor :value="mdValue" :handle-change="onMdChange" />
    <CodeEditor :value="codeValue" :handle-change="onCodeChange" />
  </div>
</template>

<script setup lang="ts">
import HelloWorld from "@/components/HelloWorld.vue";
import MdEditor from "@/components/MdEditor.vue"; // @ is an alias to /src
import { ref } from "vue";
import CodeEditor from "@/components/CodeEditor.vue";

const mdValue = ref("");
const onMdChange = (v: string) => {
  mdValue.value = v;
  console.log(v);
};

const codeValue = ref("");
const onCodeChange = (v: string) => {
  codeValue.value = v;
  console.log(v);
};
</script>
