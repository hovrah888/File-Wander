package com.hovrah.filewander.data.filesystem
import java.io.File

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