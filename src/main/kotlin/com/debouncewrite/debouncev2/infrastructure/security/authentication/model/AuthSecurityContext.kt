package com.debouncewrite.debouncev2.infrastructure.security.authentication.model

import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContext
import java.security.Principal

data class AuthSecurityContext(
    val principal: AuthPrincipal
) : SecurityContext {

    override fun getAuthentication(): Authentication? = null

    override fun setAuthentication(authentication: Authentication?) {}

}