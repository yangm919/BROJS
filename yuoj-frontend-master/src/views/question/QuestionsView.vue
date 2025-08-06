<template>
  <div id="questionsView">
    <!-- Search area -->
    <div class="search-section">
      <div class="search-header">
        <h2 class="page-title">Programming Questions</h2>
        <p class="page-subtitle">
          Challenge yourself with our curated collection of programming problems
        </p>
      </div>

      <div class="search-form">
        <a-form
          :model="searchParams"
          layout="inline"
          class="search-form-content"
        >
          <a-form-item field="title" label="Question Name" class="search-item">
            <a-input
              v-model="searchParams.title"
              placeholder="Search by question name..."
              class="search-input"
              allow-clear
            >
              <template #prefix>
                <icon-search />
              </template>
            </a-input>
          </a-form-item>

          <a-form-item field="tags" label="Tags" class="search-item">
            <a-select
              v-model="searchParams.tags"
              placeholder="Select tags to filter..."
              multiple
              allow-clear
              allow-search
              :options="tagOptions"
              class="search-tags"
              style="width: 100%"
            />
          </a-form-item>

          <a-form-item class="search-actions">
            <a-button type="primary" @click="doSubmit" class="search-button">
              <icon-search />
              Search
            </a-button>
            <a-button @click="resetSearch" class="reset-button">
              <icon-refresh />
              Reset
            </a-button>
          </a-form-item>
        </a-form>
      </div>
    </div>

    <!-- Statistics -->
    <div class="stats-section">
      <a-row :gutter="16">
        <a-col :span="6">
          <div class="stat-card">
            <div class="stat-icon">
              <icon-file />
            </div>
            <div class="stat-content">
              <div class="stat-number">{{ total }}</div>
              <div class="stat-label">Total Questions</div>
            </div>
          </div>
        </a-col>
        <a-col :span="6">
          <div class="stat-card">
            <div class="stat-icon solved">
              <icon-check-circle />
            </div>
            <div class="stat-content">
              <div class="stat-number">{{ solvedCount }}</div>
              <div class="stat-label">Solved</div>
            </div>
          </div>
        </a-col>
        <a-col :span="6">
          <div class="stat-card">
            <div class="stat-icon attempted">
              <icon-clock-circle />
            </div>
            <div class="stat-content">
              <div class="stat-number">{{ attemptedCount }}</div>
              <div class="stat-label">Attempted</div>
            </div>
          </div>
        </a-col>
        <a-col :span="6">
          <div class="stat-card">
            <div class="stat-icon success-rate">
              <icon-trophy />
            </div>
            <div class="stat-content">
              <div class="stat-number">{{ successRate }}%</div>
              <div class="stat-label">Success Rate</div>
            </div>
          </div>
        </a-col>
      </a-row>
    </div>

    <!-- Question List -->
    <div class="questions-section">
      <div class="questions-header">
        <h3 class="section-title">Available Questions</h3>
        <div class="view-controls">
          <a-radio-group v-model="viewMode" size="small">
            <a-radio-button value="table">Table View</a-radio-button>
            <a-radio-button value="card">Card View</a-radio-button>
          </a-radio-group>
        </div>
      </div>

      <!-- Table View -->
      <div v-if="viewMode === 'table'" class="table-view">
        <a-table
          :ref="tableRef"
          :columns="columns"
          :data="dataList"
          :pagination="{
            showTotal: true,
            pageSize: searchParams.pageSize,
            current: searchParams.current,
            total,
            showJumper: true,
            showSizeChanger: true,
          }"
          @page-change="onPageChange"
          class="questions-table"
          :row-class="getRowClass"
        >
          <template #tags="{ record }">
            <a-space wrap>
              <a-tag
                v-for="(tag, index) of record.tags"
                :key="index"
                color="blue"
                class="question-tag"
              >
                {{ tag }}
              </a-tag>
            </a-space>
          </template>

          <template #acceptedRate="{ record }">
            <div class="rate-display">
              <div class="rate-bar">
                <div
                  class="rate-fill"
                  :style="{ width: getAcceptRate(record) + '%' }"
                ></div>
              </div>
              <span class="rate-text">
                {{ getAcceptRate(record) }}% ({{ record.acceptedNum }}/{{
                  record.submitNum
                }})
              </span>
            </div>
          </template>

          <template #createTime="{ record }">
            <span class="time-display">
              {{ moment(record.createTime).format("YYYY-MM-DD") }}
            </span>
          </template>

          <template #optional="{ record }">
            <a-space>
              <a-button
                type="primary"
                @click="toQuestionPage(record)"
                class="solve-button"
              >
                <icon-play-circle />
                Solve
              </a-button>
            </a-space>
          </template>
        </a-table>
      </div>

      <!-- Card View -->
      <div v-else class="card-view">
        <a-row :gutter="[16, 16]">
          <a-col
            v-for="question in dataList"
            :key="question.id"
            :xs="24"
            :sm="12"
            :md="8"
            :lg="6"
          >
            <div class="question-card" @click="toQuestionPage(question)">
              <div class="card-header">
                <h4 class="question-title">{{ question.title }}</h4>
                <div
                  class="difficulty-badge"
                  :class="getDifficultyClass(question.difficulty)"
                >
                  {{ question.difficulty || "Unknown" }}
                </div>
              </div>

              <div class="card-content">
                <p class="question-description">
                  {{
                    question.content?.substring(0, 100) ||
                    "No description available"
                  }}...
                </p>

                <div class="question-tags">
                  <a-tag
                    v-for="(tag, index) of question.tags"
                    :key="index"
                    color="blue"
                    size="small"
                  >
                    {{ tag }}
                  </a-tag>
                </div>

                <div class="question-stats">
                  <div class="stat-item">
                    <icon-eye />
                    <span>{{ question.submitNum || 0 }}</span>
                  </div>
                  <div class="stat-item">
                    <icon-check-circle />
                    <span>{{ question.acceptedNum || 0 }}</span>
                  </div>
                  <div class="stat-item">
                    <icon-clock-circle />
                    <span>{{ getAcceptRate(question) }}%</span>
                  </div>
                </div>
              </div>

              <div class="card-footer">
                <a-button type="primary" size="small" class="card-solve-btn">
                  <icon-play-circle />
                  Solve
                </a-button>
              </div>
            </div>
          </a-col>
        </a-row>

        <!-- Card View Pagination -->
        <div class="card-pagination">
          <a-pagination
            v-model:current="searchParams.current"
            v-model:page-size="searchParams.pageSize"
            :total="total"
            :show-total="true"
            :show-jumper="true"
            :show-size-changer="true"
            @change="onPageChange"
          />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref, watchEffect, computed } from "vue";
