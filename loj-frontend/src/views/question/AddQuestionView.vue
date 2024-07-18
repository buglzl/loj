<template>
  <div id="addQuestionView">
    <h2>创建题目</h2>
    <a-form
      :model="form"
      :style="{ width: '600px' }"
      labelAlign="left"
      @submit="handleSubmit"
    >
      <a-form-item field="title" label="标题">
        <a-input v-model="form.title" placeholder="请输入题目标题" />
      </a-form-item>
      <a-form-item field="tags" label="标签">
        <a-input-tag v-model="form.tags" placeholder="请选择标签" allow-clear />
      </a-form-item>
      <a-form-item field="content" label="题目内容">
        <MdEditor
          :value="form.content"
          :handle-change="onContentChange"
          style="min-width: 60vw"
        />
      </a-form-item>
      <a-form-item field="answer" label="答案">
        <MdEditor
          :value="form.answer"
          :handle-change="onAnswerChange"
          style="min-width: 60vw"
        />
      </a-form-item>

      <a-form-item label="判题限制" :content-flex="false" :merge-props="false">
        <a-space direction="vertical" fill>
          <a-form-item field="form.judgeConfig.timeLimit" label="时间限制">
            <a-input-number
              v-model="form.judgeConfig.timeLimit"
              :style="{ width: '320px' }"
              placeholder="请输入时间限制"
              class="input-demo"
              :min="10485760"
              :max="535298048"
              model-event="input"
            />
          </a-form-item>
          <a-form-item field="form.judgeConfig.memoryLimit" label="空间限制">
            <a-input-number
              v-model="form.judgeConfig.memoryLimit"
              :style="{ width: '320px' }"
              placeholder="请输入空间限制"
              class="input-demo"
              :min="10485760"
              :max="535298048"
              model-event="input"
            />
          </a-form-item>
          <a-form-item field="form.judgeConfig.stackLimit" label="栈限制">
            <a-input-number
              v-model="form.judgeConfig.stackLimit"
              :style="{ width: '320px' }"
              placeholder="请输入栈限制"
              class="input-demo"
              :min="10485760"
              :max="535298048"
              model-event="input"
            />
          </a-form-item>
        </a-space>
      </a-form-item>
      <a-form-item label="判题样例" :content-flex="false" :merge-props="false">
        <a-form-item
          v-for="(caseItem, index) of form.judgeCase"
          :field="`form.judgeCase[${index}]`"
          :label="`样例-${index}`"
          :key="index"
        >
          <a-space>
            <a-textarea
              placeholder="请输入样例输入"
              :auto-size="{
                minRows: 2,
                maxRows: 5,
              }"
              v-model="caseItem.input"
              style="margin-top: 20px; min-width: 350px"
            />
            <a-textarea
              placeholder="请输入样例输出"
              :auto-size="{
                minRows: 2,
                maxRows: 5,
              }"
              v-model="caseItem.output"
              style="margin-top: 20px; min-width: 350px"
            />
            <!--            <a-input v-model="caseItem.input" placeholder="请输入样例输入" />-->
            <!--            <a-input v-model="caseItem.output" placeholder="请输入样例输出" />-->
            <a-button
              @click="handleDeleteCase(index)"
              :style="{ marginLeft: '10px' }"
              status="danger"
              >删除
            </a-button>
          </a-space>
        </a-form-item>
        <div>
          <a-button @click="handleAddCase" type="outline" status="success"
            >添加样例
          </a-button>
        </div>
      </a-form-item>

      <a-form-item>
        <a-button
          html-type="提交"
          type="primary"
          style="min-width: 200px; margin-top: 20px"
          >Submit
        </a-button>
      </a-form-item>
    </a-form>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from "vue";
import MdEditor from "@/components/MdEditor.vue";
import CodeEditor from "@/components/CodeEditor.vue";
import {
  QuestionAddRequest,
  QuestionControllerService,
} from "../../../generated";
import { errorMessages } from "@vue/compiler-core";
import { Message } from "@arco-design/web-vue";
import { useRoute } from "vue-router";
import { addFromIconFontCn } from "@arco-design/web-vue/es/icon-component/add-from-icon-font-cn";

const route = useRoute();
// 如果路由包含 update 视作更新页面
const updatePage = route.path.includes("update");
/**
 *
 * 添加更新题目表单
 */
const form = ref({
  title: "",
  tags: [],
  answer: "",
  content: "",
  judgeCase: [
    {
      input: "",
      output: "",
    },
  ],
  judgeConfig: {
    timeLimit: 0,
    memoryLimit: 0,
    stackLimit: 0,
  },
});
/**
 * 根据id获取题目信息
 */
const loadData = async () => {
  const id = route.query.id;
  if (!id) {
    return;
  }
  const res = await QuestionControllerService.getQuestionByIdUsingGet(
    id as any
  );
  console.log(res);

  if (res.code === 0) {
    form.value = res.data as any;
    if (!form.value.judgeCase) {
      form.value.judgeCase = [
        {
          input: "",
          output: "",
        },
      ];
    } else {
      form.value.judgeCase = JSON.parse(form.value.judgeCase as any);
    }
    if (!form.value.judgeConfig) {
      form.value.judgeConfig = {
        timeLimit: 0,
        memoryLimit: 0,
        stackLimit: 0,
      };
    } else {
      form.value.judgeConfig = JSON.parse(form.value.judgeConfig as any);
    }

    if (!form.value.tags) {
      form.value.tags = [];
    } else {
      form.value.tags = JSON.parse(form.value.tags as any);
    }
  } else {
    Message.error("加载失败. " + res.message);
  }
};

const handleAddCase = () => {
  if (form.value.judgeCase) {
    form.value.judgeCase.push({
      input: "",
      output: "",
    });
  }
};

const handleDeleteCase = (index: number) => {
  if (form.value.judgeCase) {
    form.value.judgeCase.splice(index, 1);
  }
};

const onAnswerChange = (v: string) => {
  form.value.answer = v;
};

const onContentChange = (v: string) => {
  form.value.content = v;
};

const handleSubmit = async () => {
  console.log(form);
  if (updatePage) {
    const res = await QuestionControllerService.updateQuestionUsingPost(
      form.value
    );
    console.log(res);
    if (res.code === 0) {
      Message.success("修改成功");
    } else {
      Message.error("修改失败失败. " + res.message);
    }
  } else {
    const res = await QuestionControllerService.addQuestionUsingPost(
      form.value
    );
    console.log(res);
    if (res.code === 0) {
      Message.success("创建成功");
    } else {
      Message.error("创建失败. " + res.message);
    }
  }
};

onMounted(() => {
  loadData();
});
</script>

<style scoped></style>
