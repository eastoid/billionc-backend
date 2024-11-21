package com.debouncewrite.debouncev2.modules.checkboxes.repository

import com.debouncewrite.debouncev2.modules.checkboxes.model.Checkboxes
import de.huxhorn.sulky.ulid.ULID
import org.springframework.data.r2dbc.repository.Modifying
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.r2dbc.repository.R2dbcRepository
import org.springframework.stereotype.Repository

@Repository
interface CheckboxRepository : R2dbcRepository<Checkboxes, ULID.Value> {

    @Query("INSERT INTO checkboxes (id, bits, date) VALUES (:id::uuid, :bytes::bytea, :date)")
    suspend fun insert(id: ULID.Value, bytes: ByteArray, date: Long)

    @Modifying
    @Query("DELETE FROM checkboxes WHERE (:now - date) > :maxAge ")
    suspend fun deleteOld(now: Long, maxAge: Long): Int

    @Query("SELECT * FROM checkboxes ORDER BY date DESC LIMIT 1")
    suspend fun getLatest(): Checkboxes?

}