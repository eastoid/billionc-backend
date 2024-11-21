package com.debouncewrite.debouncev2.common.util

import com.debouncewrite.debouncev2.infrastructure.security.authentication.model.AuthPrincipal
import com.debouncewrite.debouncev2.infrastructure.config.constants.ContextKeys
import kotlinx.coroutines.reactor.ReactorContext
import kotlinx.coroutines.reactor.mono
import reactor.core.publisher.Mono
import reactor.util.context.Context
import kotlin.coroutines.coroutineContext

object UtilFuns {

//    fun getPrincipal(): Mono<AuthPrincipal> {
//        return Mono.deferContextual { contextView ->
//            return@deferContextual Mono.just(contextView.get(ContextKeys.authPrincipal))
//        }
//    }

}