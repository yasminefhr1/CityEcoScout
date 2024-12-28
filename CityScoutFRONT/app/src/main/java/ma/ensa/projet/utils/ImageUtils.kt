package ma.ensa.projet.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.ByteArrayOutputStream

object ImageUtils {
    fun compressImage(imageBytes: ByteArray, maxSizeKB: Int = 500): ByteArray {
        val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
        val outputStream = ByteArrayOutputStream()

        var quality = 100
        do {
            outputStream.reset()
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
            quality -= 5
        } while (outputStream.size() > maxSizeKB * 1024 && quality > 5)

        return outputStream.toByteArray()
    }
}