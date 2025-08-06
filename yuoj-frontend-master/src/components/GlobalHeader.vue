<template>
  <a-row id="globalHeader" align="center" :wrap="false">
    <a-col flex="auto">
      <a-menu
        mode="horizontal"
        :selected-keys="selectedKeys"
        @menu-item-click="doMenuClick"
        class="main-menu"
      >
        <a-menu-item
          key="0"
          :style="{ padding: 0, marginRight: '38px' }"
          disabled
        >
          <div class="title-bar">
            <div class="logo-container">
              <img class="logo" src="../assets/bingrui-logo.svg" />
              <div class="logo-glow"></div>
            </div>
            <div class="title">
              <span class="title-main">Br OJ</span>
              <span class="title-sub">Online Judge</span>
            </div>
          </div>
        </a-menu-item>
        <a-menu-item
          v-for="item in visibleRoutes"
          :key="item.path"
          class="menu-item"
        >
          <div class="menu-item-content">
            <span class="menu-icon" v-if="item.meta?.icon">
              <component :is="item.meta.icon" />
            </span>
            {{ item.name }}
          </div>
        </a-menu-item>
      </a-menu>
    </a-col>
    <a-col flex="200px">
      <div class="user-info">
        <a-dropdown v-if="isLoggedIn" trigger="hover">
          <a-button type="text" class="user-button">
            <div class="user-avatar">
              <icon-user />
            </div>
            <span class="user-name">{{
              store.state.user?.loginUser?.userName ?? "User"
            }}</span>
            <icon-down class="dropdown-icon" />
          </a-button>
          <template #content>
            <a-doption @click="goToProfile" class="dropdown-item">
              <icon-user />
              <span>Personal Info</span>
            </a-doption>
            <a-doption @click="handleLogout" class="dropdown-item">
              <icon-export />
              <span>Logout</span>
            </a-doption>
          </template>
        </a-dropdown>
        <a-button v-else type="primary" @click="goToLogin" class="login-button">
          <icon-user />
          <span>Login</span>
        </a-button>
      </div>
    </a-col>
  </a-row>
</template>

<script setup lang="ts">
import { routes } from "../router/routes";
import { useRouter } from "vue-router";
import { computed, ref } from "vue";
import { useStore } from "vuex";
import checkAccess from "@/access/checkAccess";
import ACCESS_ENUM from "@/access/accessEnum";
import { IconDown, IconUser, IconExport } from "@arco-design/web-vue/es/icon";
import message from "@arco-design/web-vue/es/message";

const router = useRouter();
const store = useStore();

// Routes array displayed in menu
const visibleRoutes = computed(() => {
  return routes.filter((item) => {
    if (item.meta?.hideInMenu) {
      return false;
    }
    // Filter menu based on permissions
    if (
      !checkAccess(store.state.user.loginUser, item?.meta?.access as string)
    ) {
      return false;
    }
    return true;
  });
});

// Check if already logged in
const isLoggedIn = computed(() => {
  const loginUser = store.state.user.loginUser;
  return (
    loginUser &&
    loginUser.userRole &&
    loginUser.userRole !== ACCESS_ENUM.NOT_LOGIN
  );
});

// Default homepage
const selectedKeys = ref(["/"]);

// After route navigation, update selected menu item
router.afterEach((to) => {
  selectedKeys.value = [to.path];
});

const doMenuClick = (key: string) => {
  router.push({
    path: key,
  });
};

/**
 * Navigate to login page
 */
const goToLogin = () => {
  router.push("/user/login");
};

/**
 * Navigate to personal info page
 */
const goToProfile = () => {
  router.push("/profile");
};

/**
 * Handle logout
 */
const handleLogout = async () => {
  try {
    // Here you can call the backend logout interface
    // const res = await UserControllerService.userLogoutUsingPost();

    // Clear local user state
    store.commit("user/updateUser", {
      userName: "Not Logged In",
      userRole: ACCESS_ENUM.NOT_LOGIN,
    });

    message.success("Logged out successfully");

    // Navigate to login page
    router.push("/user/login");
  } catch (error) {
    console.error("Logout failed:", error);
    message.error("Logout failed");
  }
};

// Remove auto-login test code
// setTimeout(() => {
//   store.dispatch("user/getLoginUser", {
//     userName: "Br Admin",
//     userRole: ACCESS_ENUM.ADMIN,
//   });
// }, 3000);
</script>

<style scoped>
#globalHeader {
  padding: 0 24px;
  height: 64px;
}

.title-bar {
  display: flex;
  align-items: center;
  gap: 12px;
}

.logo-container {
  position: relative;
  display: flex;
  align-items: center;
}

.logo {
  height: 40px;
  width: 40px;
  transition: transform 0.3s ease;
}

.logo:hover {
  transform: scale(1.1);
}

.logo-glow {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 50px;
  height: 50px;
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
  display: flex;
  flex-direction: column;
  align-items: flex-start;
}

.title-main {
  color: #1d2129;
  font-size: 20px;
  font-weight: 700;
  line-height: 1.2;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.title-sub {
  color: #86909c;
  font-size: 12px;
  font-weight: 400;
  line-height: 1;
}

.main-menu {
  background: transparent;
  border: none;
}

.menu-item {
  margin: 0 8px;
  border-radius: 8px;
  transition: all 0.3s ease;
}

.menu-item:hover {
  background: rgba(102, 126, 234, 0.1);
  transform: translateY(-2px);
}

.menu-item-content {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 16px;
  border-radius: 8px;
  transition: all 0.3s ease;
}

.menu-icon {
  font-size: 16px;
}

.user-info {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  gap: 12px;
}

.user-button {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 16px;
  border-radius: 24px;
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.2);
  transition: all 0.3s ease;
}

.user-button:hover {
  background: rgba(255, 255, 255, 1);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.user-avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 16px;
}

.user-name {
  font-weight: 500;
  color: #1d2129;
  max-width: 100px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.dropdown-icon {
  transition: transform 0.3s ease;
}

.user-button:hover .dropdown-icon {
  transform: rotate(180deg);
}

.login-button {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 20px;
  border-radius: 24px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  color: white;
  font-weight: 500;
  transition: all 0.3s ease;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

.login-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.4);
}

.dropdown-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 16px;
  transition: all 0.3s ease;
}

.dropdown-item:hover {
  background: rgba(102, 126, 234, 0.1);
}

/* 响应式设计 */
@media (max-width: 768px) {
  #globalHeader {
    padding: 0 16px;
  }

  .title-main {
    font-size: 18px;
  }

  .title-sub {
    font-size: 10px;
  }

  .user-name {
    display: none;
  }

  .login-button span {
    display: none;
  }
}

@media (max-width: 480px) {
  #globalHeader {
    padding: 0 12px;
  }

  .title-main {
    font-size: 16px;
  }

  .logo {
    height: 32px;
    width: 32px;
  }
}
</style>
