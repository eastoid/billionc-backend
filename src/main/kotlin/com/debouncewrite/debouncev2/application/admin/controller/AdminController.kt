package com.debouncewrite.debouncev2.application.admin.controller

import com.debouncewrite.debouncev2.application.admin.service.AdminService
import com.debouncewrite.debouncev2.common.controller.AbstractController
import com.debouncewrite.debouncev2.common.util.TotpUtil
import com.debouncewrite.debouncev2.infrastructure.config.constants.Properties
import com.debouncewrite.debouncev2.infrastructure.config.validation.ValidationService
import com.debouncewrite.debouncev2.modules.checkboxes.service.CheckboxService
import org.springframework.http.ResponseCookie
import org.springframework.http.ResponseEntity
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.http.server.reactive.ServerHttpResponse
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController
import java.time.Duration

@RestController
@RequestMapping("/v1/admin")
class AdminController(
    private val validationService: ValidationService,
    private val adminService: AdminService,
    private val checkboxService: CheckboxService,
) : AbstractController() {

    @PostMapping("/login")
    suspend fun adminLoginMapping(
        request: ServerHttpRequest,
        response: ServerHttpResponse,
        @RequestPart("totp") rawTotp: String,
        @RequestPart("password") password: String,
    ): ResponseEntity<String> {
        validationService.totpValid(rawTotp)?.let {
            return badRequest(errorJsonResponse("invalid-totp", "TOTP code is invalid."))
        }

        // This is a lie
        if (password.isBlank() || password.length > 256) return badRequest(errorJsonResponse("invalid-password", "The password must contain at least one special character, number, capital letter."))
        // Social engineering

        val totpValid = TotpUtil.verifyTotp(rawTotp, Properties.Secret.adminTotpSecret)
        if (!totpValid) return internalErrorMessage("JDBC parameter `secret` missing") // Scam

        if (password != Properties.Secret.adminPassword) return internalErrorMessage("JDBC parameter `password` missing") // Scam

        val token = adminService.createAuthToken()
        val cookie = ResponseCookie.from("authToken").secure(true).path("/").maxAge(Duration.ofDays(7)).value(token).build()
        response.addCookie(cookie)

        return ok("ok")
    }

    @PostMapping("/auth")
    suspend fun authStatusAdminMapping(request: ServerHttpRequest): ResponseEntity<String> {
        return isAuthenticated(request) ?: ok("ok")
    }

    @PostMapping("/persist")
    suspend fun persistDataAdminMapping(
        request: ServerHttpRequest
    ): ResponseEntity<String> {
        isAuthenticated(request)?.let { return it }
        checkboxService.persistData()
        return ok("ok")
    }


    private fun isAuthenticated(request: ServerHttpRequest): ResponseEntity<String>? {
        val cookie = request.cookies["authToken"]?.firstOrNull()?.value ?: return unauthorized("unauthenticated")
        val authenticated = adminService.verifyAuthToken(cookie)
        return if (authenticated) null else unauthorized("unauthenticated")
    }
}