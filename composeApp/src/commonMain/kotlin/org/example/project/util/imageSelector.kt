package org.example.project.util

import java.awt.FileDialog
import java.awt.Frame

// Todo: Implement imageSelector for android
fun imageSelector(): String? {
    val fileDialog = FileDialog(Frame(), "Select Image", FileDialog.LOAD)
    fileDialog.file = "*.png;*.jpg;*.jpeg"
    fileDialog.isVisible = true
    return if (fileDialog.file != null) {
        "${fileDialog.directory}${fileDialog.file}"
    } else {
        null
    }
}

fun fileSelector(): List<String>? {
    val fileDialog = FileDialog(Frame(), "Select file", FileDialog.LOAD)
    fileDialog.isVisible = true
    fileDialog.isResizable = true
    fileDialog.isMultipleMode = true
    return if (fileDialog.file != null) {
        fileDialog.files?.map { "${fileDialog.directory}$it" }?.toList()
    } else {
        null
    }
}
