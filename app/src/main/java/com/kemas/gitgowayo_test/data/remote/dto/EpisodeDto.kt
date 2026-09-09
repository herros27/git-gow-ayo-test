package com.kemas.gitgowayo_test.data.remote.dto


import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class EpisodeDto(
    val id: Int,
    val name: String,
    val season: Int,
    val number: Int?,
    val rating: RatingDto?,
    val image: ImageDto?,
    val summary: String?
)