package org.example.project.util

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.decodeToImageBitmap
import org.jetbrains.skia.Image
import java.io.File
import java.util.*

fun decodeBase64ToBitmap(base64Str: String): ImageBitmap {
    val decodedBytes = Base64.getDecoder().decode(base64Str)
    val img = Image.makeFromEncoded(decodedBytes)
    return img.toComposeImageBitmap()
}

fun encodeUrlToBase64(imageUrl: String): String {
    val img = File(imageUrl)
    return Base64.getEncoder().encodeToString(img.readBytes())
}

@OptIn(ExperimentalResourceApi::class)
fun urlImageToBitmap(url: String): ImageBitmap {
    return File(url).inputStream().readAllBytes().decodeToImageBitmap()
}