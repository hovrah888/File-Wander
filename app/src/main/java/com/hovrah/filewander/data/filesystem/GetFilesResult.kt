package com.hovrah.filewander.data.filesystem

// состояния для получения списка файлов
sealed interface GetFilesResult {

    data class Success(val files: List<FileItem>) : GetFilesResult

    sealed class Error : GetFilesResult {
        data object DirectoryNotFound : Error()
        data object PermissionDenied : Error()
        data object NotADirectory : Error()
        data class Unknown(val message : String) : Error()
    }
}