<template>
  <Viewer :value="value" :plugins="plugins" />
</template>

<script setup lang="ts">
import gfm from "@bytemd/plugin-gfm";
import highlight from "@bytemd/plugin-highlight";
import math from "@bytemd/plugin-math";
import { Viewer } from "@bytemd/vue-next";
import { withDefaults, defineProps } from "vue";
import "katex/dist/katex.css";

/**
 * 定义组件属性类型
 */
interface Props {
  value: string;
}

const props = withDefaults(defineProps<Props>(), {
  value: () => "",
});

const plugins = [
  gfm(),
  highlight(),
  math(),
  // Add more plugins here
];
</script>

<style>
.bytemd-toolbar-icon.bytemd-tippy.bytemd-tippy-right:last-child {
  display: none;
}
</style>
