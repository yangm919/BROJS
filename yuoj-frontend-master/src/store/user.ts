// initial state
import { StoreOptions } from "vuex";
import ACCESS_ENUM from "@/access/accessEnum";
import { UserControllerService } from "../../generated";

export default {
  namespaced: true,
  state: () => ({
    loginUser: {
      userName: "Not Logged In",
      userRole: ACCESS_ENUM.NOT_LOGIN,
    },
  }),
  actions: {
    async getLoginUser({ commit, state }, payload) {
      // Get login information from remote request
      const res = await UserControllerService.getLoginUserUsingGet();
      if (res.code === 0) {
        commit("updateUser", res.data);
      } else {
        commit("updateUser", {
          userName: "Not Logged In",
          userRole: ACCESS_ENUM.NOT_LOGIN,
        });
      }
    },

    /**
     * Logout user
     */
    async logoutUser({ commit }) {
      try {
        // Here you can call the backend logout interface
        // const res = await UserControllerService.userLogoutUsingPost();

        // Clear user state
        commit("updateUser", {
          userName: "Not Logged In",
          userRole: ACCESS_ENUM.NOT_LOGIN,
        });
      } catch (error) {
        console.error("Logout failed:", error);
        // Even if backend call fails, clear local state
        commit("updateUser", {
          userName: "Not Logged In",
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
