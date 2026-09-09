package com.kemas.gitgowayo_test.data.remote.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SeasonDto(
    val id: Int,
    val number: Int,
    val name: String?,
    val episodeOrder: Int?,
    val premiereDate: String?,
    val image: ImageDto?
)