import {
  Page_Question_,
  Question,
  QuestionControllerService,
  QuestionQueryRequest,
} from "../../../generated";
import message from "@arco-design/web-vue/es/message";
import * as querystring from "querystring";
import { useRouter } from "vue-router";
import moment from "moment";
import {
  IconSearch,
  IconRefresh,
  IconFile,
  IconCheckCircle,
  IconClockCircle,
  IconTrophy,
  IconPlayCircle,
  IconEye,
} from "@arco-design/web-vue/es/icon";
import { getTagOptions } from "@/constants/tags";

const tableRef = ref();
const router = useRouter();

// Tag options
const tagOptions = getTagOptions();

const dataList = ref<any[]>([]);
const total = ref(0);
const viewMode = ref("table");
const searchParams = ref<QuestionQueryRequest>({
  title: "",
  tags: [],
  pageSize: 8,
  current: 1,
});

// Calculate statistics
const solvedCount = computed(() => {
  return dataList.value.filter((q: any) => q.status === "solved").length;
});

const attemptedCount = computed(() => {
  return dataList.value.filter((q: any) => q.status === "attempted").length;
});

const successRate = computed(() => {
  if (total.value === 0) return 0;
  return Math.round((solvedCount.value / total.value) * 100);
});

const loadData = async () => {
  try {
    const res = await QuestionControllerService.listQuestionVoByPageUsingPost(
      searchParams.value
    );
    if (res.code === 0) {
      dataList.value = res.data.records;
      total.value = res.data.total;
    } else {
      message.error("Loading failed, " + res.message);
    }
  } catch (error) {
    message.error("Failed to load questions");
  }
};

