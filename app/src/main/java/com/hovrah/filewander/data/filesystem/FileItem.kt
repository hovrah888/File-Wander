package com.hovrah.filewander.data.filesystem

import java.io.File

data class FileItem(
    val file: File,
    val name: String,
    val size: Long,
    val extension: String,
    val lastModified: Long,
    val isDirectory: Boolean
)
