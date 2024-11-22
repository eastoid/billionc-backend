package com.debouncewrite.debouncev2.modules.checkboxes.service

import com.debouncewrite.debouncev2.common.util.ConcurrentBitSet
import com.debouncewrite.debouncev2.common.util.ULIDGenerator
import com.debouncewrite.debouncev2.common.util.unixNow
import com.debouncewrite.debouncev2.infrastructure.rsocket.service.RsocketService
import com.debouncewrite.debouncev2.modules.checkboxes.repository.CheckboxRepository
import com.debouncewrite.debouncev2.modules.checkboxes.model.Checkboxes
import kotlinx.coroutines.*
import org.springframework.boot.context.event.ApplicationStartedEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong
import kotlin.system.exitProcess

@Service
class CheckboxService(
    private val checkboxRepository: CheckboxRepository,
    private val checkboxMetadataService: CheckboxMetadataService,
    private val checkboxSubscriptionService: CheckboxSubscriptionService,
    private val rsocketService: RsocketService,
) {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private val bitSet = ConcurrentBitSet(1_000_000_000)
    val clickCount = AtomicLong()
    val checkedBoxes = AtomicInteger()

    private var lastPersistedBitHash: Int? = null


    fun setBox(index: Int, value: Boolean) {
        clickCount.incrementAndGet()
        if (value) {
            bitSet.setBit(index)
            checkedBoxes.incrementAndGet()
        } else {
            bitSet.clearBit(index)
            checkedBoxes.decrementAndGet()
        }
    }

    fun getBoxRange(chunkIndex: Int): List<Boolean> {
        val startIndex = chunkIndex * 200
        val endIndex = startIndex + 199
        return bitSet.getBitRange(startIndex, endIndex)
    }

    @EventListener(ApplicationStartedEvent::class)
    private suspend fun onStart() {
        loadCheckboxes()
        loadClickCount()
        loadCheckedCount()

        // Loops
        scope.launch {
            loop_persistClickCount()
        }

        scope.launch {
            loop_persistCheckedCount()
        }

        scope.launch {
            loop_persistCheckboxBits()
        }

        scope.launch {
            loop_sendMetadata()
        }
    }

    suspend fun loadCheckboxes() {
        val dbResult = getLatestBits()
        if (dbResult != null) {
            println("SETTING BITS FROM DATABASE")
            bitSet.setBitsFromByteArray(dbResult.bytes)
            return
        }
        println("SETTING NEW BITS")
        bitSet.setAllBits()
    }

    suspend fun loadClickCount() {
        val countResult = checkboxMetadataService.getClicks()
        if (countResult.hasError) {
            repeat(3) { println("FAILED TO LOAD CLICK COUNT FROM DB") }
            exitProcess(1)
        }

        val count = countResult.valueOrNull ?: 0
        clickCount.set(count)
    }

    suspend fun loadCheckedCount() {
        val checkedResult = checkboxMetadataService.getCheckedCount()
        if (checkedResult.hasError) {
            repeat(3) { println("FAILED TO LOAD CHECKED COUNT FROM DB") }
            exitProcess(1)
        }

        val count = checkedResult.valueOrNull ?: 0
        checkedBoxes.set(count)
    }

    private suspend fun loop_persistClickCount() {
        var previousCount = clickCount.get()
        while(true) {
            delay(60000)
            runCatching {
                val count = clickCount.get()
                if (count == previousCount) return@runCatching
                checkboxMetadataService.setClicks(count)
                previousCount = count
            }
        }
    }

    private suspend fun loop_persistCheckedCount() {
        var previousCount = checkedBoxes.get()
        while(true) {
            delay(60000)
            runCatching {
                val count = checkedBoxes.get()
                if (count == previousCount) return@runCatching
                checkboxMetadataService.setCheckedCount(count)
                previousCount = count
            }
        }
    }

    private suspend fun loop_persistCheckboxBits() {
        while(true) {
            delay(7200000) // 2 hrs
            runCatching {
                val hash = bitSet.hashCode()
                val previousHash = lastPersistedBitHash

                // Prevent persisting bits if they havent changed
                persistData(hash == previousHash)
            }
        }
    }

    private suspend fun loop_sendMetadata() {
        while (true) {
            delay(60000) // 2 minutes
            runCatching {
                val clicks = clickCount.get()
                val checks = checkedBoxes.get()
                val json = """
                    {"clickCount":$clicks, "checkCount":$checks}
                """.trimIndent()
                rsocketService.sendToAllConnections(json, "metadata")
            }
        }
    }

    suspend fun persistData(preventBitPersistence: Boolean = false) {
        println("Persisting checkboxes.")

        val clicks = clickCount.get()
        val checkedBoxes = checkedBoxes.get()

        if (!preventBitPersistence) {
            val checkboxes = Checkboxes(ULIDGenerator.generate(), bitSet.bytes, unixNow())
            insertBits(checkboxes)
        }

        checkboxMetadataService.setClicks(clicks)
        checkboxMetadataService.setCheckedCount(checkedBoxes)
    }

    suspend fun insertBits(data: Checkboxes): Boolean {
        deleteOldBits()
        try {
            println("Inserting binary data with ID: ${data.id}, size: ${data.bytes.size} bytes")
            checkboxRepository.insert(data.id, data.bytes, data.date)
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }

    suspend fun deleteOldBits() {
        try {
            checkboxRepository.deleteOld(unixNow(), 86400)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun getLatestBits(): Checkboxes? {
        try {
            return checkboxRepository.getLatest()
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }
}