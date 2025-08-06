import router from "@/router";
import store from "@/store";
import ACCESS_ENUM from "@/access/accessEnum";
import checkAccess from "@/access/checkAccess";

router.beforeEach(async (to, from, next) => {
  console.log("Current route:", to.path);
  console.log("Login user info:", store.state.user.loginUser);

  let loginUser = store.state.user.loginUser;

  // If user info is incomplete or not logged in, try to get user info
  if (
    !loginUser ||
    !loginUser.userRole ||
    loginUser.userRole === ACCESS_ENUM.NOT_LOGIN
  ) {
    try {
      // Add await to wait for user login success before executing subsequent code
      await store.dispatch("user/getLoginUser");
      loginUser = store.state.user.loginUser;
    } catch (error) {
      console.error("Failed to get user info:", error);
      // Failed to get user info, set to not logged in status
      loginUser = {
        userName: "Not Logged In",
        userRole: ACCESS_ENUM.NOT_LOGIN,
      };
    }
  }

  const needAccess = (to.meta?.access as string) ?? ACCESS_ENUM.NOT_LOGIN;
  console.log("Required permission:", needAccess);
  console.log("User permission:", loginUser.userRole);

  // Pages to jump to must be logged in
  if (needAccess !== ACCESS_ENUM.NOT_LOGIN) {
    // If not logged in, jump to login page
    if (
      !loginUser ||
      !loginUser.userRole ||
      loginUser.userRole === ACCESS_ENUM.NOT_LOGIN
    ) {
      console.log("User not logged in, redirecting to login page");
      next(`/user/login?redirect=${encodeURIComponent(to.fullPath)}`);
      return;
    }

    // If already logged in but insufficient permissions, jump to no permission page
    if (!checkAccess(loginUser, needAccess)) {
      console.log(
        "Insufficient user permissions, redirecting to no permission page"
      );
      next("/noAuth");
      return;
    }
  }

  console.log("Permission check passed, access allowed");
  next();
});
