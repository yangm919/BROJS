<template>
  <div id="userLoginView">
    <div class="login-container">
      <div class="login-card">
        <div class="login-header">
          <img
            class="logo"
            src="../../assets/bingrui-logo.svg"
            alt="Bingrui Logo"
          />
          <h2 class="title">用户登录</h2>
          <p class="subtitle">欢迎使用鱼 OJ 在线编程评测系统</p>
        </div>

        <a-form
          class="login-form"
          label-align="left"
          auto-label-width
          :model="form"
          @submit="handleSubmit"
        >
          <a-form-item field="userAccount" label="账号">
            <a-input v-model="form.userAccount" placeholder="请输入账号" />
          </a-form-item>
          <a-form-item
            field="userPassword"
            tooltip="密码不少于 8 位"
            label="密码"
          >
            <a-input-password
              v-model="form.userPassword"
              placeholder="请输入密码"
            />
          </a-form-item>
          <a-form-item>
            <a-button type="primary" html-type="submit" style="width: 120px">
              登录
            </a-button>
          </a-form-item>
        </a-form>

        <div class="login-footer">
          <span>还没有账号？</span>
          <a-link @click="goToRegister">立即注册</a-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { reactive } from "vue";
import { UserControllerService, UserLoginRequest } from "../../../generated";
import message from "@arco-design/web-vue/es/message";
import { useRouter, useRoute } from "vue-router";
import { useStore } from "vuex";

/**
 * 表单信息
 */
const form = reactive({
  userAccount: "",
  userPassword: "",
} as UserLoginRequest);

const router = useRouter();
const route = useRoute();
const store = useStore();

/**
 * 提交表单
 */
const handleSubmit = async () => {
  const res = await UserControllerService.userLoginUsingPost(form);
  // 登录成功，跳转到主页或之前的页面
  if (res.code === 0) {
    await store.dispatch("user/getLoginUser");
    // 获取重定向地址，如果没有则跳转到主页
    const redirect = route.query.redirect as string;
    router.push({
      path: redirect || "/",
      replace: true,
    });
  } else {
    message.error("登录失败，" + res.message);
  }
};

/**
 * 跳转到注册页面
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
}

.login-container {
  width: 100%;
  max-width: 400px;
}

.login-card {
  background: white;
  border-radius: 12px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  padding: 40px;
}

.login-header {
  text-align: center;
  margin-bottom: 32px;
}

.logo {
  height: 64px;
  margin-bottom: 16px;
}

.title {
  color: #1d2129;
  font-size: 24px;
  font-weight: 600;
  margin: 0 0 8px 0;
}

.subtitle {
  color: #86909c;
  font-size: 14px;
  margin: 0;
}

.login-form {
  width: 100%;
}

.login-footer {
  text-align: center;
  margin-top: 16px;
  color: #86909c;
  font-size: 14px;
}

.login-footer a {
  margin-left: 4px;
}
</style>
