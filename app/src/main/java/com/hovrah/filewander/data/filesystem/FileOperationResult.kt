package com.hovrah.filewander.data.filesystem

import java.io.File

// состояние для результата работы над файлом
sealed class FileOperationResult{

    data class Success(val file : File) : FileOperationResult()

    data object SuccessDeleting : FileOperationResult()

    // вложенный sealed class для видов ошибок
    sealed class Error : FileOperationResult() {
        data object FileNotFound : Error()
        data object PermissionDenied : Error()
        data object DestinationExists : Error()
        data object MovingDirectoryIntoItself : Error()
        data object InvalidNewName : Error()
        data object NotEnoughSpace: Error()
        data class Unknown(val message : String) : Error()
    }
}