/**
 * Monitor searchParams variable, trigger page reload when changed
 */
watchEffect(() => {
  // Only reload when searchParams changes, not on every reactive update
  if (
    searchParams.value.current ||
    searchParams.value.title ||
    (searchParams.value.tags && searchParams.value.tags.length > 0)
  ) {
    loadData();
  }
});

const doSubmit = () => {
  searchParams.value.current = 1;
  loadData();
};

const resetSearch = () => {
  searchParams.value = {
    title: "",
    tags: [],
    pageSize: 8,
    current: 1,
  };
};

const onPageChange = (current: number) => {
  searchParams.value.current = current;
  loadData();
};

const toQuestionPage = (question: Question) => {
  router.push(`/question/view/${question.id}`);
};

const getAcceptRate = (question: Question) => {
  if (!question.submitNum || question.submitNum === 0) return 0;
  return Math.round(((question.acceptedNum || 0) / question.submitNum) * 100);
};

const getDifficultyClass = (difficulty?: string) => {
  switch (difficulty?.toLowerCase()) {
    case "easy":
      return "difficulty-easy";
    case "medium":
      return "difficulty-medium";
    case "hard":
      return "difficulty-hard";
    default:
      return "difficulty-unknown";
  }
};

const getRowClass = (record: any) => {
  return {
    "row-solved": record.status === "solved",
    "row-attempted": record.status === "attempted",
  };
};

const columns = [
  {
    title: "Title",
    dataIndex: "title",
    key: "title",
  },
  {
    title: "Tags",
    dataIndex: "tags",
    key: "tags",
    slotName: "tags",
  },
  {
    title: "Acceptance Rate",
    dataIndex: "acceptedRate",
    key: "acceptedRate",
    slotName: "acceptedRate",
  },
  {
    title: "Create Time",
    dataIndex: "createTime",
    key: "createTime",
    slotName: "createTime",
  },
  {
    title: "Action",
    key: "optional",
    slotName: "optional",
  },
];

onMounted(() => {
  loadData();
});
</script>

<style scoped>
#questionsView {
  max-width: 100%;
}

/* Search area */
.search-section {
  background: linear-gradient(
    135deg,
    rgba(255, 255, 255, 0.9) 0%,
    rgba(255, 255, 255, 0.7) 100%
  );
  border-radius: 16px;
  padding: 24px;
  margin-bottom: 24px;
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.search-header {
  margin-bottom: 20px;
  text-align: center;
}

.page-title {
  font-size: 28px;
  font-weight: 700;
  color: #1d2129;
  margin: 0 0 8px 0;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.page-subtitle {
  color: #86909c;
  font-size: 16px;
  margin: 0;
}

.search-form {
  background: rgba(255, 255, 255, 0.8);
  border-radius: 12px;
  padding: 20px;
  border: 1px solid rgba(255, 255, 255, 0.3);
}

.search-form-content {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
  align-items: flex-end;
}

.search-item {
  margin-bottom: 0;
}

.search-input {
  min-width: 200px;
}

.search-tags {
  min-width: 200px;
}

.search-actions {
  margin-bottom: 0;
  display: flex;
  gap: 12px;
}

.search-button {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  display: flex;
  align-items: center;
  gap: 6px;
}

.reset-button {
  display: flex;
  align-items: center;
  gap: 6px;
}

/* Statistics area */
.stats-section {
  margin-bottom: 24px;
}

.stat-card {
  background: rgba(255, 255, 255, 0.9);
  border-radius: 12px;
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 16px;
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.2);
  transition: all 0.3s ease;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
}

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1);
}

