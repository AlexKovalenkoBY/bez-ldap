// src/router/index.js
import { createRouter, createWebHistory } from 'vue-router';
import LoginForm from '../components/LoginForm.vue';
import DashboardComponent from '../components/DashboardComponent.vue';
import FilesComponent from '../components/FilesComponent.vue';

const routes = [
  {
    path: '/',
    name: 'Files',
    component: FilesComponent,
    meta: { requiresAuth: true },
  },
  {
    path: '/login',
    name: 'Login',
    component: LoginForm,
  },
  // {
  //   path: '/dashboard/:username',
  //   name: 'Dashboard',
  //   component: DashboardComponent,
  //   meta: { requiresAuth: true },
  // },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

router.beforeEach((to, from, next) => {
  const accessToken = localStorage.getItem('accessToken');

  if (to.matched.some((record) => record.meta.requiresAuth)) {
    if (!accessToken) {
      next({ name: 'Login' });
    } else {
      next();
    }
  } else {
    next();
  }
});

export default router;