package com.dicoding.picodiploma.loginwithanimation.data.response

import com.google.gson.annotations.SerializedName

data class ResponseClassification(

	@field:SerializedName("forum_link")
	var forumLink: String? = null,

	@field:SerializedName("image_url")
	val imageUrl: String? = null,

	@field:SerializedName("text")
	val text: String? = null,

	@field:SerializedName("classification")
	val classification: Classification? = null,

	@field:SerializedName("recommendations")
	val recommendations: List<String?>? = null
)

data class Classification(

	@field:SerializedName("difficulty")
	val difficulty: String? = null,

	@field:SerializedName("confidence")
	val confidence: Any? = null,

	@field:SerializedName("timestamp")
	val timestamp: String? = null
)
