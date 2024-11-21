package com.debouncewrite.debouncev2.infrastructure.security.authentication.model

//import org.springframework.data.relational.core.mapping.Column
//
//
//data class PasswordChaChaPair(
//    @Column("password_master_key")
//    val ciphertext: String,
//    @Column("password_master_key_header")
//    val header: String,
//    @Column("master_salt")
//    val salt: String,
//) {
//    override fun toString(): String {
//        return "{\"ciphertext\":\"${this.ciphertext}\",\"header\":\"${this.header}\",\"salt\":\"${this.salt}\"}"
//    }
//}
//
//data class PhraseChaChaPair(
//    @Column("phrase_master_key")
//    val ciphertext: String,
//    @Column("phrase_master_key_header")
//    val header: String,
//    @Column("phrase_salt")
//    val salt: String,
//) {
//    override fun toString(): String {
//        return "{\"ciphertext\":\"${this.ciphertext}\",\"header\":\"${this.header}\",\"salt\":\"${this.salt}\"}"
//    }
//}
