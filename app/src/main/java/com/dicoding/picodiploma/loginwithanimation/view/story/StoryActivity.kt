package com.dicoding.picodiploma.loginwithanimation.view.story

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.dicoding.picodiploma.loginwithanimation.R
import com.dicoding.picodiploma.loginwithanimation.databinding.ActivityStoryBinding
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileInputStream


class StoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStoryBinding
    private var selectedImageUri: Uri? = null

    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            selectedImageUri = uri
            binding.imageView.setImageURI(uri)
        }
    }

    private val launcherIntentCamera =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { isSuccess ->
            if (isSuccess) {
                selectedImageUri?.let {
                    binding.imageView.setImageURI(it)
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        checkPermissions()

        binding.btnGallery.setOnClickListener {
            pickImage.launch("image/*")
        }

        binding.btnCamera.setOnClickListener {
            startCamera()
        }

        binding.btnUpload.setOnClickListener {
            uploadImage()
        }
    }

    private fun checkPermissions() {
        val permissionsNeeded = mutableListOf<String>()

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissionsNeeded.add(Manifest.permission.CAMERA)
        }
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }


        if (permissionsNeeded.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, permissionsNeeded.toTypedArray(), 100)
        }
    }

    private fun startCamera() {
        selectedImageUri = getImageUri()
        selectedImageUri?.let {
            launcherIntentCamera.launch(it)
        } ?: run {
            showError("Failed to create URI for the camera.")
        }
    }

    private fun getImageUri(): Uri {
        val file = File(externalCacheDir, "temp_photo.jpg")
        return FileProvider.getUriForFile(this, "${packageName}.provider", file)
    }

    private fun uploadImage() {
        if (selectedImageUri == null) {
            showError(R.string.error_selectImage.toString())
            return
        }

        val file = File(getRealPathFromURI(selectedImageUri))
        val requestFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
        val body = MultipartBody.Part.createFormData("photo", file.name, requestFile)

    }

    private fun getRealPathFromURI(uri: Uri?): String {
        if (uri == null) return ""

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val fileDescriptor = contentResolver.openFileDescriptor(uri, "r") ?: return ""
            val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
            val tempFile = File(cacheDir, "temp_image.jpg")
            tempFile.outputStream().use { inputStream.copyTo(it) }
            tempFile.absolutePath
        } else {
            val cursor = contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                it.moveToFirst()
                val index = it.getColumnIndex(MediaStore.Images.Media.DATA)
                if (index != -1) return it.getString(index)
            }
            ""
        }
    }


    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}

