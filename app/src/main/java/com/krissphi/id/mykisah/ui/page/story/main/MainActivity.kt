package com.krissphi.id.mykisah.ui.page.story.main

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.krissphi.id.mykisah.R
import com.krissphi.id.mykisah.data.repository.ViewModelFactory
import com.krissphi.id.mykisah.databinding.ActivityMainBinding
import com.krissphi.id.mykisah.ui.adapter.StoryAdapter
import com.krissphi.id.mykisah.ui.page.maps.MapsActivity
import com.krissphi.id.mykisah.ui.page.story.create.StoryCreateActivity
import com.krissphi.id.mykisah.ui.page.welcome.WelcomeActivity

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding
    private val storyViewModel: StoryViewModel by viewModels { ViewModelFactory(this) }
    private lateinit var storyAdapter: StoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setSupportActionBar(binding.toolbar)
        setupRecycleView()
        setupObserver()
        setupAction()
        setupSwipeToRefresh()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.app_bar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                showLogoutDialog()
                true
            }

            R.id.action_add_story -> {
                goToCreateStory()
                true
            }

            R.id.action_look_map -> {
                val intent = Intent(this, MapsActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showLogoutDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle(getString(R.string.logout))
            .setMessage(getString(R.string.logout_message))
            .setCancelable(false)
            .setPositiveButton(getString(R.string.logout)) { _, _ ->
                storyViewModel.logout()
                val intent = Intent(this, WelcomeActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                Toast.makeText(this, getString(R.string.logout_success), Toast.LENGTH_SHORT).show()
                finish()
            }
            .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun setupAction() {
        binding.fabAdd.setOnClickListener {
            goToCreateStory()
        }
    }

    private fun goToCreateStory() {
        val intent = Intent(this, StoryCreateActivity::class.java)
        startActivity(intent)
    }

    private fun setupRecycleView() {
        storyAdapter = StoryAdapter()
        binding.rvStories.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = storyAdapter
            setHasFixedSize(true)
        }

        val threshold = 6
        binding.fabAdd.hide()
        binding.rvStories.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(rv: RecyclerView, dx: Int, dy: Int) {
                when {
                    dy > threshold && !binding.fabAdd.isShown -> binding.fabAdd.show()
                    dy < -threshold && binding.fabAdd.isShown -> binding.fabAdd.hide()
                }
            }
        })
    }

    private fun setupObserver() {
        storyViewModel.stories.observe(this) { stories ->
            storyAdapter.submitList(stories)
        }
        storyViewModel.errorMessage.observe(this) { msg ->
            if (msg.isNotEmpty()) Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
        }

        storyViewModel.isLoading.observe(this) { isLoading ->
            binding.swipeRefreshLayout.isRefreshing = isLoading
        }
    }

    private fun setupSwipeToRefresh() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            storyViewModel.fetchStories()
        }
    }

}
