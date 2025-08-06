<template>
  <div id="userProfileView">
    <div class="profile-container">
      <div class="profile-header">
        <h2 class="title">Personal Information</h2>
        <p class="subtitle">View your learning statistics and achievements</p>
      </div>

      <div class="profile-content">
        <!-- User basic information -->
        <a-card class="user-info-card" title="Basic Information">
          <div class="user-info">
            <div class="avatar-section">
              <a-avatar :size="80" :src="userInfo.userAvatar">
                <icon-user />
              </a-avatar>
            </div>
            <div class="info-section">
              <h3>{{ userInfo.userName || "User" }}</h3>
              <p class="user-role">{{ getRoleText(userInfo.userRole) }}</p>
              <p class="join-date">
                Join time: {{ formatDate(userInfo.createTime) }}
              </p>
            </div>
          </div>
        </a-card>

        <!-- Statistics information -->
        <a-card class="statistics-card" title="Learning Statistics">
          <div class="statistics-grid">
            <div class="stat-item">
              <div class="stat-number">
                {{ statistics.totalSubmissions || 0 }}
              </div>
              <div class="stat-label">Total Submissions</div>
            </div>
            <div class="stat-item">
              <div class="stat-number">
                {{ statistics.totalQuestions || 0 }}
              </div>
              <div class="stat-label">Questions Solved</div>
            </div>
            <div class="stat-item">
              <div class="stat-number">
                {{ statistics.acceptedSubmissions || 0 }}
              </div>
              <div class="stat-label">Accepted Submissions</div>
            </div>
            <div class="stat-item">
              <div class="stat-number">
                {{ formatRate(statistics.acceptanceRate) }}%
              </div>
              <div class="stat-label">Acceptance Rate</div>
            </div>
            <div class="stat-item">
              <div class="stat-number">
                {{ statistics.createdQuestions || 0 }}
              </div>
              <div class="stat-label">Questions Created</div>
            </div>
          </div>
        </a-card>

        <!-- Action buttons -->
        <div class="actions">
          <a-button type="primary" @click="goBack">
            <icon-arrow-left />
            Back
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

// User information
const userInfo = ref({
  userName: "",
  userRole: "",
  userAvatar: "",
  createTime: "",
});

// Statistics information
const statistics = ref<UserStatisticsVO>({
  totalSubmissions: 0,
  totalQuestions: 0,
  acceptedSubmissions: 0,
  acceptanceRate: 0,
  createdQuestions: 0,
});

/**
 * Get role text
 */
const getRoleText = (role: string) => {
  switch (role) {
    case "admin":
      return "Administrator";
    case "user":
      return "Regular User";
    default:
      return "User";
  }
};

/**
 * Format date
 */
const formatDate = (date: string) => {
  if (!date) return "Unknown";
  return moment(date).format("YYYY-MM-DD");
};

/**
 * Format acceptance rate
 */
const formatRate = (rate: number | undefined) => {
  if (!rate) return "0.0";
  return (rate * 100).toFixed(1);
};

/**
 * Load user statistics
 */
const loadUserStatistics = async () => {
  try {
    const res = await UserControllerService.getUserStatisticsUsingGet();
    if (res.code === 0 && res.data) {
      statistics.value = res.data;
    } else {
      message.error(
        "Failed to get statistics: " + (res.message || "Unknown error")
      );
    }
  } catch (error) {
    console.error("Failed to get statistics:", error);
    message.error("Failed to get statistics");
  }
};

/**
 * Load user information
 */
const loadUserInfo = () => {
  const loginUser = store.state.user.loginUser;
  if (loginUser) {
    userInfo.value = {
      userName: loginUser.userName || "User",
      userRole: loginUser.userRole || "user",
      userAvatar: loginUser.userAvatar || "",
      createTime: loginUser.createTime || "",
    };
  }
};

/**
 * Go back to previous page
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
