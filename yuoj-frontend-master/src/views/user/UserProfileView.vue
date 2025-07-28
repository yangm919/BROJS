<template>
  <div id="userProfileView">
    <div class="profile-container">
      <div class="profile-header">
        <h2 class="title">个人信息</h2>
        <p class="subtitle">查看您的学习统计和成就</p>
      </div>

      <div class="profile-content">
        <!-- 用户基本信息 -->
        <a-card class="user-info-card" title="基本信息">
          <div class="user-info">
            <div class="avatar-section">
              <a-avatar :size="80" :src="userInfo.userAvatar">
                <icon-user />
              </a-avatar>
            </div>
            <div class="info-section">
              <h3>{{ userInfo.userName || "用户" }}</h3>
              <p class="user-role">{{ getRoleText(userInfo.userRole) }}</p>
              <p class="join-date">
                加入时间：{{ formatDate(userInfo.createTime) }}
              </p>
            </div>
          </div>
        </a-card>

        <!-- 统计信息 -->
        <a-card class="statistics-card" title="学习统计">
          <div class="statistics-grid">
            <div class="stat-item">
              <div class="stat-number">
                {{ statistics.totalSubmissions || 0 }}
              </div>
              <div class="stat-label">提交题目总数</div>
            </div>
            <div class="stat-item">
              <div class="stat-number">
                {{ statistics.totalQuestions || 0 }}
              </div>
              <div class="stat-label">做题数量</div>
            </div>
            <div class="stat-item">
              <div class="stat-number">
                {{ statistics.acceptedSubmissions || 0 }}
              </div>
              <div class="stat-label">正确提交数</div>
            </div>
            <div class="stat-item">
              <div class="stat-number">
                {{ formatRate(statistics.acceptanceRate) }}%
              </div>
              <div class="stat-label">正确率</div>
            </div>
            <div class="stat-item">
              <div class="stat-number">
                {{ statistics.createdQuestions || 0 }}
              </div>
              <div class="stat-label">创建题目数</div>
            </div>
          </div>
        </a-card>

        <!-- 操作按钮 -->
        <div class="actions">
          <a-button type="primary" @click="goBack">
            <icon-arrow-left />
            返回
          </a-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from "vue";
import { useRouter } from "vue-router";
import { useStore } from "vuex";
import { IconUser, IconArrowLeft } from "@arco-design/web-vue/es/icon";
import message from "@arco-design/web-vue/es/message";
import moment from "moment";
import { UserControllerService, UserStatisticsVO } from "../../../generated";

const router = useRouter();
const store = useStore();

// 用户信息
const userInfo = ref({
  userName: "",
  userRole: "",
  userAvatar: "",
  createTime: "",
});

// 统计信息
const statistics = ref<UserStatisticsVO>({
  totalSubmissions: 0,
  totalQuestions: 0,
  acceptedSubmissions: 0,
  acceptanceRate: 0,
  createdQuestions: 0,
});

/**
 * 获取角色文本
 */
const getRoleText = (role: string) => {
  switch (role) {
    case "admin":
      return "管理员";
    case "user":
      return "普通用户";
    default:
      return "用户";
  }
};

/**
 * 格式化日期
 */
const formatDate = (date: string) => {
  if (!date) return "未知";
  return moment(date).format("YYYY年MM月DD日");
};

/**
 * 格式化正确率
 */
const formatRate = (rate: number | undefined) => {
  if (!rate) return "0.0";
  return (rate * 100).toFixed(1);
};

/**
 * 加载用户统计信息
 */
const loadUserStatistics = async () => {
  try {
    const res = await UserControllerService.getUserStatisticsUsingGet();
    if (res.code === 0 && res.data) {
      statistics.value = res.data;
    } else {
      message.error("获取统计信息失败：" + (res.message || "未知错误"));
    }
  } catch (error) {
    console.error("获取统计信息失败:", error);
    message.error("获取统计信息失败");
  }
};

/**
 * 加载用户信息
 */
const loadUserInfo = () => {
  const loginUser = store.state.user.loginUser;
  if (loginUser) {
    userInfo.value = {
      userName: loginUser.userName || "用户",
      userRole: loginUser.userRole || "user",
      userAvatar: loginUser.userAvatar || "",
      createTime: loginUser.createTime || "",
    };
  }
};

/**
 * 返回上一页
 */
const goBack = () => {
  router.go(-1);
};

onMounted(() => {
  loadUserInfo();
  loadUserStatistics();
});
</script>

<style scoped>
#userProfileView {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
}

.profile-container {
  max-width: 800px;
  margin: 0 auto;
}

.profile-header {
  text-align: center;
  margin-bottom: 32px;
  color: white;
}

.title {
  font-size: 32px;
  font-weight: 600;
  margin: 0 0 8px 0;
}

.subtitle {
  font-size: 16px;
  margin: 0;
  opacity: 0.9;
}

.profile-content {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.user-info-card,
.statistics-card {
  background: white;
  border-radius: 12px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
}

.user-info {
  display: flex;
  align-items: center;
  gap: 24px;
}

.avatar-section {
  flex-shrink: 0;
}

.info-section h3 {
  font-size: 24px;
  font-weight: 600;
  margin: 0 0 8px 0;
  color: #1d2129;
}

.user-role {
  font-size: 14px;
  color: #86909c;
  margin: 0 0 4px 0;
}

.join-date {
  font-size: 14px;
  color: #86909c;
  margin: 0;
}

.statistics-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
  gap: 24px;
}

.stat-item {
  text-align: center;
  padding: 20px;
  background: #f7f8fa;
  border-radius: 8px;
}

.stat-number {
  font-size: 32px;
  font-weight: 600;
  color: #165dff;
  margin-bottom: 8px;
}

.stat-label {
  font-size: 14px;
  color: #86909c;
}

.actions {
  display: flex;
  justify-content: center;
  margin-top: 24px;
}

@media (max-width: 768px) {
  .statistics-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .user-info {
    flex-direction: column;
    text-align: center;
  }
}
</style>
