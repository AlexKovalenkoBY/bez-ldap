<!-- src/components/FileUploader.vue -->
<template>
  <div class="container">
    <div class="drop-zone" title="Для загрузки справочников перетащите их сюда либо нажмите здесь" @click="openFileInput" @dragover.prevent @drop.prevent="handleDrop">
      +
    </div>
    <input type="file" ref="fileInput" class="d-none" multiple accept=".pdf" @change="handleFileChange" />
  </div>
</template>

<script>
import axios from 'axios';

export default {
  name: 'FileUploader',
  methods: {
    openFileInput() {
      this.$refs.fileInput.click();
    },
    handleFileChange(event) {
      const files = event.target.files;
      this.uploadFiles(files);
    },
    handleDrop(event) {
      const files = event.dataTransfer.files;
      this.uploadFiles(files);
    },
    async uploadFiles(files) {
      const url = 'http://127.0.0.1:8080/api/auth/uploadfiles';
      const token = localStorage.getItem('accessToken');
      if (!token) {
        console.error('Токен аутентификации отсутствует');
        return;
      }

      const formData = new FormData();
      for (let i = 0; i < files.length; i++) {
        formData.append('files', files[i]);
      }

      try {
        const result = await axios.post(url, formData, {
          headers: {
            "Accept": "application/json",
            "Content-Type": "multipart/form-data",
            "Authorization": `Bearer ${token}`
          }
        });
        console.log('Файлы успешно загружены:', result.data);
        this.$emit('fileUploaded'); // Эмитим событие после успешной загрузки файлов
      } catch (error) {
        console.error('Ошибка при загрузке файлов:', error);
      }
    }
  }
};
</script>

<style scoped>
.container {
  margin: 0 auto;
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 200px;
  max-width: 80%;
  background: radial-gradient(circle, skyblue, steelblue);
  color: #222;
}

.drop-zone {
  width: 100px;
  height: 100px;
  display: flex;
  justify-content: center;
  align-items: center;
  font-size: 10em;
  font-weight: bold;
  border: 6px solid;
  border-radius: 8px;
  user-select: none;
  cursor: pointer;
}

input {
  display: none;
}
</style>