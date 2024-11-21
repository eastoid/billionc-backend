package com.debouncewrite.debouncev2.modules.checkboxes.service

import com.debouncewrite.debouncev2.modules.checkboxes.repository.MetadataRepository
import org.springframework.stereotype.Service
import com.debouncewrite.debouncev2.common.model.Result
import com.debouncewrite.debouncev2.common.util.toResult
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener

@Service
class CheckboxMetadataService(
    private val metadataRepository: MetadataRepository
) {

    suspend fun getClicks(): Result<Long> {
        try {
            return metadataRepository.getClickCount()?.toLongOrNull()?.toResult() ?: Result.notFound()
        } catch (e: Exception) {
            e.printStackTrace()
            return Result.error("Failed to get click count from db")
        }
    }

    suspend fun setClicks(number: Long) {
        try {
            return metadataRepository.setClicks(number.toString())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun setCheckedCount(number: Int) {
        try {
            return metadataRepository.setCheckedCount(number.toString())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun getCheckedCount(): Result<Int> {
        try {
            return metadataRepository.getCheckedCount()?.toIntOrNull()?.toResult() ?: Result.notFound()
        } catch (e: Exception) {
            e.printStackTrace()
            return Result.error("Failed to get checked box count from db")
        }
    }

}