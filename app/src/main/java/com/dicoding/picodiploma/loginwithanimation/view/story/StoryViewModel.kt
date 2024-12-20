package com.dicoding.picodiploma.loginwithanimation.view.story

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import com.dicoding.picodiploma.loginwithanimation.data.StoryRepository
import java.io.File
import java.io.FileOutputStream

class StoryViewModel(private val storyRepository: StoryRepository) : ViewModel() {
    fun uploadStory(
        token: String,
        photoUri: Uri,
        description: String,
        context: Context
    ) {
        val file = uriToFile(photoUri, context)
    }

    private fun uriToFile(uri: Uri, context: Context): File {
        val contentResolver = context.contentResolver
        val file = File(context.cacheDir, "image_${System.currentTimeMillis()}.png")
        val inputStream = contentResolver.openInputStream(uri)
        val outputStream = FileOutputStream(file)

        val buffer = ByteArray(1024)
        var length: Int
        while (true) {
            length = inputStream?.read(buffer) ?: -1
            if (length == -1) break
            outputStream.write(buffer, 0, length)
        }

        outputStream.close()
        inputStream?.close()
        return file
    }
}