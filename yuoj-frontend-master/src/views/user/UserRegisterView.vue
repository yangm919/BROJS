<template>
  <div id="userRegisterView">
    <div class="register-container">
      <div class="register-card">
        <div class="register-header">
          <img
            class="logo"
            src="../../assets/bingrui-logo.svg"
            alt="Bingrui Logo"
          />
          <h2 class="title">用户注册</h2>
          <p class="subtitle">创建您的鱼 OJ 账号</p>
        </div>

        <a-form
          class="register-form"
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
          <a-form-item field="checkPassword" label="确认密码">
            <a-input-password
              v-model="form.checkPassword"
              placeholder="请再次输入密码"
            />
          </a-form-item>
          <a-form-item>
            <a-button type="primary" html-type="submit" style="width: 120px">
              注册
            </a-button>
          </a-form-item>
        </a-form>

        <div class="register-footer">
          <span>已有账号？</span>
          <a-link @click="goToLogin">立即登录</a-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { reactive } from "vue";
import { UserControllerService, UserRegisterRequest } from "../../../generated";
import message from "@arco-design/web-vue/es/message";
import { useRouter } from "vue-router";

/**
 * 表单信息
 */
const form = reactive({
  userAccount: "",
  userPassword: "",
  checkPassword: "",
} as UserRegisterRequest);

const router = useRouter();

/**
 * 提交表单
 */
const handleSubmit = async () => {
  // 验证密码
  if (form.userPassword !== form.checkPassword) {
    message.error("两次输入的密码不一致");
    return;
  }

  // 验证密码长度
  if (form.userPassword && form.userPassword.length < 8) {
    message.error("密码长度不能少于8位");
    return;
  }

  try {
    const res = await UserControllerService.userRegisterUsingPost({
      userAccount: form.userAccount,
      userPassword: form.userPassword,
      checkPassword: form.checkPassword,
    });

    if (res.code === 0) {
      message.success("注册成功，请登录");
      router.push("/user/login");
    } else {
      message.error("注册失败：" + res.message);
    }
  } catch (error: any) {
    console.error("注册错误:", error);
    message.error("注册失败，请检查网络连接");
  }
};

/**
 * 跳转到登录页面
 */
const goToLogin = () => {
  router.push("/user/login");
};
</script>

<style scoped>
#userRegisterView {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
}

.register-container {
  width: 100%;
  max-width: 400px;
}

.register-card {
  background: white;
  border-radius: 12px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  padding: 40px;
}

.register-header {
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

.register-form {
  width: 100%;
}

.register-footer {
  text-align: center;
  margin-top: 16px;
  color: #86909c;
  font-size: 14px;
}

.register-footer a {
  margin-left: 4px;
}
</style>
