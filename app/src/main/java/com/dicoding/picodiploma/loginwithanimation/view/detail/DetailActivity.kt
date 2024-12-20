package com.dicoding.picodiploma.loginwithanimation.view.detail

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.picodiploma.loginwithanimation.databinding.ActivityDetailBinding


class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val storyName = intent.getStringExtra("STORY_NAME")
        val storyDescription = intent.getStringExtra("STORY_DESCRIPTION")
        val storyPhotoUrl = intent.getStringExtra("STORY_PHOTO_URL")

        binding.titleDetail.text = storyName ?: "No title"
        binding.descDetail.text = storyDescription ?: "No description"
    }
}