package com.demensdeum.teflecher

import java.awt.FileDialog
import java.awt.Frame
import java.io.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

actual suspend fun loadQuizFile(): String? = withContext(Dispatchers.IO) {
    var result: String? = null
    val dialog = FileDialog(null as Frame?, "Select Quiz JSON", FileDialog.LOAD)
    dialog.file = "*.json"
    dialog.isVisible = true
    val file = dialog.file
    val directory = dialog.directory
    if (file != null && directory != null) {
        result = File(directory, file).readText()
    }
    result
}
