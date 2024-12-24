package org.example.project.controller
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import org.jetbrains.skia.Image
import java.util.Base64

fun decodeBase64ToBitmap(base64Str: String): ImageBitmap {
    val decodedBytes = Base64.getDecoder().decode(base64Str)
    val img = Image.makeFromEncoded(decodedBytes)
    return img.toComposeImageBitmap()
}