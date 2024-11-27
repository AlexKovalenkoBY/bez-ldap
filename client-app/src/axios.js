// src/axios.js
import axios from 'axios';

const instance = axios.create({
  baseURL: 'http://localhost:8080/api', // Базовый URL вашего бэкенда
});

instance.interceptors.request.use(
  (config) => {
    const accessToken = localStorage.getItem('accessToken');
    if (accessToken) {
      config.headers['Authorization'] = `Bearer ${accessToken}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

export default instance;