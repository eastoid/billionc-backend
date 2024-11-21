package com.debouncewrite.debouncev2.common.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1")
class UtilController(

) : AbstractController() {

    @GetMapping("/ping")
    fun pingMapping() = ok("pong")

}