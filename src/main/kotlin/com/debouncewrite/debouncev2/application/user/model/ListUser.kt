package com.debouncewrite.debouncev2.application.user.model

import com.debouncewrite.debouncev2.infrastructure.config.serialization.UlidSerializer
import de.huxhorn.sulky.ulid.ULID
import kotlinx.serialization.Serializable
import org.springframework.data.annotation.Id
//import org.springframework.data.relational.core.mapping.Column
//import org.springframework.data.relational.core.mapping.Table
//
//@Table("users")
//@Serializable
//data class ListUser(
//    @Id
//    @Column("user_id")
//    @Serializable(with = UlidSerializer::class)
//    val userId: ULID.Value,
//    val email: String,
//    @Column("creation_date")
//    val creationDate: Long,
//    val status: Status,
//    val role: Role,
//    @Column("last_login_date")
//    val lastLoginDate: Long?,
//    @Column("totp_mfa_status")
//    val totpMfaStatus: Boolean,
//    @Column("email_verified")
//    val emailVerified: Boolean,
//    @Column("failed_login_attempts")
//    val failedLoginAttempts: Int?,
//    @Column("account_locked_until")
//    val accountLockedUntil: Long?,
//)
