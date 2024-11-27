<template>
  <div class="dashboard">
    <h2>Dashboard</h2>
    <p>Welcome to the dashboard!</p>
    <div v-if="userDetails">
      <p><strong>Username:</strong> {{ userDetails.username }}</p>
      <p><strong>Common Name:</strong> {{ userDetails.commonName }}</p>
      <p><strong>Surname:</strong> {{ userDetails.surname }}</p>
      <p><strong>Given Name:</strong> {{ userDetails.givenName }}</p>
      <p><strong>Display Name:</strong> {{ userDetails.displayName }}</p>
      <p><strong>Role:</strong> {{ userDetails.role }}</p>
    </div>
    <button @click="logout">Logout</button>
  </div>
</template>

<script>
// src/components/DashboardComponent.vue
import axios from '../axios';

export default {
  name: 'DashboardComponent',
  data() {
    return {
      userDetails: null,
    };
  },
  methods: {
    logout() {
      localStorage.removeItem('accessToken');
      this.$router.push('/');
    },
    async fetchUserDetails() {
      try {
        const response = await axios.get(`/auth/user/${this.username}`);
        this.userDetails = response.data;
      } catch (error) {
        console.error('Failed to fetch user details', error);
        if (error.response && error.response.status === 403) {
          localStorage.removeItem('accessToken'); // Удаление недействительного токена
          this.$router.push('/login'); // Перенаправление на страницу входа
        }
      }
    },
  },
  created() {
    this.fetchUserDetails();
  },
};
</script>

<style scoped>
.dashboard {
  max-width: 300px;
  margin: 0 auto;
  padding: 20px;
  border: 1px solid #ccc;
  border-radius: 5px;
}

button {
  width: 100%;
  padding: 10px;
  background-color: #f44336;
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
}

button:hover {
  background-color: #da190b;
}
</style>