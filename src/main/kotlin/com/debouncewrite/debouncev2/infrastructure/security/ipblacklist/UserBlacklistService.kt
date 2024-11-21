package com.debouncewrite.debouncev2.infrastructure.security.ipblacklist

//import com.debouncewrite.debouncev2.common.service.RedisService
//import com.debouncewrite.debouncev2.infrastructure.security.ipblacklist.model.UserBlacklistMode
//import com.debouncewrite.debouncev2.infrastructure.security.ipblacklist.repository.UserBlacklistRepository
//import de.huxhorn.sulky.ulid.ULID
//import kotlinx.coroutines.reactive.awaitFirstOrNull
//import kotlinx.coroutines.reactor.mono
//import org.springframework.stereotype.Service
//import reactor.core.publisher.Mono
//
//@Service
//class UserBlacklistService(
//    private val userBlacklistRepository: UserBlacklistRepository,
//    private val redis: RedisService
//) {
//
//    suspend fun isBlacklisted(mode: UserBlacklistMode, ip: String): Boolean? {
//        return isBlacklisted(mode, null, ip)
//    }
//
//    suspend fun isBlacklisted(mode: UserBlacklistMode, userId: ULID.Value): Boolean? {
//        return isBlacklisted(mode, userId, null)
//    }
//
//    private suspend fun isBlacklisted(mode: UserBlacklistMode, userId: ULID.Value?, ip: String?): Boolean? {
//        try {
//            val b = userBlacklistRepository.checkBlacklist(mode, userId, ip).awaitFirstOrNull()
//            return b != null
//        } catch (e: Exception) {
//            return null
//        }
//    }
//
//
//}