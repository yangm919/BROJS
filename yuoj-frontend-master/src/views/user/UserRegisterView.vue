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
          <h2 class="title">User Registration</h2>
          <p class="subtitle">Create your Br OJ account</p>
        </div>

        <a-form
          class="register-form"
          label-align="left"
          auto-label-width
          :model="form"
          @submit="handleSubmit"
        >
          <a-form-item field="userAccount" label="Account">
            <a-input
              v-model="form.userAccount"
              placeholder="Please enter your account"
            />
          </a-form-item>
          <a-form-item
            field="userPassword"
            tooltip="Password must be at least 8 characters"
            label="Password"
          >
            <a-input-password
              v-model="form.userPassword"
              placeholder="Please enter your password"
            />
          </a-form-item>
          <a-form-item field="checkPassword" label="Confirm Password">
            <a-input-password
              v-model="form.checkPassword"
              placeholder="Please enter your password again"
            />
          </a-form-item>
          <a-form-item>
            <a-button type="primary" html-type="submit" style="width: 120px">
              Register
            </a-button>
          </a-form-item>
        </a-form>

        <div class="register-footer">
          <span>Already have an account?</span>
          <a-link @click="goToLogin">Login Now</a-link>
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
 * Form information
 */
const form = reactive({
  userAccount: "",
  userPassword: "",
  checkPassword: "",
} as UserRegisterRequest);

const router = useRouter();

/**
 * Submit form
 */
const handleSubmit = async () => {
  // Validate password
  if (form.userPassword !== form.checkPassword) {
    message.error("The two passwords entered are inconsistent");
    return;
  }

  // Validate password length
  if (form.userPassword && form.userPassword.length < 8) {
    message.error("Password length cannot be less than 8 characters");
    return;
  }

  try {
    const res = await UserControllerService.userRegisterUsingPost({
      userAccount: form.userAccount,
      userPassword: form.userPassword,
      checkPassword: form.checkPassword,
    });

    if (res.code === 0) {
      message.success("Registration successful, please login");
      router.push("/user/login");
    } else {
      message.error("Registration failed: " + res.message);
    }
  } catch (error: any) {
    console.error("Registration error:", error);
    message.error("Registration failed, please check network connection");
  }
};

/**
 * Navigate to login page
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
