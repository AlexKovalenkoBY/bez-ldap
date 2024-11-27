<template>
    <div class="container-fluid" style="max-width: 800px; margin: 0 auto;">
        <h2 class="text-center">Список скачанных файлов</h2>

        <div v-if="folderList.length > 0">
            <table class="table table-hover">
                <thead class="thead-light">
                    <tr>
                        <th scope="col">Имя</th>
                        <th scope="col">Ссылка</th>
                        <th scope="col">Удалить</th>
                    </tr>
                </thead>
                <tbody>
                    <tr v-for="file in folderList" :key="file.name">
                        <td>{{ file.name }}</td>
                        <td><a :href="file.url" title="Скачать этот файл">Скачать</a></td>
                        <td>
                            <a :href="'/files/delete/' + file.name" :fileName="file.name"
                                v-on:click.prevent="confirmDelete(file.name)" title="Удалить этот файл"
                                class="fa-regular fa-trash-can icon-dark btn-delete">
                                <svg xmlns="http://www.w3.org/2000/svg" width="32" height="32" fill="currentColor"
                                    viewBox="0 0 16 16">
                                    <path
                                        d="M5.5 5.5A.5.5 0 0 1 6 6v6a.5.5 0 0 1-1 0V6a.5.5 0 0 1 .5-.5m2.5 0a.5.5 0 0 1 .5.5v6a.5.5 0 0 1-1 0V6a.5.5 0 0 1 .5-.5m3 .5a.5.5 0 0 0-1 0v6a.5.5 0 0 0 1 0z" />
                                    <path
                                        d="M14.5 3a1 1 0 0 1-1 1H13v9a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V4h-.5a1 1 0 0 1-1-1V2a1 1 0 0 1 1-1H6a1 1 0 0 1 1-1h2a1 1 0 0 1 1 1h3.5a1 1 0 0 1 1 1zM4.118 4 4 4.059V13a1 1 0 0 0 1 1h6a1 1 0 0 0 1-1V4.059L11.882 4zM2.5 3h11V2h-11z" />
                                </svg>
                            </a>
                        </td>
                    </tr>
                </tbody>
            </table>
        </div>

        <div v-else>
            <span>Нет скачаных файлов!</span>
        </div>


    </div>
</template>

<script>
import axios from 'axios';

export default {
    data() {
        return {
            folderList: [], // Здесь будет список файлов
            fileToDelete: '', // Имя файла, который нужно удалить
        };
    },
    methods: {
        confirmDelete(fileName) {
            this.fileToDelete = fileName;
            $('#confirmModal').modal('show');
        },
        deleteFile() {
            // Логика удаления файла
            console.log('Удаляем файл:', this.fileToDelete);
            // После удаления можно обновить список файлов
            this.folderList = this.folderList.filter(file => file.name !== this.fileToDelete);
            $('#confirmModal').modal('hide');
        },
        async getFilesList() {
            try {
                const token = localStorage.getItem('accessToken');
                if (token) {
                    const response = await axios.post('http://localhost:8080/api/auth/getFilesList', {}, {
                        headers: {
                            Authorization: `Bearer ${token}`,
                        },
                    });
                    folderList.value = response.data;
                } else {
                    this.$router.push({ name: 'Login' });
                }
            } catch (error) {
                console.error('Failed to fetch user files list ', error);
                if (error.response && error.response.status === 403) {
                    localStorage.removeItem('accessToken'); // Удаление недействительного токена
                    this.$router.push('/login'); // Перенаправление на страницу входа
                }
            }
        }
    },
    mounted() {
        this.getFilesList();
    },
};
</script>

<style scoped>
/* Добавьте стили, если необходимо */
</style>