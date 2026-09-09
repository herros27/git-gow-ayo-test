package com.kemas.gitgowayo_test.domain.model

data class TvShow(
    val id: Int,
    val name: String,
    val summary: String,
    val premiered: String,
    val url: String,
    val imageMedium: String,
    val imageOriginal: String,
    val rating: Double?
)
