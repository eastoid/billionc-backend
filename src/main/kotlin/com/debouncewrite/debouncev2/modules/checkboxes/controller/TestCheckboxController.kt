package com.debouncewrite.debouncev2.modules.checkboxes.controller

import com.debouncewrite.debouncev2.common.controller.AbstractController
import com.debouncewrite.debouncev2.common.service.RedisService
import com.debouncewrite.debouncev2.modules.checkboxes.service.CheckboxService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.Instant

@RestController
@RequestMapping("/v0/checkbox")
class TestCheckboxController(
    private val redis: RedisService,
    private val checkboxService: CheckboxService
) : AbstractController() {

//    @GetMapping("/persist")
//    suspend fun perissnrttMapping(): ResponseEntity<String> {
////        redis.initializeBits("bits")
//        checkboxService.persistCheckboxes()
//
//        return ok("INITNIGA")
//    }
//
//    @GetMapping("/init")
//    suspend fun initMapping(): ResponseEntity<String> {
//        return internalErrorMessage("no")
////        redis.initializeBits("bits")
//
//        return ok("INITNIGA")
//    }
//
//    @GetMapping("/ones")
//    suspend fun onesMapping(): ResponseEntity<String> {
//        return internalErrorMessage("no")
//        redis.boxes_setBitsToOne("bits")
//
//        return ok("ONES LOL")
//    }
//
//    @GetMapping("/all")
//    suspend fun allMapping(): ResponseEntity<String> {
//        return internalErrorMessage("no")
//        checkboxService.getBoxRange(0).also { println(it); println(it.size) }
//        checkboxService.getBoxRange(4999999).also { println(it); println(it.size) }
//
//        return ok("ONES LOL")
//    }
//
//    @GetMapping("/do")
//    suspend fun doMapping(): ResponseEntity<String> {
//        return internalErrorMessage("no")
//        val xd = System.nanoTime()
//        redis.bitSet("bits", 850000000, true)
//        val beep = System.nanoTime()
//
//        println(xd)
//        println(beep - xd)
//        println(System.nanoTime())
//        println(System.nanoTime())
//        println(System.nanoTime())
//        println("==")
//        return ok("DOOD")
//    }
//
//    @GetMapping("/undo")
//    suspend fun undoMapping(): ResponseEntity<String> {
//        return internalErrorMessage("no")
//        val one = Instant.now().nano
//        redis.bitSet("bits", 850000000, false)
//
//        return ok("UNDO")
//    }
//
//    @GetMapping("/get")
//    suspend fun getMapping(): ResponseEntity<String> {
//        return internalErrorMessage("no")
//        return ok("${redis.boxes_bitGetRange(1000, 1)}")
//    }
}