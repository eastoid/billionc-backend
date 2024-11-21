package com.debouncewrite.debouncev2.common.service

import com.debouncewrite.debouncev2.common.util.collectMap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.data.redis.connection.BitFieldSubCommands
import org.springframework.data.redis.core.*
import org.springframework.stereotype.Service
import java.time.Duration


@Service
class RedisService(
    private val redisTemplate: ReactiveRedisTemplate<String, String>,
) {

    private val hashOps = redisTemplate.opsForHash<String, String>()
    private val valueOps = redisTemplate.opsForValue()
    private val setOps = redisTemplate.opsForSet()


    suspend fun save(key: String, value: String, expiration: Long? = null): Boolean {
        try {
            return valueOps.setAndAwait(key, value)
                .also { if (expiration != null) expire(key, expiration) }
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }

    suspend fun get(key: String): String? {
        try {
            return valueOps.getAndAwait(key)
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    suspend fun delete(key: String): Boolean {
        try {
            redisTemplate.deleteAndAwait(key)
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }

    suspend fun expire(key: String, seconds: Long): Boolean? {
        try {
            return redisTemplate.expireAndAwait(key, Duration.ofSeconds(seconds))
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    suspend fun getTtl(key: String): Long? {
        try {
            return redisTemplate.getExpireAndAwait(key)?.seconds
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    suspend fun increaseTtl(key: String, incr: Long): Boolean? {
        try {
            val current = getTtl(key)
            val new = (current ?: 0) + incr
            return expire(key, new)
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    suspend fun hashGet(key: String, hashKey: String): String? {
        try {
            return hashOps.getAndAwait(key, hashKey)
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    suspend fun hashAdd(key: String, hashKey: String, value: String): Boolean {
        try {
            hashOps.putAndAwait(key, hashKey, value)
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }

    suspend fun hashHasKey(key: String, hashKey: String): Boolean? {
        try {
            return hashOps.hasKeyAndAwait(key, hashKey)
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    suspend fun hashDelete(key: String, hashKey: String): Boolean {
        return try {
            hashOps.removeAndAwait(key, hashKey)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun hashGetAll(key: String): Map<String, String>? {
        try {
            val result = hashOps.entriesAsFlow(key).collectMap()
            if (result.isEmpty()) return null

            return result.filter { it.key != "ph" }
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    suspend fun hashPlaceholder(key: String): Boolean {
        return hashAdd(key, "ph", "ph")
    }

    suspend fun exists(key: String): Boolean {
        try {
            return redisTemplate.hasKeyAndAwait(key)
        } catch (e: Exception) {
            e.printStackTrace()
            delete(key)
            return false
        }
    }

    suspend fun setAdd(key: String, value: String): Boolean {
        try {
            setOps.addAndAwait(key, value)
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }

    suspend fun setRandom(key: String): String? {
        try {
            return setOps.randomMemberAndAwait(key)
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    suspend fun setScan(key: String): List<String>? {
        try {
            return setOps.scanAsFlow(key).toList()
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    suspend fun setGetAll(key: String): List<String>? {
        try {
            return setOps.members(key).collectList().awaitSingle().ifEmpty { null }
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    suspend fun setRemove(key: String, value: String): Boolean {
        try {
            setOps.removeAndAwait(key, value)
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }

    suspend fun size(key: String): Long? {
        try {
            val result = setOps.sizeAndAwait(key)
            return result
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    suspend fun increment(key: String): Long? {
        try {
            return valueOps.incrementAndAwait(key)
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }
    
    suspend fun boxes_initializeBits(key: String) {
        try {
            println("Initializing redis key $key with 1 BILLION zero bits")
            valueOps.setBitAndAwait(key, 1000000000, false)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun boxes_setBitsToOne(key: String) = coroutineScope {
        val numThreads = 200000 // Number of parallel tasks
        val totalBits = 1_000_000_000L
        val bitsPerTask = totalBits / numThreads

        println("Starting to set bits for key '$key' using $numThreads parallel tasks")

        (0 until numThreads).map { taskIndex ->
            async(Dispatchers.IO) {
                val start = taskIndex * bitsPerTask
                val end = if (taskIndex == numThreads - 1) totalBits else (taskIndex + 1) * bitsPerTask
                println("Task $taskIndex starting, handling range $start to $end")
                setBitsRange(key, start, end)
                println("Task $taskIndex completed for range $start to $end")
            }
        }.awaitAll()

        println("Completed setting all bits for key '$key'")
    }

    private suspend fun setBitsRange(key: String, start: Long, end: Long) {
        var i = start
        while (i < end) {
            val bitsToSet = minOf(63, end - i).toInt()

            val subCommands = BitFieldSubCommands.create()
                .set(BitFieldSubCommands.BitFieldType.unsigned(bitsToSet))
                .valueAt(i)
                .to((1L shl bitsToSet) - 1)

            redisTemplate.opsForValue()
                .bitFieldAndAwait(key, subCommands)

            i += bitsToSet
        }
    }

    suspend fun bitSet(key: String, index: Long, bit: Boolean) {
        try {
            valueOps.setBitAndAwait(key, index, bit)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun boxes_bitGetRange(index: Long, range: Int): List<Long?>? {
        try {
            val sub = BitFieldSubCommands.create()
                .get(BitFieldSubCommands.BitFieldType.unsigned(range))
                .valueAt(index)

            return valueOps.bitFieldAndAwait("bits", sub)
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

}