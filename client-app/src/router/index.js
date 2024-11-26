// src/router/index.js
import { createRouter, createWebHistory } from 'vue-router';
import LoginForm from '../components/LoginForm.vue';
import DashboardComponent from '../components/DashboardComponent.vue';

const routes = [
  {
    path: '/',
    name: 'Login',
    component: LoginForm,
  },
  {
    path: '/login',
    name: 'Login',
    component: LoginForm,
  },
  
  {
    path: '/dashboard/:username',
    name: 'Dashboard',
    component: DashboardComponent,
    meta: { requiresAuth: true },
    props: true, // Это позволяет передавать параметры в качестве свойств
  },

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