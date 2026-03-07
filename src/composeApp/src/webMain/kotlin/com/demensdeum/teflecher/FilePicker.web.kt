package com.demensdeum.teflecher

import kotlinx.browser.document
import kotlinx.coroutines.suspendCancellableCoroutine
import org.w3c.dom.HTMLInputElement
import org.w3c.files.FileReader
import kotlin.coroutines.resume

actual suspend fun loadQuizFile(): String? = suspendCancellableCoroutine { continuation ->
    try {
        val input = document.createElement("input") as HTMLInputElement
        input.type = "file"
        input.accept = ".json"

        input.onchange = {
            val file = input.files?.item(0)
            if (file != null) {
                val reader = FileReader()
                reader.onload = {
                    val content = reader.result.toString()
                    continuation.resume(content)
                }
                reader.onerror = {
                    continuation.resume(null)
                }
                reader.readAsText(file)
            } else {
                continuation.resume(null)
            }
            null
        }

        input.click()
    } catch (e: Exception) {
        continuation.resume(null)
    }
}
