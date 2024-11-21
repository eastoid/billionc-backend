package com.debouncewrite.debouncev2.infrastructure.security.authentication.model

//import org.springframework.data.relational.core.mapping.Column
//
//data class BcryptPair(
//    @Column("password_hash")
//    val hash: String,
//    @Column("password_salt")
//    val salt: String,
//) {
//
//    companion object {
//        fun fromValidated(bcrypt: String): BcryptPair {
//            return BcryptPair(
//                bcrypt.substring(29),
//                bcrypt.substring(0, 29)
//            )
//        }
//
//        fun fromOrNull(bcrypt: String): BcryptPair? {
//            if (bcrypt.length != 60) return null
//            return BcryptPair(
//                bcrypt.substring(29),
//                bcrypt.substring(0, 29)
//            )
//        }
//    }
//
//    val full
//        get() = hash + salt
//
//}