.stat-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  color: white;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.stat-icon.solved {
  background: linear-gradient(135deg, #52c41a 0%, #389e0d 100%);
}

.stat-icon.attempted {
  background: linear-gradient(135deg, #faad14 0%, #d48806 100%);
}

.stat-icon.success-rate {
  background: linear-gradient(135deg, #f5222d 0%, #cf1322 100%);
}

.stat-content {
  flex: 1;
}

.stat-number {
  font-size: 24px;
  font-weight: 700;
  color: #1d2129;
  line-height: 1;
}

.stat-label {
  font-size: 14px;
  color: #86909c;
  margin-top: 4px;
}

/* Question List area */
.questions-section {
  background: rgba(255, 255, 255, 0.9);
  border-radius: 16px;
  padding: 24px;
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.questions-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.section-title {
  font-size: 20px;
  font-weight: 600;
  color: #1d2129;
  margin: 0;
}

.view-controls {
  display: flex;
  gap: 12px;
}

/* Table View */
.questions-table {
  border-radius: 12px;
  overflow: hidden;
}

.questions-table :deep(.arco-table-th) {
  background: rgba(102, 126, 234, 0.1);
  font-weight: 600;
}

.questions-table :deep(.arco-table-tr:hover) {
  background: rgba(102, 126, 234, 0.05);
}

.row-solved {
  background: rgba(82, 196, 26, 0.05);
}

.row-attempted {
  background: rgba(250, 173, 20, 0.05);
}

.question-tag {
  border-radius: 6px;
  font-size: 12px;
}

.rate-display {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.rate-bar {
  width: 100%;
  height: 6px;
  background: #f0f0f0;
  border-radius: 3px;
  overflow: hidden;
}

.rate-fill {
  height: 100%;
  background: linear-gradient(90deg, #52c41a 0%, #389e0d 100%);
  border-radius: 3px;
  transition: width 0.3s ease;
}

.rate-text {
  font-size: 12px;
  color: #86909c;
}

.time-display {
  color: #86909c;
  font-size: 14px;
}

.solve-button {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  display: flex;
  align-items: center;
  gap: 6px;
}

/* Card View */
.question-card {
  background: rgba(255, 255, 255, 0.9);
  border-radius: 12px;
  padding: 20px;
  cursor: pointer;
  transition: all 0.3s ease;
  border: 1px solid rgba(255, 255, 255, 0.2);
  backdrop-filter: blur(10px);
  height: 100%;
  display: flex;
  flex-direction: column;
}

.question-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 12px;
}

.question-title {
  font-size: 16px;
  font-weight: 600;
  color: #1d2129;
  margin: 0;
  flex: 1;
  line-height: 1.4;
}

.difficulty-badge {
  padding: 4px 8px;
  border-radius: 6px;
  font-size: 12px;
  font-weight: 500;
  color: white;
  margin-left: 8px;
}

.difficulty-easy {
  background: #52c41a;
}

.difficulty-medium {
  background: #faad14;
}

.difficulty-hard {
  background: #f5222d;
}

.difficulty-unknown {
  background: #86909c;
}

.card-content {
  flex: 1;
  margin-bottom: 16px;
}

.question-description {
  color: #86909c;
  font-size: 14px;
  line-height: 1.5;
  margin-bottom: 12px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.question-tags {
  margin-bottom: 12px;
}

.question-stats {
  display: flex;
  gap: 16px;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: #86909c;
}

.card-footer {
  margin-top: auto;
}

.card-solve-btn {
  width: 100%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
}

.card-pagination {
  margin-top: 24px;
  display: flex;
  justify-content: center;
}

/* Responsive design */
@media (max-width: 768px) {
  .search-form-content {
    flex-direction: column;
    align-items: stretch;
  }

  .search-item {
    width: 100%;
  }

  .search-input,
  .search-tags {
    width: 100%;
  }

  .questions-header {
    flex-direction: column;
    gap: 16px;
    align-items: stretch;
  }

  .view-controls {
    justify-content: center;
  }
}

@media (max-width: 480px) {
  .search-section,
  .questions-section {
    padding: 16px;
  }

  .page-title {
    font-size: 24px;
  }

  .page-subtitle {
    font-size: 14px;
  }

  .stat-card {
    padding: 16px;
  }

  .stat-icon {
    width: 40px;
    height: 40px;
    font-size: 20px;
  }

  .stat-number {
    font-size: 20px;
  }
}
</style>
