<template>
  <div id="viewQuestionView">
    <a-row :gutter="[24, 24]">
      <a-col :md="12" :xs="24">
        <a-tabs
          default-active-key="question"
          size="large"
          :animation="true"
          type="card-gutter"
        >
          <a-tab-pane key="question" title="题目">
            <a-card
              v-if="question"
              :title="question.title"
              label-style="margin-left: 100px; color: red;"
            >
              <div>
                <a-descriptions :column="1">
                  <a-descriptions-item label="Input file:">
                    standard input
                  </a-descriptions-item>
                  <a-descriptions-item label="Output file:">
                    standard output
                  </a-descriptions-item>
                  <a-descriptions-item label="Time limit:">
                    {{ question.judgeConfig?.timeLimit ?? `-` }} millisecond
                  </a-descriptions-item>
                  <a-descriptions-item label="Memory limit:">
                    {{ question.judgeConfig?.memoryLimit ?? `-` }} kilobytes
                  </a-descriptions-item>
                </a-descriptions>
              </div>
              <MdViewer :value="question.content || ' '" />
              <template #extra>
                <a-space wrap>
                  <a-tag
                    v-for="(tag, index) of question.tags"
                    :key="index"
                    color="green"
                    >{{ tag }}
                  </a-tag>
                </a-space>
              </template>
            </a-card>
          </a-tab-pane>
          <a-tab-pane key="submitRecord" title="提交记录">
            <QuestionSubmitView />
          </a-tab-pane>
          <a-tab-pane key="comment" title="题解" disabled> 题解</a-tab-pane>
          <a-tab-pane key="discuss" title="讨论" disabled>
            Content of Tab Panel 3
          </a-tab-pane>
        </a-tabs>
      </a-col>
      <a-col :md="12" :xs="24">
        <div>
          <a-form :model="form" layout="inline">
            <a-form-item
              field="language"
              label="编程语言"
              style="min-width: 240px"
            >
              <a-select
                :style="{ width: '320px' }"
                v-model="form.language"
                placeholder="请选择编程语言"
                allow-clear
              >
                <a-option>cpp</a-option>
                <a-option>java</a-option>
                <a-option>go</a-option>
              </a-select>
            </a-form-item>
          </a-form>
          <CodeEditor
            :language="form.language"
            :value="form.code"
            :handle-change="onCodeChange"
          />
          <a-divider :size="0" />
          <a-button
            type="primary"
            style="min-width: 120px"
            shape="round"
            status="success"
            @click="doSubmit"
          >
            <template #icon>
              <icon-code />
            </template>
            提交答案
          </a-button>
        </div>
      </a-col>
    </a-row>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref, withDefaults, defineProps } from "vue";
import {
  QuestionControllerService,
  type QuestionSubmitAddRequest,
  QuestionSubmitControllerService,
  QuestionVO,
} from "../../../generated";
import { Message } from "@arco-design/web-vue";
import CodeEditor from "@/components/CodeEditor.vue";
import MdViewer from "@/components/MdViewer.vue";
import { IconCode } from "@arco-design/web-vue/es/icon";
import QuestionSubmitView from "@/views/question/QuestionSubmitView.vue";

interface Props {
  id: string;
}

const props = withDefaults(defineProps<Props>(), {
  id: () => "",
});
const question = ref<QuestionVO>();
const form = ref<QuestionSubmitAddRequest>({
  language: "",
  code: "",
});

const loadData = async () => {
  const res = await QuestionControllerService.getQuestionVoByIdUsingGet(
    props.id as any
  );
  if (res.code === 0) {
    question.value = res.data;
  } else {
    Message.error("加载失败. " + res.message);
  }
};

const onCodeChange = (v: string) => {
  form.value.code = v;
};

const doSubmit = async () => {
  if (!question.value?.id) {
    return;
  }
  console.log(form.value.code);
  console.log(form);
  const res = await QuestionControllerService.doQuestionSubmitUsingPost({
    ...form.value,
    questionId: question.value.id,
  });
  if (res.code === 0) {
    Message.success("提交成功");
  } else {
    Message.error("提交失败, " + res.message);
  }
};

onMounted(() => {
  loadData();
});
</script>

<style scoped>
#viewQuestionView {
  max-width: 1600px;
  margin: 0 auto;
}

#viewQuestionView .arco-space-horizontal .arco-space-item {
  margin-bottom: 0 !important;
}
</style>
