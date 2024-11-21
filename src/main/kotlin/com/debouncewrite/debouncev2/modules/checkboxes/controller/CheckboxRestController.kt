package com.debouncewrite.debouncev2.modules.checkboxes.controller

import com.debouncewrite.debouncev2.common.controller.AbstractController
import com.debouncewrite.debouncev2.modules.checkboxes.service.CheckboxMetadataService
import com.debouncewrite.debouncev2.modules.checkboxes.service.CheckboxService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/checkbox")
class CheckboxRestController(
    private val checkboxService: CheckboxService
) : AbstractController() {

    @GetMapping("/metadata")
    suspend fun getMetadataMapping(): ResponseEntity<String> {
        val clicks = checkboxService.clickCount.get()
        val checks = checkboxService.checkedBoxes.get()

        val result = """
            {"clickCount":$clicks,"checkedCount":$checks}
        """.trimIndent()
        return ok(result)
    }

}