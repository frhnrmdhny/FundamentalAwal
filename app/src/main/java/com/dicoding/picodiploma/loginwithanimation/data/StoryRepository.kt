package com.dicoding.picodiploma.loginwithanimation.data

import com.dicoding.picodiploma.loginwithanimation.data.pref.UserPreference
import com.dicoding.picodiploma.loginwithanimation.data.remote.ApiService
import com.dicoding.picodiploma.loginwithanimation.data.remote.response.AddStoryResponse
import com.dicoding.picodiploma.loginwithanimation.data.remote.response.StoryResponse
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

class StoryRepository private constructor(
    private val storyApiService: ApiService,
    private val userPreference: UserPreference,
) {
    suspend fun getAllStories(): StoryResponse {
        return storyApiService.getAllStories()
    }

    suspend fun uploadStory(
        photoFile: File,
        description: String,
        lat: Float? = null,
        lon: Float? = null,
    ): AddStoryResponse {
        val descriptionRequestBody =
            RequestBody.create("text/plain".toMediaTypeOrNull(), description)
        val photoRequestBody = RequestBody.create("image/*".toMediaTypeOrNull(), photoFile)
        val photoPart = MultipartBody.Part.createFormData("photo", photoFile.name, photoRequestBody)
        val latRequestBody =
            lat?.let { RequestBody.create("text/plain".toMediaTypeOrNull(), it.toString()) }
        val lonRequestBody =
            lon?.let { RequestBody.create("text/plain".toMediaTypeOrNull(), it.toString()) }

        return storyApiService.uploadStory(
            description = descriptionRequestBody,
            photo = photoPart,
            lat = latRequestBody,
            lon = lonRequestBody
        )
    }

    @Volatile
    private var instance: StoryRepository? = null

    fun getInstance(
        storyapiService: ApiService,
        userPreference: UserPreference,
    ): StoryRepository =
        instance ?: synchronized(this) {
            instance ?: StoryRepository(storyapiService, userPreference)
        }.also {
            instance = it
        }


}


