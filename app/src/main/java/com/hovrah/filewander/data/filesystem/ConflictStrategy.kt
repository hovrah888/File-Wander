package com.hovrah.filewander.data.filesystem

// сценарии при несостыковке имени файлов
enum class ConflictStrategy{
    FAIL_IF_EXISTS,
    OVERWRITE,
    LEAVE_BOTH,
}