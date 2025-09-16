package com.krissphi.id.mykisah.utils

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

fun formattedDate(date: String): String {
    val locale = Locale.forLanguageTag("id-ID")
    val inputMs = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }
    val inputNoMs = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }
    val outDate = SimpleDateFormat("d MMMM yyyy", locale)

    val epoch = runCatching { inputMs.parse(date)?.time }
        .getOrNull() ?: runCatching { inputNoMs.parse(date)?.time }
        .getOrNull() ?: return date

    val diff = (System.currentTimeMillis() - epoch).coerceAtLeast(0L)

    val sec = diff / 1000
    val min = sec / 60
    val hour = min / 60
    val day = hour / 24
    val week = day / 7

    return when {
        sec < 60 -> "$sec detik lalu"
        min < 60 -> "$min menit lalu"
        hour < 24 -> "$hour jam lalu"
        day < 7 -> "$day hari lalu"
        week < 5 -> "$week minggu lalu"
        else -> outDate.format(Date(epoch))
    }
}


private const val FILENAME_FORMAT = "yyyyMMdd_HHmmss"
private fun newTimeStamp(): String =
    SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(Date())

fun getImageUri(context: Context): Uri {
    val timeStamp = newTimeStamp()

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, "$timeStamp.jpg")
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            put(MediaStore.MediaColumns.RELATIVE_PATH, "Pictures/MyKisah/")
        }
        context.contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            contentValues
        )?.let { return it }
    }

    return getImageUriForPreQ(context, timeStamp)
}

private fun getImageUriForPreQ(context: Context, timeStamp: String): Uri {
    val filesDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    val imageFile = File(filesDir, "MyCamera/$timeStamp.jpg")
    imageFile.parentFile?.mkdirs()

    val authority = "${context.packageName}.fileprovider"
    return FileProvider.getUriForFile(context, authority, imageFile)
}

fun uriToFile(imageUri: Uri, context: Context): File {
    val myFile = createCustomTempFile(context)
    val inputStream = context.contentResolver.openInputStream(imageUri) as InputStream
    val outputStream = FileOutputStream(myFile)
    val buffer = ByteArray(1024)
    var length: Int
    while (inputStream.read(buffer).also { length = it } > 0) outputStream.write(buffer, 0, length)
    outputStream.close()
    inputStream.close()
    return myFile
}

fun createCustomTempFile(context: Context): File {
    val filesDir = context.externalCacheDir
    return File.createTempFile(newTimeStamp(), ".jpg", filesDir)
}

fun File.reduceFileImage(): File {
    val file = this
    val bitmap = BitmapFactory.decodeFile(file.path)
    var compressQuality = 100
    var streamLength: Int
    do {
        val bmpStream = ByteArrayOutputStream()
        bitmap?.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpStream)
        val bmpPicByteArray = bmpStream.toByteArray()
        streamLength = bmpPicByteArray.size
        compressQuality -= 5
    } while (streamLength > 1000000)
    bitmap?.compress(Bitmap.CompressFormat.JPEG, compressQuality, FileOutputStream(file))
    return file
}