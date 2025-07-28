// initial state
import { StoreOptions } from "vuex";
import ACCESS_ENUM from "@/access/accessEnum";
import { UserControllerService } from "../../generated";

export default {
  namespaced: true,
  state: () => ({
    loginUser: {
      userName: "未登录",
      userRole: ACCESS_ENUM.NOT_LOGIN,
    },
  }),
  actions: {
    async getLoginUser({ commit, state }, payload) {
      // 从远程请求获取登录信息
      const res = await UserControllerService.getLoginUserUsingGet();
      if (res.code === 0) {
        commit("updateUser", res.data);
      } else {
        commit("updateUser", {
          userName: "未登录",
          userRole: ACCESS_ENUM.NOT_LOGIN,
        });
      }
    },

    /**
     * 登出用户
     */
    async logoutUser({ commit }) {
      try {
        // 这里可以调用后端的登出接口
        // const res = await UserControllerService.userLogoutUsingPost();

        // 清除用户状态
        commit("updateUser", {
          userName: "未登录",
          userRole: ACCESS_ENUM.NOT_LOGIN,
        });
      } catch (error) {
        console.error("登出失败:", error);
        // 即使后端调用失败，也要清除本地状态
        commit("updateUser", {
          userName: "未登录",
          userRole: ACCESS_ENUM.NOT_LOGIN,
        });
      }
    },
  },
  mutations: {
    updateUser(state, payload) {
      state.loginUser = payload;
    },
  },
} as StoreOptions<any>;
