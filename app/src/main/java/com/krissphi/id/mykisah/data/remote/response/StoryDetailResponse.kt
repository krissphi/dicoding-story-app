package com.krissphi.id.mykisah.data.remote.response

import com.google.gson.annotations.SerializedName

data class StoryDetailResponse(

	@field:SerializedName("error")
	val error: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("story")
	val story: StoryItem? = null
)

