<template>
  <a-table
    :columns="questionColumns"
    :data="dataList"
    :pagination="{
      showTotal: true,
      pageSize: searchParams.pageSize,
      current: searchParams.pageNumber,
      total,
    }"
  >
    <template #optional="{ record }">
      <a-space>
        <a-button type="primary" @click="doUpdate(record)">修改</a-button>
        <a-button status="danger" @click="doDelete">删除</a-button>
        <a-modal
          v-model:visible="visible"
          @ok="handleOk(record)"
          @cancel="handleCancel"
        >
          <template #title> 警告</template>
          <div>请再次确认删除</div>
        </a-modal>
      </a-space>
    </template>
  </a-table>
</template>

<script setup lang="ts">
import { onMounted, ref } from "vue";
import {
  DeleteRequest,
  Question,
  QuestionControllerService,
} from "../../../generated";
import { Message } from "@arco-design/web-vue";
import { useRouter } from "vue-router";

const show = ref(true);
const total = ref(0);
const dataList = ref([]);
const visible = ref(false);
const router = useRouter();
const searchParams = ref({
  pageSize: 10,
  pageNumber: 1,
});

const loadData = async () => {
  const res = await QuestionControllerService.listQuestionByPageUsingPost(
    searchParams.value
  );
  if (res.code === 0) {
    dataList.value = res.data.records;
    total.value = res.data.total;
  } else {
    Message.error("加载失败. " + res.message);
  }
};

const handleClick = () => {
  visible.value = true;
};
const handleOk = async (question: Question) => {
  visible.value = false;
  const res = await QuestionControllerService.deleteQuestionUsingPost({
    id: question.id,
  });
  if (res.code === 0) {
    Message.success("删除成功");
    // 更新数据
    loadData();
  } else {
    Message.error("删除失败. " + res.message);
  }
};
const handleCancel = () => {
  visible.value = false;
};

const doDelete = () => {
  visible.value = true;
};

const doUpdate = (question: Question) => {
  router.push({
    path: "/update/question",
    query: {
      id: question.id,
    },
  });
};

onMounted(() => {
  loadData();
});

const questionColumns = [
  {
    title: "id",
    dataIndex: "id",
  },
  {
    title: "标题",
    dataIndex: "title",
  },
  {
    title: "标签",
    dataIndex: "tags",
  },
  {
    title: "题目内容",
    dataIndex: "content",
    width: 250,
  },
  {
    title: "答案",
    dataIndex: "answer",
    width: 250,
  },
  {
    title: "通过数",
    dataIndex: "acceptedNum",
  },
  {
    title: "提交数",
    dataIndex: "submitNum",
  },
  {
    title: "创建用户",
    dataIndex: "userId",
  },
  {
    title: "创建时间",
    dataIndex: "createTime",
  },
  {
    title: "上次更新时间",
    dataIndex: "updateTime",
  },
  {
    title: "判题配置",
    dataIndex: "judgeConfig",
  },
  {
    title: "判题用例",
    dataIndex: "judgeCase",
  },
  {
    title: "操作",
    slotName: "optional",
    minWidth: 100,
  },
];
</script>

<style scoped></style>
