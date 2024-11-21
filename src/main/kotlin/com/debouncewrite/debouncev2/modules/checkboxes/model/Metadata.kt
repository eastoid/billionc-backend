package com.debouncewrite.debouncev2.modules.checkboxes.model

import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table("metadata")
data class Metadata(
    @Column("id")
    val id: String,
    @Column("value")
    val value: String,
)
