package com.kemas.gitgowayo_test.domain.model

data class Episode(
    val id: Int,
    val name: String,
    val season: Int,
    val number: Int,
    val rating: Double?,
    val imageUrl: String,
    val summary: String
)