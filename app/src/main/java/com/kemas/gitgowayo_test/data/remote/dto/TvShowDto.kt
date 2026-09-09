package com.kemas.gitgowayo_test.data.remote.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TvShowDto(
    val id: Int,
    val name: String,
    val summary: String?,
    val premiered: String?,
    val url : String?,
    val image: ImageDto?,
    val rating: RatingDto?
    )

@JsonClass(generateAdapter = true)
data class ImageDto(
    val medium: String?,
    val original: String?
)

@JsonClass(generateAdapter = true)
data class RatingDto(
    val average: Double?
)