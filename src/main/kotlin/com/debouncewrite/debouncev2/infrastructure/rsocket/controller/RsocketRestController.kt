package com.debouncewrite.debouncev2.infrastructure.rsocket.controller

import com.debouncewrite.debouncev2.common.controller.AbstractController
import com.debouncewrite.debouncev2.common.service.RedisService
import com.debouncewrite.debouncev2.common.util.StringUtils
import com.debouncewrite.debouncev2.infrastructure.config.constants.RedisKeys
import org.springframework.http.ResponseEntity
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@RequestMapping("/v1/rsocket")
class RsocketRestController(
    private val redis: RedisService,
) : AbstractController() {

    private val salt = StringUtils.randomString(24)

    @PostMapping("/idtoken")
    suspend fun getIpTokenMapping(request: ServerHttpRequest): ResponseEntity<String> {
        val realIp = getRealIp(request) ?: return badRequest(errorJsonResponse("invalid-ip", "Server did not receive your IP address."))
        val plaintext = realIp + salt
        val code = StringUtils.sha256Hash(plaintext)

        val key = RedisKeys.Security.ipToken(code)
        redis.save(key, realIp, 30)
        return ResponseEntity.ok(code)
    }

}