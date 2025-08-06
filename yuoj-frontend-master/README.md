# Br OJ Frontend Project

## Project Introduction

This is a Vue 3 + TypeScript based online programming evaluation system frontend project.

## Technology Stack

- Vue 3
- TypeScript
- Vue Router 4
- Vuex 4
- Arco Design Vue
- Axios

## Authentication Mechanism

### Permission Levels

The project defines three permission levels:

1. **NOT_LOGIN** - Accessible without login
2. **USER** - Requires user login
3. **ADMIN** - Requires administrator privileges

### Pages Requiring Authentication

The following pages require user login to access:

- `/view/question/:id` - Online problem solving
- `/add/question` - Create question
- `/update/question` - Update question
- `/question_submit` - Browse question submissions

The following pages require administrator privileges:

- `/manage/question/` - Manage questions

### Authentication Flow

1. When users access pages requiring authentication, the system checks user login status
2. If not logged in, automatically redirect to login page `/user/login`
3. After successful login, automatically redirect back to the original page
4. If insufficient permissions, redirect to no permission page `/noAuth`

### Usage Instructions

1. Start the project:
```bash
npm install
npm run serve
```

2. When accessing pages requiring authentication, the system will automatically handle login redirection

3. After login, you can normally access all pages with permissions

## Project Structure

```
src/
├── access/           # Permission control
│   ├── accessEnum.ts # Permission enum
│   ├── checkAccess.ts # Permission check
│   └── index.ts      # Route guard
├── components/       # Public components
├── layouts/          # Layout components
├── router/           # Route configuration
├── store/            # State management
├── views/            # Page components
└── generated/        # Auto-generated API types
```

## Development Notes

- When adding new pages, configure routes and permissions in `router/routes.ts`
- Permission checks are handled uniformly in `access/index.ts`
- User state management is in `store/user.ts`
- Login page is in `views/user/UserLoginView.vue`
