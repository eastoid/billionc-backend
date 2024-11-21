package com.debouncewrite.debouncev2.modules.checkboxes.repository

import com.debouncewrite.debouncev2.modules.checkboxes.model.Checkboxes
import de.huxhorn.sulky.ulid.ULID
import org.springframework.data.r2dbc.repository.Modifying
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.r2dbc.repository.R2dbcRepository
import org.springframework.stereotype.Repository

@Repository
interface MetadataRepository : R2dbcRepository<Checkboxes, ULID.Value> {

    @Query("SELECT value FROM metadata WHERE id = 'clicks'")
    suspend fun getClickCount(): String?

    @Query("SELECT value FROM metadata WHERE id = 'checkedCount'")
    suspend fun getCheckedCount(): String?

    @Modifying
    @Query("""
        INSERT INTO metadata (id, value)
        VALUES ('clicks', :clickCount)
        ON CONFLICT (id) 
        DO UPDATE SET value = :clickCount
    """)
    suspend fun setClicks(clickCount: String)

    @Modifying
    @Query("""
        INSERT INTO metadata (id, value)
        VALUES ('checkedCount', :count)
        ON CONFLICT (id) 
        DO UPDATE SET value = :count
    """)
    suspend fun setCheckedCount(count: String)

}