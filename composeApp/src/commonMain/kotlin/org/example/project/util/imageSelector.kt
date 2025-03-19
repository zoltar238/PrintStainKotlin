package org.example.project.util

import java.awt.FileDialog
import java.awt.Frame

// Todo: Implement imageSelector for android
fun imageSelector(): String? {
    val fileDialog = FileDialog(Frame(), "Seleccionar imagen", FileDialog.LOAD)
    fileDialog.file = "*.png;*.jpg;*.jpeg"
    fileDialog.isVisible = true
    return if (fileDialog.file != null) {
        "${fileDialog.directory}${fileDialog.file}"
    } else {
        null
    }
}
