package com.debouncewrite.debouncev2.infrastructure.rsocket.service

import com.debouncewrite.debouncev2.common.util.launch
import com.debouncewrite.debouncev2.modules.checkboxes.service.CheckboxSubscriptionService
import io.rsocket.Payload
import io.rsocket.util.DefaultPayload
import kotlinx.coroutines.reactive.awaitFirstOrNull
import org.springframework.messaging.rsocket.RSocketRequester
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import java.nio.charset.StandardCharsets
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

@Service
class RsocketService(private val checkboxSubscriptionService: CheckboxSubscriptionService) {

    // Rsocket, IP address
    private val connections = ConcurrentHashMap<RSocketRequester, String>()
    // RSocket hashcode, rsocket
    private val hashCodes = ConcurrentHashMap<Int, RSocketRequester>()
    // Ip, connection count
    private val connectionCounts = ConcurrentHashMap<String, AtomicInteger>()


    fun add(rsocket: RSocketRequester, ip: String) {
        rsocket.rsocket() ?: return

        val count = connectionCounts.computeIfAbsent(ip) { AtomicInteger(0) }
        if (count.get() >= 6) {
            val payload = createPayload("Sorry, you have too many sessions open.", "notification")
            rsocket.rsocketClient().requestResponse(payload.toMono()).doFinally {
                runCatching { rsocket.dispose() }
            }.launch()

            return
        }
        count.incrementAndGet()

        connections[rsocket] = ip
        hashCodes[rsocket.hashCode()] = rsocket

        rsocket.rsocket()!!.onClose().doFinally {
            runCatching {
                connections.remove(rsocket)
                hashCodes.remove(rsocket.hashCode())

                connectionCounts.compute(ip) { _, count ->
                    count?.apply { decrementAndGet() }?.takeIf { it.get() > 0 }
                }

                checkboxSubscriptionService.unsubscribeAll(rsocket.hashCode())
            }.onFailure {
                println("Failed to remove rsocket session on disconnection: \n" + it.stackTraceToString())
            }
        }.subscribe()
    }

    fun remove(requester: RSocketRequester) {
        connections.remove(requester)
        runCatching {
            requester.dispose()
        }
    }

    fun getIp(requester: RSocketRequester): String? {
        return connections[requester]
    }

    suspend fun sendToAll(message: String, route: String, recipients: Collection<Int>, excludedRecipient: Int? = null) {
        val payload = createPayload(message, route)

        recipients.forEach { recipient ->
            if (recipient == excludedRecipient) return@forEach
            val rsocket = hashCodes[recipient] ?: return@forEach

            rsocket.rsocketClient().requestResponse(payload.toMono()).launch()
        }
    }

    suspend fun sendToAllConnections(message: String, route: String) {
        val payload = Mono.just(createPayload(message, route))

        // Send
        connections.keys.forEach { requester ->
            requester.rsocketClient().requestResponse(payload).awaitFirstOrNull()
        }
    }

    fun createPayload(message: String, route: String): Payload {
        // Route
        val routeBytes: ByteArray = route.toByteArray(StandardCharsets.UTF_8)
        val routeLength = routeBytes.size
        val mimeTypeId = 0x7E.toByte()
        val metadata = buildString {
            append(Char(mimeTypeId.toUShort()))
            append(routeLength.toChar())
            append(route)
        }

        // Message
        val payload = DefaultPayload.create(message, metadata)
        return payload
    }
}