<template>
  <div class="no-auth-container">
    <div class="no-auth-content">
      <div class="icon">
        <icon-exclamation-circle-fill />
      </div>
      <h2 class="title">Access Denied</h2>
      <p class="description">
        Sorry, you don't have permission to access this page
      </p>
      <div class="actions">
        <a-button type="primary" @click="goHome">
          <icon-home />
          Back to Home
        </a-button>
        <a-button @click="goBack">
          <icon-arrow-left />
          Go Back
        </a-button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import {
  IconExclamationCircleFill,
  IconHome,
  IconArrowLeft,
} from "@arco-design/web-vue/es/icon";
import { useRouter } from "vue-router";

const router = useRouter();

const goHome = () => {
  router.push("/");
};

const goBack = () => {
  router.go(-1);
};
</script>

<style scoped>
.no-auth-container {
  min-height: 60vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
}

.no-auth-content {
  text-align: center;
  max-width: 400px;
}

.icon {
  font-size: 64px;
  color: #f53f3f;
  margin-bottom: 24px;
}

.title {
  font-size: 24px;
  font-weight: 600;
  color: #1d2129;
  margin: 0 0 16px 0;
}

.description {
  font-size: 16px;
  color: #86909c;
  margin: 0 0 32px 0;
}

.actions {
  display: flex;
  gap: 16px;
  justify-content: center;
}
</style>
