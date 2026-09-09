package com.kemas.gitgowayo_test.data.remote.dto


import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CastDto(
    val person: PersonDto,
    val character: CharacterDto
)

@JsonClass(generateAdapter = true)
data class PersonDto(
    val id: Int,
    val name: String,
    val image: ImageDto?
)

@JsonClass(generateAdapter = true)
data class CharacterDto(
    val id: Int,
    val name: String
)