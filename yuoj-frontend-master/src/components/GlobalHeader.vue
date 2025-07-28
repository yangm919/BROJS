<template>
  <a-row id="globalHeader" align="center" :wrap="false">
    <a-col flex="auto">
      <a-menu
        mode="horizontal"
        :selected-keys="selectedKeys"
        @menu-item-click="doMenuClick"
      >
        <a-menu-item
          key="0"
          :style="{ padding: 0, marginRight: '38px' }"
          disabled
        >
          <div class="title-bar">
            <img class="logo" src="../assets/bingrui-logo.svg" />
            <div class="title">鱼 OJ</div>
          </div>
        </a-menu-item>
        <a-menu-item v-for="item in visibleRoutes" :key="item.path">
          {{ item.name }}
        </a-menu-item>
      </a-menu>
    </a-col>
    <a-col flex="200px">
      <div class="user-info">
        <a-dropdown v-if="isLoggedIn">
          <a-button type="text">
            {{ store.state.user?.loginUser?.userName ?? "用户" }}
            <icon-down />
          </a-button>
          <template #content>
            <a-doption @click="goToProfile">
              <icon-user />
              个人信息
            </a-doption>
            <a-doption @click="handleLogout">
              <icon-export />
              退出登录
            </a-doption>
          </template>
        </a-dropdown>
        <a-button v-else type="text" @click="goToLogin">
          <icon-user />
          登录
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

// 展示在菜单的路由数组
const visibleRoutes = computed(() => {
  return routes.filter((item) => {
    if (item.meta?.hideInMenu) {
      return false;
    }
    // 根据权限过滤菜单
    if (
      !checkAccess(store.state.user.loginUser, item?.meta?.access as string)
    ) {
      return false;
    }
    return true;
  });
});

// 判断是否已登录
const isLoggedIn = computed(() => {
  const loginUser = store.state.user.loginUser;
  return (
    loginUser &&
    loginUser.userRole &&
    loginUser.userRole !== ACCESS_ENUM.NOT_LOGIN
  );
});

// 默认主页
const selectedKeys = ref(["/"]);

// 路由跳转后，更新选中的菜单项
router.afterEach((to) => {
  selectedKeys.value = [to.path];
});

const doMenuClick = (key: string) => {
  router.push({
    path: key,
  });
};

/**
 * 跳转到登录页面
 */
const goToLogin = () => {
  router.push("/user/login");
};

/**
 * 跳转到个人信息页面
 */
const goToProfile = () => {
  router.push("/profile");
};

/**
 * 处理登出
 */
const handleLogout = async () => {
  try {
    // 这里可以调用后端的登出接口
    // const res = await UserControllerService.userLogoutUsingPost();

    // 清除本地用户状态
    store.commit("user/updateUser", {
      userName: "未登录",
      userRole: ACCESS_ENUM.NOT_LOGIN,
    });

    message.success("已退出登录");

    // 跳转到登录页面
    router.push("/user/login");
  } catch (error) {
    console.error("登出失败:", error);
    message.error("登出失败");
  }
};

// 移除自动登录的测试代码
// setTimeout(() => {
//   store.dispatch("user/getLoginUser", {
//     userName: "鱼皮管理员",
//     userRole: ACCESS_ENUM.ADMIN,
//   });
// }, 3000);
</script>

<style scoped>
.title-bar {
  display: flex;
  align-items: center;
}

.title {
  color: #444;
  margin-left: 16px;
}

.logo {
  height: 48px;
}

.user-info {
  display: flex;
  justify-content: flex-end;
  align-items: center;
}
</style>
