package org.example.project.util

import net.lingala.zip4j.ZipFile
import net.lingala.zip4j.model.ZipParameters
import net.lingala.zip4j.model.enums.CompressionLevel
import net.lingala.zip4j.model.enums.CompressionMethod
import java.io.File

object Zipper {
    fun createZip(files: List<File>, outputFile: File): File {
        val zipFile = ZipFile(outputFile)
        val paramaeters = ZipParameters().apply {
            compressionMethod = CompressionMethod.DEFLATE
            compressionLevel = CompressionLevel.NORMAL
        }
        zipFile.addFiles(files, paramaeters)

        return outputFile
    }
}