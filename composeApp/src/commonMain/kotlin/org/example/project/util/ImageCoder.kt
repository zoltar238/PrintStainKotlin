package org.example.project.util

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asSkiaBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.decodeToImageBitmap
import org.jetbrains.skia.Image
import org.jetbrains.skiko.toBufferedImage
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.*
import javax.imageio.ImageIO

fun decodeBase64ToBitmap(base64Str: String): ImageBitmap {
    val decodedBytes = Base64.getDecoder().decode(base64Str)
    val img = Image.makeFromEncoded(decodedBytes)
    return img.toComposeImageBitmap()
}

fun encodeBitmapToBase64(bitmap: ImageBitmap): String {
    val bufferedImage = bitmap.asSkiaBitmap().toBufferedImage()
    val outputStream = ByteArrayOutputStream()
    ImageIO.write(bufferedImage, "png", outputStream)
    return Base64.getEncoder().encodeToString(outputStream.toByteArray())
}

@OptIn(ExperimentalResourceApi::class)
fun urlImageToBitmap(url: String): ImageBitmap {
    return File(url).inputStream().readAllBytes().decodeToImageBitmap()
}