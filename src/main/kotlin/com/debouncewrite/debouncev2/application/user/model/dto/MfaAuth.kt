package com.debouncewrite.debouncev2.application.user.model.dto

//import com.debouncewrite.debouncev2.common.util.TotpUtil
//import org.springframework.data.relational.core.mapping.Column
//import org.springframework.data.relational.core.mapping.Table
//
//@Table("users")
//data class MfaAuth(
//    @Column("password_hash")
//    private val authHash: String,
//    @Column("password_salt")
//    private val authSalt: String,
//    @Column("totp_mfa_secret")
//    val totpSecret: String?,
//) {
//
//    val password = authSalt + authHash
//
//    fun matches(inputAuthHash: String, totp: String?): String? {
//        if (totpSecret != null) {
//            totp ?: return "mfa"
//            val code = TotpUtil.generateTotpForSecret(totpSecret)
//            if (code != totp) return "mfa"
//        }
//
//        if (inputAuthHash != password) return "password"
//        return null
//    }
//
//}
