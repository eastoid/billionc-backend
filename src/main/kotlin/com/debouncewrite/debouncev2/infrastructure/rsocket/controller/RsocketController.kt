package com.debouncewrite.debouncev2.infrastructure.rsocket.controller

import com.debouncewrite.debouncev2.common.controller.AbstractController
import com.debouncewrite.debouncev2.common.service.RedisService
import com.debouncewrite.debouncev2.infrastructure.config.constants.RedisKeys
import com.debouncewrite.debouncev2.infrastructure.config.validation.ValidationService
import com.debouncewrite.debouncev2.infrastructure.rsocket.service.RsocketRateLimitService
import com.debouncewrite.debouncev2.infrastructure.rsocket.service.RsocketService
import com.debouncewrite.debouncev2.modules.checkboxes.service.CheckboxService
import com.debouncewrite.debouncev2.modules.checkboxes.service.CheckboxSubscriptionService
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.messaging.rsocket.RSocketRequester
import org.springframework.messaging.rsocket.annotation.ConnectMapping
import org.springframework.stereotype.Controller

@Controller
class RsocketController(
    private val validationService: ValidationService,
    private val rsocketService: RsocketService,
    private val redis: RedisService,
    private val checkboxService: CheckboxService,
    private val rateLimitService: RsocketRateLimitService,
    private val checkboxSubscriptionService: CheckboxSubscriptionService,
) : AbstractController() {

    @ConnectMapping
    suspend fun connectMapping(requester: RSocketRequester, @Payload ipToken: String) {
        if (ipToken.length != 64) return requester.dispose()

        val ip = redis.get(RedisKeys.Security.ipToken(ipToken))
        if (ip == null || !(ip.contains(".") || ip.contains(":"))) return requester.dispose()

        if (!rateLimitService.isRequestAllowed(ip, "con")) return requester.dispose()

        rsocketService.add(requester, ip)
    }

    @MessageMapping("unsub")
    suspend fun rsocketUnsubscribeMapping(requester: RSocketRequester, @Payload payload: String): String {
        val ip = rsocketService.getIp(requester) ?: return "Request rate limiter failed to load your IP address."
        if (!rateLimitService.isRequestAllowed(ip, "uns")) return "ratelimit"

        val id = payload.toIntOrNull() ?: return "Chunk index must be a number."
        if (id >= 5_000_000) return "Chunk index is too big."
        if (id < 0) return "Chunk index is negative."

        checkboxSubscriptionService.unsubscribe(requester.hashCode(), id)
        return Json.encodeToString("ok")
    }

    @MessageMapping("gas")
    suspend fun rsocketGetMapping(requester: RSocketRequester, @Payload payload: String): String {
        val ip = rsocketService.getIp(requester) ?: return "Request rate limiter failed to load your IP address."
        if (!rateLimitService.isRequestAllowed(ip, "get")) return "ratelimit"

        val id = payload.toIntOrNull() ?: return "Chunk index must be a number."
        if (id >= 5_000_000) return "Chunk index is too big."
        if (id < 0) return "Chunk index is negative."

        val chunk = checkboxService.getBoxRange(id).map { if (it) 1 else 0 }

        checkboxSubscriptionService.subscribe(requester.hashCode(), id)
        return Json.encodeToString(chunk)
    }

    @MessageMapping("set")
    suspend fun rsocketSetMapping(requester: RSocketRequester, @Payload payload: String): String {
        val ip = rsocketService.getIp(requester) ?: return "Request rate limiter failed to load your IP address."
        if (!rateLimitService.isRequestAllowed(ip, "set")) return "ratelimit"

        val split = payload.split(";")
        if (split.size != 2) return "Couldn't update checkbox. Invalid request."

        val rawIndex = split[0]
        val rawValue = split[1]

        val index = rawIndex.toIntOrNull() ?: return "Checkboxes index is null."
        if (index < 0 || index > 999999999) return "Checkboxes index is in an invalid range."
        val value = rawValue.toBooleanStrictOrNull() ?: return "Checkboxes value is invalid."

        checkboxService.setBox(index, value)

        val chunkIndex = index / 200

        val subscribers = checkboxSubscriptionService.getSubscribedUsers(chunkIndex)
        rsocketService.sendToAll(payload, "mod", subscribers, requester.hashCode())
        return "ok"
    }

    @MessageMapping("getsid")
    fun rsocketGetSidMapping(requester: RSocketRequester): String {
        return requester.hashCode().toString()
    }
}