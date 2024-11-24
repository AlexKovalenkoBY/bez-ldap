import { createRouter, createWebHistory } from 'vue-router';
import LoginForm from '../components/LoginForm.vue';

const routes = [
  {
    path: '/',
    name: 'Login',
    component: LoginForm,
  },
  {
    path: '/dashboard',
    name: 'Dashboard',
    component: () => import('../components/DashboardComponent.vue'),
    meta: { requiresAuth: true },
  },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token');
  if (to.matched.some(record => record.meta.requiresAuth) && !token) {
    next('/');
  } else {
    next();
  }
});

export default router;