package com.hovrah.filewander

import com.hovrah.filewander.data.filesystem.GetFilesResult
import com.hovrah.filewander.data.filesystem.LocalFileSystemManager
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Assume.assumeFalse
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

class LocalFileSystemManagerTest {

    // подключение тестовой папки на JVM
    @get:Rule
    val tempFolder = TemporaryFolder()

    private val manager = LocalFileSystemManager(UnconfinedTestDispatcher()) // создание локального менеджера ФС на тестовом диспетчере

    // при попытке получить содержимое файла как содержимое папки - возникает ошибка NotADirectory
    @Test
    fun `getFiles on a file returns NotADirectory`() = runTest {
        val file = tempFolder.newFile("a.txt")

        val result = manager.getFiles(file)
        assertEquals(GetFilesResult.Error.NotADirectory, result)
    }

    // при попытке получить содержимое несуществующей папки - возникает ошибка DirectoryNotFound
    @Test
    fun `getFiles on a nonexistent folder returns DirectoryNotFound`() = runTest {
        val nonexistentFolder = File(tempFolder.root, "a") // путь к несуществующей папки внутри tempFolder
        val result = manager.getFiles(nonexistentFolder)

        assertEquals(GetFilesResult.Error.DirectoryNotFound, result)
    }

    // при попытке получить содержимое пустой папки files будет пустым
    @Test
    fun `getFiles on a empty folder returns Success`() = runTest {
        val emptyFolder = tempFolder.newFolder("a")
        val result = manager.getFiles(emptyFolder)

        assertEquals(GetFilesResult.Success(emptyList()), result)
    }

    // при попытке получить содержимое папки с файлом и подпапкой — возвращается Success со списком FileItem
    @Test
    fun `getFiles on folder with contents returns Success with list of FileItems`() = runTest {
        val parentFolder = tempFolder.newFolder("parent")

        val subFile = File(parentFolder, "test_file.txt").apply { createNewFile() }
        subFile.writeText("hello")
        val subFolder = File(parentFolder, "sub_folder.1").apply { mkdir() }

        val result = manager.getFiles(parentFolder)

        assertTrue(result is GetFilesResult.Success)

        val files = (result as GetFilesResult.Success).files
        assertEquals(2, files.size) // кол-во элементов


//          НЕЛЬЗЯ тут брать файлы по порядку из files, они могут лежать в разнобой
//        val fileItem = files[0]
//        val folderItem = files[1]
        // брать только по имени
        val fileItem = files.first { it.name == "test_file.txt" }
        val folderItem = files.first { it.name == "sub_folder.1" }

        //свойства файла
        assertFalse(fileItem.isDirectory)
        assertEquals("test_file.txt", fileItem.name)
        assertEquals("txt", fileItem.extension)
        assertEquals(5, fileItem.size)


        //свойства подпапки
        assertTrue(folderItem.isDirectory)
        assertEquals("sub_folder.1", folderItem.name)
        assertEquals("", folderItem.extension)
        assertEquals(folderItem.size, 0)
    }

    // при отсутствии прав чтения — возвращается PermissionDenied
    @Test
    fun `getFiles on unreadable folder returns PermissionDenied`() = runTest {
        val unreadableFolder = tempFolder.newFolder("a")

        unreadableFolder.setReadable(false) // запрет на чтение
        assumeFalse(unreadableFolder.canRead()) // если не получается, то тест пропускается (для некоторых окружений может работать по-разному, поэтому можно указать)
        val result = manager.getFiles(unreadableFolder)


        assertEquals(GetFilesResult.Error.PermissionDenied, result)
    }

}