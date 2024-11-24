<template>
    <div class="dashboard">
      <h2>Dashboard</h2>
      <p>Welcome to the dashboard!</p>
      <button @click="logout">Logout</button>
    </div>
  </template>
  
  <script>
  export default {
    methods: {
      logout() {
        localStorage.removeItem('token');
        this.$router.push('/');
      },
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