package com.hovrah.filewander.data.filesystem

import com.hovrah.filewander.data.filesystem.FileOperationResult.Error
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

// сценарии при несостыковке имени файлов
enum class ConflictStrategy{
    FAIL_IF_EXISTS,
    OVERWRITE,
    LEAVE_BOTH,
}

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

interface FileSystemManager { // интерфейс для FileSystemManager (именно интерфейс, ведь в будущем для тестирования он будет нужен)
    suspend fun getFiles(directory: File): GetFilesResult

    suspend fun createDirectory(
        parent: File,
        name: String,
        conflictStrategy: ConflictStrategy = ConflictStrategy.FAIL_IF_EXISTS) : FileOperationResult

    suspend fun renameFile(
        file: File,
        newName: String,
        conflictStrategy: ConflictStrategy = ConflictStrategy.FAIL_IF_EXISTS) : FileOperationResult

    // в Kotlin File не разделяется на файл и папку на уровне типов
    suspend fun moveFile(
        file: File,
        newDirectory: File,
        conflictStrategy: ConflictStrategy = ConflictStrategy.FAIL_IF_EXISTS) : FileOperationResult

    suspend fun deleteFile(file: File): FileOperationResult
}