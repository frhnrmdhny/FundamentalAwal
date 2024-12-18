package com.dicoding.picodiploma.loginwithanimation.data

import com.dicoding.picodiploma.loginwithanimation.data.pref.UserPreference
import com.dicoding.picodiploma.loginwithanimation.data.remote.ApiService

class StoryRepository private constructor(
    private val storyApiService: ApiService,
    private val userPreference: UserPreference,
) {

}