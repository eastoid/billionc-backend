package com.debouncewrite.debouncev2.infrastructure.security.ratelimiter

import com.debouncewrite.debouncev2.common.util.NetworkUtils
import org.springframework.core.annotation.Order
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono


@Order(3)
@Component
class RateLimitWebFilter : WebFilter {

    val rateLimiter = HttpRateLimiter()

    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        val ip: String? = NetworkUtils.getRealIp(exchange.request)
        val path = exchange.request.path.value()

        if (ip == null || !rateLimiter.requestAllowed(ip, path)) {
            exchange.response.statusCode = HttpStatus.TOO_MANY_REQUESTS
            val message = "Too many requests."
            val buffer: DataBuffer = exchange.response.bufferFactory().wrap(message.toByteArray())
            return exchange.response.writeWith(Mono.just(buffer))
        }

        // Allow the request to proceed
        return chain.filter(exchange)
    }

}