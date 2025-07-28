# 鱼 OJ 前端项目

## 项目简介

这是一个基于 Vue 3 + TypeScript 的在线编程评测系统前端项目。

## 技术栈

- Vue 3
- TypeScript
- Vue Router 4
- Vuex 4
- Arco Design Vue
- Axios

## 鉴权机制

### 权限级别

项目定义了三种权限级别：

1. **NOT_LOGIN** - 无需登录即可访问
2. **USER** - 需要用户登录
3. **ADMIN** - 需要管理员权限

### 需要鉴权的页面

以下页面需要用户登录才能访问：

- `/view/question/:id` - 在线做题
- `/add/question` - 创建题目
- `/update/question` - 更新题目
- `/question_submit` - 浏览题目提交

以下页面需要管理员权限：

- `/manage/question/` - 管理题目

### 鉴权流程

1. 用户访问需要鉴权的页面时，系统会检查用户登录状态
2. 如果未登录，自动跳转到登录页面 `/user/login`
3. 登录成功后，自动跳转回原页面
4. 如果权限不足，跳转到无权限页面 `/noAuth`

### 使用方法

1. 启动项目：
```bash
npm install
npm run serve
```

2. 访问需要鉴权的页面时，系统会自动处理登录跳转

3. 登录后可以正常访问所有有权限的页面

## 项目结构

```
src/
├── access/           # 权限控制
│   ├── accessEnum.ts # 权限枚举
│   ├── checkAccess.ts # 权限检查
│   └── index.ts      # 路由守卫
├── components/       # 公共组件
├── layouts/          # 布局组件
├── router/           # 路由配置
├── store/            # 状态管理
├── views/            # 页面组件
└── generated/        # 自动生成的API类型
```

## 开发说明

- 添加新页面时，在 `router/routes.ts` 中配置路由和权限
- 权限检查在 `access/index.ts` 中统一处理
- 用户状态管理在 `store/user.ts` 中
- 登录页面在 `views/user/UserLoginView.vue`
