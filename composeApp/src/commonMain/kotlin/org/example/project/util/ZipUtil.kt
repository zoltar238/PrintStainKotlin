package org.example.project.util

import net.lingala.zip4j.ZipFile
import net.lingala.zip4j.model.ZipParameters
import net.lingala.zip4j.model.enums.CompressionLevel
import net.lingala.zip4j.model.enums.CompressionMethod
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

object Zipper {
    fun createZipFile(files: List<File>, outputFile: File): File {
        ZipOutputStream(FileOutputStream(outputFile)).use { zipOut ->
            files.forEach { file ->
                FileInputStream(file).use { fis ->
                    val zipEntry = ZipEntry(file.name)
                    zipOut.putNextEntry(zipEntry)
                    fis.copyTo(zipOut, 1024)
                }
            }
        }
        return outputFile
    }

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