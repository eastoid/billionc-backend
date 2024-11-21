package com.debouncewrite.debouncev2.modules.checkboxes.model

import de.huxhorn.sulky.ulid.ULID
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table


@Table("checkboxes")
data class Checkboxes(
    @Column("id")
    val id: ULID.Value,
    @Column("bits")
    val bytes: ByteArray,
    @Column("date")
    val date: Long,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Checkboxes

        if (id != other.id) return false
        if (!bytes.contentEquals(other.bytes)) return false
        if (date != other.date) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + bytes.contentHashCode()
        result = 31 * result + date.hashCode()
        return result
    }
}