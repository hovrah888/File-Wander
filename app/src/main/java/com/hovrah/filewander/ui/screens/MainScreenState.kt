package com.hovrah.filewander.ui.screens

import com.hovrah.filewander.data.filesystem.FileItem
import java.io.File

data class MainScreenState(
    val currentDirectory: File,
    val files: List<FileItem>,
    val isLoading: Boolean,

    //val sortType: SortType,
    //val sortOrder: SortOrder,
    //val favouriteDirectories: List<Files> = listOf(...)
)
