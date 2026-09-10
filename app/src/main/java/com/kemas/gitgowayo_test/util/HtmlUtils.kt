package com.kemas.gitgowayo_test.util

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.fromHtml
import androidx.core.text.HtmlCompat

// Deprecated
fun String.stripHtml(): String {
    return HtmlCompat.fromHtml(this, HtmlCompat.FROM_HTML_MODE_COMPACT)
        .toString()
        .trim()
}

fun String.toAnnotatedString(): AnnotatedString =
    AnnotatedString.fromHtml(this)