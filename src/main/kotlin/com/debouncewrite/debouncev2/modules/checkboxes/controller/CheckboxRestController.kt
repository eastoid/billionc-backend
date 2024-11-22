package com.debouncewrite.debouncev2.modules.checkboxes.controller

import com.debouncewrite.debouncev2.common.controller.AbstractController
import com.debouncewrite.debouncev2.infrastructure.rsocket.service.RsocketService
import com.debouncewrite.debouncev2.modules.checkboxes.service.CheckboxService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/checkbox")
class CheckboxRestController(
    private val checkboxService: CheckboxService,
    private val rsocketService: RsocketService
) : AbstractController() {

    @PostMapping("/metadata")
    suspend fun getMetadataMapping(
        @RequestPart("userCount", required = false) getUserCount: String?
    ): ResponseEntity<String> {
        val clicks = checkboxService.clickCount.get()
        val checks = checkboxService.checkedBoxes.get()

        val result = if (getUserCount == null) {
            """
                {"clickCount":$clicks,"checkedCount":$checks}
            """.trimIndent()
        } else {
            val userCount = rsocketService.connectionCount.get()
            """
                {"clickCount":$clicks,"checkedCount":$checks,"userCount":$userCount}
            """.trimIndent()
        }

        return ok(result)
    }

}