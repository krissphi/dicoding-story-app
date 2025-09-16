package com.krissphi.id.mykisah.ui.adapter

import android.app.Activity
import android.content.Intent
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.core.app.ActivityOptionsCompat
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.krissphi.id.mykisah.R
import com.krissphi.id.mykisah.data.remote.response.StoryItem
import com.krissphi.id.mykisah.databinding.ItemStoryBinding
import com.krissphi.id.mykisah.ui.page.story.detail.StoryDetailActivity
import com.krissphi.id.mykisah.utils.formattedDate

class StoryAdapter : PagingDataAdapter<StoryItem, StoryAdapter.MyViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val story = getItem(position)
        if (story != null) {
            holder.bind(story)
        }
        holder.itemView.animation =
            AnimationUtils.loadAnimation(holder.itemView.context, R.anim.item_animation)
    }

    class MyViewHolder(private val binding: ItemStoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(story: StoryItem) {
            binding.tvProfile.text = story.name
            val profile = story.name ?: ""
            val caption = story.description ?: ""
            val span = SpannableStringBuilder().apply {
                append(profile)
                setSpan(StyleSpan(Typeface.BOLD), 0, profile.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                append(" ")
                append(caption)
            }
            binding.tvProfileCaption.text = span
            Glide.with(itemView.context)
                .load(story.photoUrl)
                .error(R.drawable.outline_broken_image_24)
                .into(binding.imgStory)

            if (story.createdAt != null) {
                binding.tvCreatedAt.text = formattedDate(story.createdAt)
                binding.tvCreatedAt.visibility = View.VISIBLE
            } else {
                binding.tvCreatedAt.visibility = View.GONE
            }

            binding.root.setOnClickListener {
                val context = binding.root.context
                val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    itemView.context as Activity,
                    binding.imgStory,
                    "story_image"
                )

                val intent = Intent(context, StoryDetailActivity::class.java)
                intent.putExtra("story_id", story.id)

                context.startActivity(intent, options.toBundle())
            }
        }
    }


    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<StoryItem>() {
            override fun areItemsTheSame(oldItem: StoryItem, newItem: StoryItem): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: StoryItem, newItem: StoryItem): Boolean {
                return oldItem == newItem
            }
        }
    }
}