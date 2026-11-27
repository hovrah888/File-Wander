package com.hovrah.filewander.data.filesystem

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Files.createDirectory

// extension-функция для перезаписи "сырого" File -> FileItem
fun File.toFileItem(): FileItem {
    val isDir = this.isDirectory
    return FileItem(
        file = this,
        name = this.name,
        size = if (isDir) 0L else this.length(), // для директорий длина обычно не определена
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
    ): FileOperationResult = withContext(dispatcher) {
        if (!parent.exists()){
            return@withContext FileOperationResult.Error.FileNotFound
        }
        if (parent.isFile){
            return@withContext FileOperationResult.Error.NotADirectory
        }
        if (!parent.canWrite()){
            return@withContext FileOperationResult.Error.PermissionDenied
        }
        if (name.contains("/") || name == "." || name == ".." || name.length > 40 || name.isBlank()){
            return@withContext FileOperationResult.Error.InvalidNewName
        }
//        if (parent.usableSpace == 0L) {
//            return@withContext FileOperationResult.Error.NotEnoughSpace
//        }

        val targetDir = File(parent, name)

        if (targetDir.exists()) {
            when (conflictStrategy) {
                ConflictStrategy.FAIL_IF_EXISTS -> {
                    return@withContext FileOperationResult.Error.DestinationExists
                }

                else -> {}
            }
        }

        // повторные проверки того же самого тут ДОПУСТИМЫ
        // ведь ФС может измениться между проверкой и использованием, по факту race condition,
        // или другими словами TOCTOU (Time Of Check To Time Of Use).
        return@withContext try {
            val createdPath = createDirectory(targetDir.toPath())
            FileOperationResult.Success(createdPath.toFile())
        } catch (e: FileAlreadyExistsException) {
            FileOperationResult.Error.DestinationExists
        } catch (e: NoSuchFileException) {
            FileOperationResult.Error.FileNotFound
        } catch (e: AccessDeniedException) {
            FileOperationResult.Error.PermissionDenied
        } catch (e: IOException) {
            // IOException может означать NotEnoughSpace
            if (e.message?.contains("No space left", ignoreCase = true) == true) {
                FileOperationResult.Error.NotEnoughSpace
            } else {
                FileOperationResult.Error.Unknown(e.message ?: "Failed to create directory")
            }
        }
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