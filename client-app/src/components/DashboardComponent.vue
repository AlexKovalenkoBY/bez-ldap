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
  import axios from 'axios';
  
  export default {
    data() {
      return {
        userDetails: null,
      };
    },
    methods: {
      logout() {
        localStorage.removeItem('token');
        this.$router.push('/');
      },
      async fetchUserDetails() {
        try {
          const token = localStorage.getItem('token');
          const response = await axios.get('http://localhost:8080/api/auth/user', {
            headers: {
              Authorization: `Bearer ${token}`,
            },
          });
          this.userDetails = response.data;
        } catch (error) {
          console.error('Failed to fetch user details', error);
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