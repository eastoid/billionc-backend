package com.debouncewrite.debouncev2.infrastructure.config

import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.event.EventListener
import org.springframework.web.reactive.function.client.WebClient


@Configuration
class AppConfig(
) {

    @Bean
    fun webClient(): WebClient {
        return WebClient.builder().build()
    }

    @EventListener(ApplicationReadyEvent::class)
    suspend fun startupLog() {
    }


}