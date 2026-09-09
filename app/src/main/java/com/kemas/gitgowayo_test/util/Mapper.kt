package com.kemas.gitgowayo_test.util

import com.kemas.gitgowayo_test.data.remote.dto.CastDto
import com.kemas.gitgowayo_test.data.remote.dto.EpisodeDto
import com.kemas.gitgowayo_test.data.remote.dto.SeasonDto
import com.kemas.gitgowayo_test.data.remote.dto.TvShowDto
import com.kemas.gitgowayo_test.domain.model.Cast
import com.kemas.gitgowayo_test.domain.model.Episode
import com.kemas.gitgowayo_test.domain.model.Season
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

fun CastDto.toDomain(): Cast {
    return Cast(
        id = person.id,
        name = person.name,
        characterName = character.name,
        imageUrl = person.image?.medium ?: person.image?.original ?: ""
    )
}
fun SeasonDto.toDomain(): Season {
    return Season(
        id = id,
        number = number,
        episodeCount = episodeOrder ?: 0,
        premiereDate = premiereDate ?: "Unknown",
        imageUrl = image?.medium ?: image?.original ?: ""
    )
}

fun EpisodeDto.toDomain(): Episode {
    return Episode(
        id = id,
        name = name,
        season = season,
        number = number ?: 0,
        rating = rating?.average,
        imageUrl = image?.medium ?: image?.original ?: "",
        summary = summary ?: ""
    )
}