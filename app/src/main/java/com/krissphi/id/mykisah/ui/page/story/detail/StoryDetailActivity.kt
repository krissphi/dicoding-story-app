package com.krissphi.id.mykisah.ui.page.story.detail

import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.krissphi.id.mykisah.R
import com.krissphi.id.mykisah.data.remote.response.StoryItem
import com.krissphi.id.mykisah.data.repository.ViewModelFactory
import com.krissphi.id.mykisah.databinding.ActivityStoryDetailBinding
import com.krissphi.id.mykisah.utils.formattedDate
import android.graphics.drawable.Drawable
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target

class StoryDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStoryDetailBinding
    private val viewModel: StoryDetailViewModel by viewModels {
        ViewModelFactory(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        postponeEnterTransition()

        window.requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS)
        enableEdgeToEdge()
        binding = ActivityStoryDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        fetchData()
        setupObservers()
    }

    private fun fetchData() {
        val id = intent.getStringExtra("story_id")
        if (id != null) {
            viewModel.fetchStoryDetail(id)
        } else {
            Toast.makeText(this, R.string.story_not_found, Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun setupObservers() {
        viewModel.story.observe(this) { story ->
            bindStoryData(story)
        }

        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.errorMessage.observe(this) { message ->
            val message = when (message) {
                "STORY_NOT_FOUND" -> getString(R.string.story_not_found)
                else -> message
            }
            if (message.isNotEmpty()) {
                Toast.makeText(this, getString(R.string.error_message, message), Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun bindStoryData(story: StoryItem) {
        Glide.with(this)
            .load(story.photoUrl)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>,
                    isFirstResource: Boolean
                ): Boolean {
                    startPostponedEnterTransition()
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable,
                    model: Any,
                    target: Target<Drawable>?,
                    dataSource: DataSource,
                    isFirstResource: Boolean
                ): Boolean {
                    startPostponedEnterTransition()
                    return false
                }
            })
            .into(binding.imgStory)

        binding.tvProfile.text = story.name
        binding.tvCaption.text = story.description
        if (story.createdAt != null) {
            binding.tvCreatedAt.text = formattedDate(story.createdAt)
        }

        if (story.lat != null && story.lon != null) {
            binding.tvLat.text = getString(R.string.latitude, story.lat)
            binding.tvLon.text = getString(R.string.longitude, story.lon)
            binding.tvLat.visibility = View.VISIBLE
            binding.tvLon.visibility = View.VISIBLE
        } else {
            binding.tvLat.visibility = View.GONE
            binding.tvLon.visibility = View.GONE
        }
    }

}