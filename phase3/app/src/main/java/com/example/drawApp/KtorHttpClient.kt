package com.example.drawApp

import io.ktor.client.*
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging

object KtorHttpClient {
    val httpClient: HttpClient by lazy {
        HttpClient {
            install(Logging) {
                level = LogLevel.ALL
            }
            // Add any other configurations if need here/
        }
    }
}


