package com.debouncewrite.debouncev2.common.controller

import com.debouncewrite.debouncev2.common.util.NetworkUtils
import com.debouncewrite.debouncev2.infrastructure.security.authentication.model.AuthPrincipal
import com.debouncewrite.debouncev2.common.util.StringUtils
import com.debouncewrite.debouncev2.infrastructure.config.constants.ContextKeys
import kotlinx.coroutines.reactor.ReactorContext
import org.springframework.http.ResponseEntity
import org.springframework.http.server.reactive.ServerHttpRequest
import reactor.core.publisher.Mono
import kotlin.coroutines.coroutineContext

abstract class AbstractController {

    fun errorJsonResponse(error: String, message: String): String {
        return StringUtils.errorJsonResponse(error, message)
    }

    fun badRequest(body: String): ResponseEntity<String> = ResponseEntity.badRequest().body(body)

    fun ok(body: String): ResponseEntity<String> = ResponseEntity.ok(body)

    fun internal(body: String): ResponseEntity<String> = ResponseEntity.status(500).body(body)

    fun notFound(body: String): ResponseEntity<String> = ResponseEntity.status(404).body(body)

    fun unauthorized(body: String): ResponseEntity<String> = ResponseEntity.status(401).body(body)

    fun internalErrorMessage(message: String): ResponseEntity<String> = ResponseEntity.status(500).body(errorJsonResponse("internal-error", message))

    fun getRealIp(request: ServerHttpRequest): String? {
        return NetworkUtils.getRealIp(request)
    }

    fun getCensoredIp(request: ServerHttpRequest): String? {
        return NetworkUtils.getCensoredIp(request)
    }

//    fun getPrincipal(): Mono<AuthPrincipal> {
//        return Mono.deferContextual { contextView ->
//            return@deferContextual Mono.just(contextView.get<AuthPrincipal?>(ContextKeys.authPrincipal))
//        }
//    }
//
//    suspend fun getPrincipalAndAwait(): AuthPrincipal {
//        return coroutineContext[ReactorContext]?.context?.get<AuthPrincipal?>(ContextKeys.authPrincipal) ?: throw IllegalStateException("Principal is null when accessed in controller.")
//    }


}