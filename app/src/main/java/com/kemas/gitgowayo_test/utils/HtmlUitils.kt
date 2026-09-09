package com.kemas.gitgowayo_test.utils

import androidx.core.text.HtmlCompat

fun String.stripHtml(): String {
    return HtmlCompat.fromHtml(this, HtmlCompat.FROM_HTML_MODE_COMPACT)
        .toString()
        .trim()
}