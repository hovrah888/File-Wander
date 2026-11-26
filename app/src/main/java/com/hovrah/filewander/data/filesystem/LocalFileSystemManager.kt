package com.hovrah.filewander.data.filesystem

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.io.File

// extension-функция для перезаписи "сырого" File -> FileItem
fun File.toFileItem(): FileItem {
    val isDir = this.isDirectory
    return FileItem(
        file = this,
        name = this.name,
        size = if (isDir) 0 else this.length(), // для директорий длина обычно не определена
        extension = if (isDir) "" else this.extension, // расширение файла
        lastModified = this.lastModified(), // время последнего изменения
        isDirectory = isDir
    )
}

// реализация интерфейса FileSystemManager внутри соответственного класса
class LocalFileSystemManager(val dispatcher : CoroutineDispatcher) : FileSystemManager {

    override suspend fun getFiles(directory: File): GetFilesResult = withContext(dispatcher) {
        if (!directory.exists()) {
            return@withContext GetFilesResult.Error.DirectoryNotFound
        }
        if (!directory.isDirectory) {
            return@withContext GetFilesResult.Error.NotADirectory
        }
        if (!directory.canRead()) {
            return@withContext GetFilesResult.Error.PermissionDenied
        }
        val filesArray = directory.listFiles()
            ?: return@withContext GetFilesResult.Error.Unknown("Failed to list files in directory")


        val fileItems = filesArray.map { it.toFileItem() }

        GetFilesResult.Success(fileItems)
    }

    override suspend fun createDirectory(
        parent: File,
        name: String,
        conflictStrategy: ConflictStrategy
    ): FileOperationResult {
        TODO("Not yet implemented")
    }

    override suspend fun renameFile(
        file: File,
        newName: String,
        conflictStrategy: ConflictStrategy
    ): FileOperationResult {
        TODO("Not yet implemented")
    }

    override suspend fun moveFile(
        file: File,
        newDirectory: File,
        conflictStrategy: ConflictStrategy
    ): FileOperationResult {
        TODO("Not yet implemented")
    }

    override suspend fun deleteFile(file: File): FileOperationResult {
        TODO("Not yet implemented")
    }
}