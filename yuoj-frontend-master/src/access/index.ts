import router from "@/router";
import store from "@/store";
import ACCESS_ENUM from "@/access/accessEnum";
import checkAccess from "@/access/checkAccess";

router.beforeEach(async (to, from, next) => {
  console.log("当前路由:", to.path);
  console.log("登录用户信息:", store.state.user.loginUser);

  let loginUser = store.state.user.loginUser;

  // 如果用户信息不完整或未登录，尝试获取用户信息
  if (
    !loginUser ||
    !loginUser.userRole ||
    loginUser.userRole === ACCESS_ENUM.NOT_LOGIN
  ) {
    try {
      // 加 await 是为了等用户登录成功之后，再执行后续的代码
      await store.dispatch("user/getLoginUser");
      loginUser = store.state.user.loginUser;
    } catch (error) {
      console.error("获取用户信息失败:", error);
      // 获取用户信息失败，设置为未登录状态
      loginUser = {
        userName: "未登录",
        userRole: ACCESS_ENUM.NOT_LOGIN,
      };
    }
  }

  const needAccess = (to.meta?.access as string) ?? ACCESS_ENUM.NOT_LOGIN;
  console.log("需要权限:", needAccess);
  console.log("用户权限:", loginUser.userRole);

  // 要跳转的页面必须要登录
  if (needAccess !== ACCESS_ENUM.NOT_LOGIN) {
    // 如果没登录，跳转到登录页面
    if (
      !loginUser ||
      !loginUser.userRole ||
      loginUser.userRole === ACCESS_ENUM.NOT_LOGIN
    ) {
      console.log("用户未登录，跳转到登录页面");
      next(`/user/login?redirect=${encodeURIComponent(to.fullPath)}`);
      return;
    }

    // 如果已经登录了，但是权限不足，那么跳转到无权限页面
    if (!checkAccess(loginUser, needAccess)) {
      console.log("用户权限不足，跳转到无权限页面");
      next("/noAuth");
      return;
    }
  }

  console.log("权限检查通过，允许访问");
  next();
});
