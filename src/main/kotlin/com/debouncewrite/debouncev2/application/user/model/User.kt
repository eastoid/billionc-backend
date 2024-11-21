package com.debouncewrite.debouncev2.application.user.model

//import de.huxhorn.sulky.ulid.ULID
//import org.springframework.data.annotation.Id
//import org.springframework.data.relational.core.mapping.Column
//import org.springframework.data.relational.core.mapping.Table
//
//
//@Table("users")
//data class User(
//    @Id
//    @Column("user_id")
//    val userId: ULID.Value,
//    val email: String,
//    @Column("password_hash")
//    val passwordHash: String,
//    @Column("password_salt")
//    val passwordSalt: String,
//    @Column("creation_date")
//    val creationDate: Long,
//    val status: Status,
//    val role: Role,
//    @Column("last_login_date")
//    val lastLoginDate: Long?,
//    @Column("totp_mfa_status")
//    val totpMfaStatus: Boolean,
//    @Column("totp_mfa_secret")
//    val totpMfaSecret: String?,
//    @Column("totp_mfa_codes")
//    val totpMfaCodes: String?,
//    @Column("email_verified")
//    val emailVerified: Boolean,
//    @Column("email_verification_code")
//    val emailVerificationCode: String?,
//    @Column("failed_login_attempts")
//    val failedLoginAttempts: Int?,
//    @Column("account_locked_until")
//    val accountLockedUntil: Long?,
//    @Column("master_salt")
//    val masterSalt: String,
//    @Column("password_master_key")
//    val passwordMasterKey: String,
//    @Column("password_master_key_header")
//    val passwordMasterKeyHeader: String,
//    @Column("phrase_master_key")
//    val phraseMasterKey: String,
//    @Column("phrase_master_key_header")
//    val phraseMasterKeyHeader: String,
//    @Column("phrase_hash")
//    val phraseHash: String,
//    @Column("phrase_salt")
//    val phraseSalt: String,
//)
