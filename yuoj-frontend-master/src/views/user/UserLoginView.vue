<template>
  <div id="userLoginView">
    <div class="login-container">
      <div class="login-card">
        <div class="login-header">
          <div class="logo-container">
            <img
              class="logo"
              src="../../assets/bingrui-logo.svg"
              alt="Bingrui Logo"
            />
            <div class="logo-glow"></div>
          </div>
          <h2 class="title">Welcome Back</h2>
          <p class="subtitle">
            Sign in to access Br OJ Online Programming Evaluation System
          </p>
        </div>

        <a-form
          class="login-form"
          label-align="left"
          auto-label-width
          :model="form"
          @submit="handleSubmit"
        >
          <a-form-item field="userAccount" label="Account">
            <a-input
              v-model="form.userAccount"
              placeholder="Enter your username or email"
              class="form-input"
              size="large"
            >
              <template #prefix>
                <icon-user />
              </template>
            </a-input>
          </a-form-item>

          <a-form-item
            field="userPassword"
            tooltip="Password must be at least 8 characters"
            label="Password"
          >
            <a-input-password
              v-model="form.userPassword"
              placeholder="Enter your password"
              class="form-input"
              size="large"
            >
              <template #prefix>
                <icon-lock />
              </template>
            </a-input-password>
          </a-form-item>

          <a-form-item>
            <a-button
              type="primary"
              html-type="submit"
              class="login-button"
              size="large"
              :loading="loading"
            >
              <icon-right />
              Sign In
            </a-button>
          </a-form-item>
        </a-form>

        <div class="login-footer">
          <span>Don't have an account?</span>
          <a-link @click="goToRegister" class="register-link">
            <icon-user-add />
            Register Now
          </a-link>
        </div>
      </div>
    </div>

    <!-- 背景装饰 -->
    <div class="background-decoration">
      <div class="floating-shape shape-1"></div>
      <div class="floating-shape shape-2"></div>
      <div class="floating-shape shape-3"></div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from "vue";
import { UserControllerService, UserLoginRequest } from "../../../generated";
import message from "@arco-design/web-vue/es/message";
import { useRouter, useRoute } from "vue-router";
import { useStore } from "vuex";
import {
  IconUser,
  IconLock,
  IconRight,
  IconUserAdd,
} from "@arco-design/web-vue/es/icon";

/**
 * Form information
 */
const form = reactive({
  userAccount: "",
  userPassword: "",
} as UserLoginRequest);

const router = useRouter();
const route = useRoute();
const store = useStore();
const loading = ref(false);

/**
 * Submit form
 */
const handleSubmit = async () => {
  if (!form.userAccount || !form.userPassword) {
    message.error("Please fill in all fields");
    return;
  }

  loading.value = true;
  try {
    const res = await UserControllerService.userLoginUsingPost(form);
    // Login successful, redirect to home page or previous page
    if (res.code === 0) {
      message.success("Login successful!");
      await store.dispatch("user/getLoginUser");
      // Get redirect address, if not then redirect to home page
      const redirect = route.query.redirect as string;
      router.push({
        path: redirect || "/",
        replace: true,
      });
    } else {
      message.error("Login failed, " + res.message);
    }
  } catch (error) {
    message.error("Login failed, please try again");
  } finally {
    loading.value = false;
  }
};

/**
 * Navigate to registration page
 */
const goToRegister = () => {
  router.push("/user/register");
};
</script>

<style scoped>
#userLoginView {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
  position: relative;
  overflow: hidden;
}

.login-container {
  width: 100%;
  max-width: 420px;
  position: relative;
  z-index: 10;
}

.login-card {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  border-radius: 20px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.1);
  padding: 40px;
  border: 1px solid rgba(255, 255, 255, 0.2);
  transition: all 0.3s ease;
}

.login-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 25px 80px rgba(0, 0, 0, 0.15);
}

.login-header {
  text-align: center;
  margin-bottom: 32px;
}

.logo-container {
  position: relative;
  display: inline-block;
  margin-bottom: 20px;
}

.logo {
  height: 72px;
  width: 72px;
  transition: transform 0.3s ease;
}

.logo-container:hover .logo {
  transform: scale(1.1);
}

.logo-glow {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 90px;
  height: 90px;
  background: radial-gradient(
    circle,
    rgba(102, 126, 234, 0.3) 0%,
    transparent 70%
  );
  border-radius: 50%;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.logo-container:hover .logo-glow {
  opacity: 1;
}

.title {
  color: #1d2129;
  font-size: 28px;
  font-weight: 700;
  margin: 0 0 8px 0;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.subtitle {
  color: #86909c;
  font-size: 16px;
  margin: 0;
  line-height: 1.5;
}

.login-form {
  width: 100%;
}

.form-input {
  border-radius: 12px;
  border: 2px solid #f0f0f0;
  transition: all 0.3s ease;
  background: rgba(255, 255, 255, 0.8);
}

.form-input:hover {
  border-color: #667eea;
}

.form-input:focus {
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.login-button {
  width: 100%;
  height: 48px;
  border-radius: 12px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  font-weight: 600;
  font-size: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  transition: all 0.3s ease;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

.login-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.4);
}

.login-footer {
  text-align: center;
  margin-top: 24px;
  color: #86909c;
  font-size: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.register-link {
  display: flex;
  align-items: center;
  gap: 4px;
  color: #667eea;
  font-weight: 500;
  transition: all 0.3s ease;
}

.register-link:hover {
  color: #764ba2;
  transform: translateX(2px);
}

/* 背景装饰 */
.background-decoration {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
  z-index: 1;
}

.floating-shape {
  position: absolute;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.1);
  animation: float 6s ease-in-out infinite;
}

.shape-1 {
  width: 100px;
  height: 100px;
  top: 20%;
  left: 10%;
  animation-delay: 0s;
}

.shape-2 {
  width: 150px;
  height: 150px;
  top: 60%;
  right: 10%;
  animation-delay: 2s;
}

.shape-3 {
  width: 80px;
  height: 80px;
  bottom: 20%;
  left: 20%;
  animation-delay: 4s;
}

@keyframes float {
  0%,
  100% {
    transform: translateY(0px) rotate(0deg);
  }
  50% {
    transform: translateY(-20px) rotate(180deg);
  }
}

/* 响应式设计 */
@media (max-width: 480px) {
  .login-card {
    padding: 30px 20px;
  }

  .title {
    font-size: 24px;
  }

  .subtitle {
    font-size: 14px;
  }

  .logo {
    height: 60px;
    width: 60px;
  }

  .login-button {
    height: 44px;
    font-size: 14px;
  }
}

/* 输入框焦点效果 */
:deep(.arco-input-wrapper) {
  border-radius: 12px;
}

:deep(.arco-input-password-wrapper) {
  border-radius: 12px;
}

/* 表单标签样式 */
:deep(.arco-form-item-label) {
  font-weight: 500;
  color: #1d2129;
}

/* 错误消息样式 */
:deep(.arco-form-item-error) {
  color: #f53f3f;
  font-size: 12px;
}
</style>
