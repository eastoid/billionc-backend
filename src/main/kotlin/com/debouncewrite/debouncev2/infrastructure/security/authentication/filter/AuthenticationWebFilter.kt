package com.debouncewrite.debouncev2.infrastructure.security.authentication.filter

import com.debouncewrite.debouncev2.infrastructure.security.authentication.model.AuthPrincipal
import com.debouncewrite.debouncev2.common.util.NetworkUtils
import com.debouncewrite.debouncev2.common.util.StringUtils
import com.debouncewrite.debouncev2.infrastructure.config.constants.ContextKeys
import com.debouncewrite.debouncev2.infrastructure.config.constants.Texts
import com.debouncewrite.debouncev2.infrastructure.config.validation.ValidationService
import com.debouncewrite.debouncev2.infrastructure.security.EndpointSecurityConfigs
import com.debouncewrite.debouncev2.infrastructure.security.ratelimiter.model.SecurityEndpointConfig
import com.debouncewrite.debouncev2.application.user.model.Role
import com.debouncewrite.debouncev2.application.user.model.Status
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactor.mono
import org.springframework.core.annotation.Order
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono
import java.time.Instant

@Order(2)
@Component
class AuthenticationWebFilter(
    private val validationService: ValidationService,
) : WebFilter {

    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        return mono {
            val ipAddress = NetworkUtils.getRealIp(exchange.request) ?: return@mono internalErrorResponse(exchange, chain).awaitFirstOrNull()
            return@mono chain.filter(exchange).contextWrite { it.put(ContextKeys.ipAddress, ipAddress) }.awaitFirstOrNull()
        }
    }


    fun internalErrorResponse(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        val body = StringUtils.errorJsonResponse("internal-error", "Error authenticating request.")
        return immediateResponse(exchange, chain, 500, body)
    }


    fun immediateResponse(exchange: ServerWebExchange, chain: WebFilterChain, status: Int, body: String): Mono<Void> {
        exchange.response.setRawStatusCode(status)
        exchange.response.headers.contentType = MediaType.TEXT_PLAIN

        val buffer = exchange.response.bufferFactory().wrap(body.toByteArray())
        return exchange.response.writeWith(Mono.just(buffer))
    }


}