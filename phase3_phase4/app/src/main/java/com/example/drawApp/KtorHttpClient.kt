package com.example.drawApp

import io.ktor.client.*
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.json


object KtorHttpClient {
    val httpClient: HttpClient by lazy {
        HttpClient(CIO) {
            install(Logging) {
                level = LogLevel.ALL
            }

            install(ContentNegotiation) {
                json() // not sure weather i need this serialize stuff
            }

            // Add any other configurations if need here/
        }
    }
}


