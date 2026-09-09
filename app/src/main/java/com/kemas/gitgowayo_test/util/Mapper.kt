package com.kemas.gitgowayo_test.util

import com.kemas.gitgowayo_test.data.remote.dto.TvShowDto
import com.kemas.gitgowayo_test.domain.model.TvShow

fun TvShowDto.toDomain(): TvShow {
    return TvShow(
        id = id,
        name = name,
        summary = summary ?: "",
        premiered = premiered ?: "Unknown",
        url = url ?: "",
        imageMedium = image?.medium ?: "",
        imageOriginal = image?.original ?: "",
        rating = rating?.average
    )
}