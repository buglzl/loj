<template>
  <div id="questionSubmitView">
    <div style="margin: 10px 10px">
      <a-form :model="searchParams" layout="inline">
        <!--        <a-form-item field="questionId" label="题号" style="min-width: 140px">-->
        <!--          <a-input-->
        <!--            v-model="searchParams.questionId"-->
        <!--            placeholder="按题目名称搜索"-->
        <!--          />-->
        <!--        </a-form-item>-->
        <a-form-item field="language" label="编程语言" style="min-width: 140px">
          <a-select
            :style="{ width: '320px' }"
            v-model="searchParams.language"
            placeholder="请选择编程语言"
            allow-clear
          >
            <a-option>cpp</a-option>
            <a-option>java</a-option>
            <a-option>go</a-option>
          </a-select>
        </a-form-item>
        <a-form-item>
          <a-button type="primary" @click="searchQuestion">搜索</a-button>
        </a-form-item>
      </a-form>
    </div>
    <a-divider :size="0" />
    <a-table
      :columns="questionColumns"
      :data="dataList"
      :pagination="{
        showTotal: true,
        pageSize: searchParams.pageSize,
        current: searchParams.current,
        total,
      }"
      data-index=""
      @page-change="onPageChange"
    >
      <template #judgeInfo="{ record }">
        <a-space
          v-if="
            record.judgeInfo.message === 'accepted' ||
            record.judgeInfo.message === 'Accepted'
          "
          style="color: rgb(68, 157, 68)"
        >
          {{ record.judgeInfo.message ? record.judgeInfo.message : "N/A" }}
          |
          {{
            `${record.judgeInfo.time ? record.judgeInfo.time + " ms" : "N/A"}`
          }}
          |
          {{
            `${
              record.judgeInfo.memory ? record.judgeInfo.memory + " kb" : "N/A"
            }`
          }}
        </a-space>
        <a-space v-else style="color: rgb(208, 84, 104)">
          {{ record.judgeInfo.message ? record.judgeInfo.message : "N/A" }}
          |
          {{
            `${record.judgeInfo.time ? record.judgeInfo.time + " ms" : "N/A"}`
          }}
          |
          {{
            `${
              record.judgeInfo.memory ? record.judgeInfo.memory + " kb" : "N/A"
            }`
          }}
        </a-space>
      </template>
      <template #createTime="{ record }">
        {{ moment(record.createTime).format("YYYY-MM-DD HH:mm:ss") }}
      </template>
    </a-table>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref, watchEffect } from "vue";
import {
  Question,
  QuestionControllerService,
  QuestionQueryRequest,
  QuestionSubmitQueryRequest,
} from "../../../generated";
import { Message } from "@arco-design/web-vue";
import { useRouter } from "vue-router";
import moment from "moment";

const total = ref(0);
const dataList = ref([]);
const router = useRouter();
const searchParams = ref<QuestionSubmitQueryRequest>({
  questionId: undefined,
  language: undefined,
  pageSize: 10,
  current: 1,
});

const questionColumns = [
  // {
  //   title: "提交号",
  //   dataIndex: "id",
  // },
  {
    title: "编程语言",
    dataIndex: "language",
  },
  {
    title: "判题信息",
    slotName: "judgeInfo",
  },
  {
    title: "判题状态",
    dataIndex: "status",
  },
  // {
  //   title: "题目 id",
  //   dataIndex: "questionId",
  // },
  // {
  //   title: "提交用户 id",
  //   dataIndex: "userId",
  // },
  {
    title: "提交时间",
    slotName: "createTime",
  },
];

const loadData = async () => {
  const res = await QuestionControllerService.listQuestionSubmitByPageUsingPost(
    {
      ...searchParams.value,
      sortField: "id",
      sortOrder: "descend",
    }
  );
  if (res.code === 0) {
    dataList.value = res.data.records;
    total.value = res.data.total;
  } else {
    Message.error("加载失败. " + res.message);
  }
};

watchEffect(() => {
  loadData();
});

const toQuestionPage = (question: Question) => {
  router.push({
    path: `/view/question/${question.id}`,
  });
};

const onPageChange = (page: number) => {
  searchParams.value = {
    ...searchParams.value,
    current: page,
  };
};

/**
 * 确认搜索
 */
const searchQuestion = () => {
  searchParams.value = {
    ...searchParams.value,
    current: 1,
  };
};

onMounted(() => {
  loadData();
});
</script>

<style scoped>
#questionSubmitView {
  max-width: 1280px;
  margin: 0 auto;
}
</style>
