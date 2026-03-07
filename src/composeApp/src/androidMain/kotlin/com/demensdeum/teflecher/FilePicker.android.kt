package com.demensdeum.teflecher

actual suspend fun loadQuizFile(): String? {
    // Note: Android requires Intent.ACTION_GET_CONTENT which needs context.
    // For now, this returns null so the caller can fall back or handle it.
    return null
}
