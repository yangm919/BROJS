<template>
  <div id="questionSubmitView">
    <a-form :model="searchParams" layout="inline">
      <a-form-item
        field="questionId"
        label="Question ID"
        style="min-width: 240px"
      >
        <a-input v-model="searchParams.questionId" placeholder="Please enter" />
      </a-form-item>
      <a-form-item
        field="language"
        label="Programming Language"
        style="min-width: 240px"
      >
        <a-select
          v-model="searchParams.language"
          :style="{ width: '320px' }"
          placeholder="Select programming language"
        >
          <a-option>java</a-option>
          <a-option>cpp</a-option>
          <a-option>go</a-option>
          <a-option>html</a-option>
          <a-option>python</a-option>
        </a-select>
      </a-form-item>
      <a-form-item>
        <a-button type="primary" @click="doSubmit">Search</a-button>
      </a-form-item>
    </a-form>
    <a-divider :size="0" />
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
      <template #judgeInfo="{ record }">
        <div v-if="record.judgeInfo">
          <div v-if="typeof record.judgeInfo === 'string'">
            {{ record.judgeInfo }}
          </div>
          <div v-else>
            <div class="judge-info">
              <div class="judge-message">
                <a-tag :color="getJudgeStatusColor(record.judgeInfo.message)">
                  {{ record.judgeInfo.message }}
                </a-tag>
              </div>
              <div
                class="judge-stats"
                v-if="record.judgeInfo.memory || record.judgeInfo.time"
              >
                <span class="stat-item" v-if="record.judgeInfo.memory">
                  <icon-memory /> {{ formatMemory(record.judgeInfo.memory) }}
                </span>
                <span class="stat-item" v-if="record.judgeInfo.time">
                  <icon-clock-circle /> {{ formatTime(record.judgeInfo.time) }}
                </span>
              </div>
            </div>
          </div>
        </div>
      </template>
      <template #createTime="{ record }">
        {{ moment(record.createTime).format("YYYY-MM-DD") }}
      </template>
    </a-table>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref, watchEffect } from "vue";
import {
  Question,
  QuestionControllerService,
  QuestionSubmitQueryRequest,
} from "../../../generated";
import message from "@arco-design/web-vue/es/message";
import { useRouter } from "vue-router";
import moment from "moment";

const tableRef = ref();

const dataList = ref([]);
const total = ref(0);
const searchParams = ref<QuestionSubmitQueryRequest>({
  questionId: undefined,
  language: undefined,
  pageSize: 10,
  current: 1,
});

const loadData = async () => {
  const res = await QuestionControllerService.listQuestionSubmitByPageUsingPost(
    {
      ...searchParams.value,
      sortField: "createTime",
      sortOrder: "descend",
    }
  );
  if (res.code === 0) {
    dataList.value = res.data.records;
    total.value = Number(res.data.total);
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

const columns = [
  {
    title: "Submission ID",
    dataIndex: "id",
  },
  {
    title: "Programming Language",
    dataIndex: "language",
  },
  {
    title: "Judge Info",
    slotName: "judgeInfo",
  },
  {
    title: "Judge Status",
    dataIndex: "status",
  },
  {
    title: "Question ID",
    dataIndex: "questionId",
  },
  {
    title: "Submitter ID",
    dataIndex: "userId",
  },
  {
    title: "Create Time",
    slotName: "createTime",
  },
];

const onPageChange = (page: number) => {
  searchParams.value = {
    ...searchParams.value,
    current: page,
  };
};

const router = useRouter();

/**
 * Navigate to problem solving page
 * @param question
 */
const toQuestionPage = (question: Question) => {
  router.push({
    path: `/question/view/${question.id}`,
  });
};

/**
 * Confirm search, reload data
 */
const doSubmit = () => {
  // Need to reset search page number here
  searchParams.value = {
    ...searchParams.value,
    current: 1,
  };
};

/**
 * Get judge status color
 */
const getJudgeStatusColor = (message: string) => {
  switch (message) {
    case "Accepted":
      return "green";
    case "Wrong Answer":
      return "red";
    case "Time Limit Exceeded":
      return "orange";
    case "Memory Limit Exceeded":
      return "orange";
    case "Runtime Error":
      return "red";
    default:
      return "blue";
  }
};

/**
 * Format memory usage
 */
const formatMemory = (memory: number) => {
  if (memory < 1024) {
    return `${memory} KB`;
  } else {
    return `${(memory / 1024).toFixed(1)} MB`;
  }
};

/**
 * Format time usage
 */
const formatTime = (time: number) => {
  return `${time} ms`;
};
</script>

<style scoped>
#questionSubmitView {
  max-width: 1280px;
  margin: 0 auto;
}

.judge-info {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.judge-message {
  display: flex;
  align-items: center;
}

.judge-stats {
  display: flex;
  gap: 12px;
  font-size: 12px;
  color: #666;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 4px;
}

.stat-item .arco-icon {
  font-size: 14px;
}
</style>
