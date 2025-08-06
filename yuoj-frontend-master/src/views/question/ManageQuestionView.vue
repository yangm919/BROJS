<template>
  <div id="manageQuestionView">
    <a-table
      :ref="tableRef"
      :columns="columns"
      :data="dataList"
      :pagination="{
        showTotal: true,
        pageSize: searchParams.pageSize,
        current: searchParams.current,
        total,
      }"
      @page-change="onPageChange"
    >
      <template #optional="{ record }">
        <a-space>
          <a-button type="primary" @click="doUpdate(record)"> Edit</a-button>
          <a-button status="danger" @click="doDelete(record)">Delete</a-button>
        </a-space>
      </template>
    </a-table>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref, watchEffect } from "vue";
import {
  Page_Question_,
  Question,
  QuestionControllerService,
} from "../../../generated";
import message from "@arco-design/web-vue/es/message";
import * as querystring from "querystring";
import { useRouter } from "vue-router";

const tableRef = ref();

const dataList = ref([]);
const total = ref(0);
const searchParams = ref({
  pageSize: 10,
  current: 1,
});

const loadData = async () => {
  const res = await QuestionControllerService.listQuestionByPageUsingPost(
    searchParams.value
  );
  if (res.code === 0) {
    dataList.value = res.data.records;
    total.value = res.data.total;
  } else {
    message.error("Loading failed, " + res.message);
  }
};

/**
 * Watch searchParams variable, trigger page reload when changed
 */
watchEffect(() => {
  loadData();
});

/**
 * Request data when page loads
 */
onMounted(() => {
  loadData();
});

// Example data structure: {id: "1", title: "A+ D", content: "New question content", tags: "["binary tree"]", answer: "New answer", submitNum: 0,…}

const columns = [
  {
    title: "id",
    dataIndex: "id",
  },
  {
    title: "Title",
    dataIndex: "title",
  },
  {
    title: "Content",
    dataIndex: "content",
  },
  {
    title: "Tags",
    dataIndex: "tags",
  },
  {
    title: "Answer",
    dataIndex: "answer",
  },
  {
    title: "Submission Count",
    dataIndex: "submitNum",
  },
  {
    title: "Accepted Count",
    dataIndex: "acceptedNum",
  },
  {
    title: "Judge Config",
    dataIndex: "judgeConfig",
  },
  {
    title: "Judge Cases",
    dataIndex: "judgeCase",
  },
  {
    title: "User ID",
    dataIndex: "userId",
  },
  {
    title: "Create Time",
    dataIndex: "createTime",
  },
  {
    title: "Actions",
    slotName: "optional",
  },
];

const onPageChange = (page: number) => {
  searchParams.value = {
    ...searchParams.value,
    current: page,
  };
};

const doDelete = async (question: Question) => {
  const res = await QuestionControllerService.deleteQuestionUsingPost({
    id: question.id,
  });
  if (res.code === 0) {
    message.success("Delete successful");
    loadData();
  } else {
    message.error("Delete failed");
  }
};

const router = useRouter();

const doUpdate = (question: Question) => {
  router.push({
    path: "/update/question",
    query: {
      id: question.id,
    },
  });
};
</script>

<style scoped>
#manageQuestionView {
}
</style>
