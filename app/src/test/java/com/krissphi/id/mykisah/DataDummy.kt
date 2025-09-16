package com.krissphi.id.mykisah

import com.krissphi.id.mykisah.data.remote.response.StoryItem

object DataDummy {
    fun generateDummyStoryResponse(): List<StoryItem> {
        val items: MutableList<StoryItem> = arrayListOf()
        for (i in 0..100) {
            val story = StoryItem(
                id = "story-id-$i",
                name = "User $i",
                description = "Description for story $i",
                photoUrl = "https://example.com/photo$i.jpg",
                createdAt = "2023-09-15T08:00:00.$i",
                lat = null,
                lon = null
            )
            items.add(story)
        }
        return items
    }
}