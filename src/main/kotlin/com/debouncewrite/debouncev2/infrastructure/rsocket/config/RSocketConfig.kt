package com.debouncewrite.debouncev2.infrastructure.rsocket.config

import com.debouncewrite.debouncev2.common.util.print
import io.rsocket.SocketAcceptor
import io.rsocket.core.RSocketServer
import io.rsocket.plugins.InterceptorRegistry
import io.rsocket.plugins.RSocketInterceptor
import io.rsocket.plugins.SocketAcceptorInterceptor
import org.springframework.boot.rsocket.server.RSocketServerCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.Ordered
import org.springframework.core.io.buffer.DefaultDataBufferFactory
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.http.server.reactive.ServerHttpResponse
import org.springframework.messaging.rsocket.RSocketStrategies
import org.springframework.messaging.rsocket.annotation.support.RSocketMessageHandler
import org.springframework.web.reactive.HandlerMapping
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping
import org.springframework.web.reactive.socket.WebSocketHandler
import org.springframework.web.reactive.socket.WebSocketSession
import org.springframework.web.util.pattern.PathPatternRouteMatcher
import reactor.core.publisher.Mono
import java.util.concurrent.ConcurrentHashMap


@Configuration
class RSocketConfig(
) {

    @Bean
    fun rSocketStrategies(): RSocketStrategies {
        return RSocketStrategies.builder()
            .dataBufferFactory(DefaultDataBufferFactory())
            .routeMatcher(PathPatternRouteMatcher())
            .build()
    }

    @Bean
    fun rSocketMessageHandler(rSocketStrategies: RSocketStrategies): RSocketMessageHandler {
        val messageHandler = RSocketMessageHandler()
        messageHandler.rSocketStrategies = rSocketStrategies
        return messageHandler
    }

    @Bean
    fun rSocketServerCustomizer(): RSocketServerCustomizer {
        return RSocketServerCustomizer { rSocketServer: RSocketServer ->
            rSocketServer.interceptors { interceptors: InterceptorRegistry ->
                interceptors.forSocketAcceptor { acceptor: SocketAcceptor ->
                    acceptor
                }
            }
        }
    }

}