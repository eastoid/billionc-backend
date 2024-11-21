package com.debouncewrite.debouncev2.infrastructure.security

//import com.debouncewrite.debouncev2.common.model.Result
//import com.debouncewrite.debouncev2.common.service.RedisService
//import com.debouncewrite.debouncev2.common.util.StringUtils
//import com.debouncewrite.debouncev2.infrastructure.config.constants.Properties
//import com.debouncewrite.debouncev2.infrastructure.config.constants.RedisKeys
//import com.debouncewrite.debouncev2.infrastructure.security.ipblacklist.model.UserBlacklistMode
//import com.debouncewrite.debouncev2.application.user.model.Status
//import kotlinx.coroutines.reactive.awaitFirstOrNull
//import kotlinx.coroutines.reactor.mono
//import kotlinx.serialization.json.*
//import org.springframework.http.MediaType
//import org.springframework.stereotype.Service
//import org.springframework.web.reactive.function.BodyInserters
//import org.springframework.web.reactive.function.client.*
//import reactor.core.publisher.Mono
//
//@Service
//class SecurityService(
//    private val redis: RedisService,
//    private val webClient: WebClient,
//) {
//
//    suspend fun isIpBlacklisted(ip: String): Boolean? {
//        val key = RedisKeys.Security.blacklistStatus(ip)
//        val redisResult = redis.get(key)
//        if (redisResult == "1") return true
//        if (redisResult == "0") return false
//
//        val blacklist = userBlacklistService.isBlacklisted(UserBlacklistMode.IP, ip)
//            ?: return null
//
//        val value = if (blacklist) "1" else "0"
//        redis.save(key, value)
//        redis.expire(key, 259200)
//        return blacklist
//    }
//
//}