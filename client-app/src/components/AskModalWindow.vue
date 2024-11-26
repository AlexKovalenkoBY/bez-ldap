        <!-- Модальное окно подтверждения удаления -->
        <div class="modal fade text-center" id="confirmModal" tabindex="-1" role="dialog"
            aria-labelledby="confirmModalLabel" aria-hidden="true">
            <div class="modal-dialog" role="document">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title" id="confirmModalLabel">Подтверждение удаления</h5>
                        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                            <span aria-hidden="true">&times;</span>
                        </button>
                    </div>
                    <div class="modal-body">
                        <span id="confirmText">Вы действительно хотите удалить файл: <strong>{{ fileToDelete
                                }}</strong>?</span>
                    </div>
                    <div class="modal-footer">
                        <a type="button" id="yesBtn" class="btn btn-danger" v-on:click="deleteFile">Да, удаляем</a>
                        <button type="button" class="btn btn-success" data-dismiss="modal">Нет, не надо</button>
                    </div>
                </div>
            </div>
        </div>