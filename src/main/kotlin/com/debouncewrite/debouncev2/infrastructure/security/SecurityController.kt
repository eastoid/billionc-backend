package com.debouncewrite.debouncev2.infrastructure.security

import com.debouncewrite.debouncev2.common.controller.AbstractController
import com.debouncewrite.debouncev2.infrastructure.config.constants.Texts
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/security")
class SecurityController(

) : AbstractController() {

    @RequestMapping("/bip39list")
    fun bip39listMapping(): ResponseEntity<String> {
        return ok(Texts.bip39list)
    }

}