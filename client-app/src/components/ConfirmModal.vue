<!-- src/components/ConfirmModal.vue -->
<template>
    <div class="modal-overlay" v-if="visible" @click="hideModal">
        <div class="modal-container" @click.stop>
            <div class="modal-header">
                <h5 class="modal-title">Подтверждение удаления</h5>
                <button type="button" class="close" @click="hideModal">
                    <span aria-hidden="true">&times;</span>
                </button>
            </div>
            <div class="modal-body">
                Вы уверены, что хотите удалить файл "{{ fileName }}"?
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" @click="hideModal">Отмена</button>
                <button type="button" class="btn btn-danger" @click="confirm">Удалить</button>
            </div>
        </div>
    </div>
</template>

<script>
export default {
    props: {
        fileName: String,
    },
    data() {
        return {
            visible: false,
        };
    },
    methods: {
        showModal() {
            this.visible = true;
        },
        hideModal() {
            this.visible = false;
        },
        confirm() {
            this.$emit('confirm');
            this.hideModal();
        }
    }
};
</script>

<style scoped>
.modal-overlay {
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background: rgba(0, 0, 0, 0.5);
    display: flex;
    justify-content: center;
    align-items: center;
    z-index: 1000;
}

.modal-container {
    background: white;
    padding: 20px;
    border-radius: 5px;
    width: 80%;
    max-width: 500px;
    box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.modal-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    border-bottom: 1px solid #e5e5e5;
    padding-bottom: 10px;
    margin-bottom: 10px;
}

.modal-footer {
    display: flex;
    justify-content: flex-end;
    border-top: 1px solid #e5e5e5;
    padding-top: 10px;
    margin-top: 10px;
}

.close {
    background: none;
    border: none;
    font-size: 1.5rem;
    cursor: pointer;
}
</style>