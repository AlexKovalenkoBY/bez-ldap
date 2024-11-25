<!-- src/components/Login.vue -->
<template>
    <div>
      <h1>Login</h1>
      <input type="text" v-model="username" placeholder="Username" />
      <input type="password" v-model="password" placeholder="Password" />
      <button @click="handleLogin">Login</button>
    </div>
  </template>
  
  <script>
  import { ref } from 'vue';
  import { useRouter } from 'vue-router';
  import axios from 'axios';
  import { setAccessToken, setRefreshToken } from '../services/jwtService';
  
  export default {
    setup() {
      const username = ref('');
      const password = ref('');
      const router = useRouter();
  
      const handleLogin = async () => {
        try {
          const response = await axios.post('http://localhost:8080/api/auth/login', {
            username: username.value,
            password: password.value,
          });
  
          if (response.status === 200) {
            setAccessToken(response.data.accessToken);
            setRefreshToken(response.data.refreshToken);
            router.push('/dashboard');
          } else {
            alert('Login failed: ' + response.data.message);
          }
        } catch (error) {
          console.error('Error during login:', error);
        }
      };
  
      return {
        username,
        password,
        handleLogin,
      };
    },
  };
  </script>