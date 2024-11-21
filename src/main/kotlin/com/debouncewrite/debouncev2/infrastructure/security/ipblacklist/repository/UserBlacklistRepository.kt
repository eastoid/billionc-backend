package com.debouncewrite.debouncev2.infrastructure.security.ipblacklist.repository

//import com.debouncewrite.debouncev2.infrastructure.security.ipblacklist.model.UserBlacklist
//import com.debouncewrite.debouncev2.infrastructure.security.ipblacklist.model.UserBlacklistMode
//import de.huxhorn.sulky.ulid.ULID
//import org.springframework.data.r2dbc.repository.Query
//import org.springframework.data.r2dbc.repository.R2dbcRepository
//import org.springframework.stereotype.Repository
//import reactor.core.publisher.Mono
//
//@Repository
//interface UserBlacklistRepository : R2dbcRepository<UserBlacklist, ULID.Value> {
//
//    @Query("INSERT INTO userblacklist " +
//            "(id, user_id, mode, ip, blacklistedat, reason) " +
//            "VALUES " +
//            "(:id::uuid, :userId::uuid, :mode::userblacklistmode, :ip, :blacklistedAt, :reason)")
//    fun blacklist(
//        id: ULID.Value,
//        userId: ULID.Value?,
//        mode: UserBlacklistMode,
//        ip: String,
//        blacklistedAt: Long,
//        reason: String
//    ): Mono<Boolean>
//
//    @Query("SELECT * FROM userblacklist WHERE mode = :mode::userblacklistmode AND (user_id = :userId::uuid OR ip = :ip)")
//    fun checkBlacklist(mode: UserBlacklistMode, userId: ULID.Value?, ip: String?): Mono<UserBlacklist>
//
//}