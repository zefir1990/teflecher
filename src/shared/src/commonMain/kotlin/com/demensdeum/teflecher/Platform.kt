package com.demensdeum.teflecher

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform