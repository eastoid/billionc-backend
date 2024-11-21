package com.debouncewrite.debouncev2.infrastructure.config.r2dbc

import de.huxhorn.sulky.ulid.ULID
import io.r2dbc.spi.ConnectionFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.ReadingConverter
import org.springframework.data.convert.WritingConverter
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration
import org.springframework.data.r2dbc.convert.R2dbcCustomConversions
import org.springframework.data.r2dbc.dialect.DialectResolver
import org.springframework.stereotype.Component
import java.nio.ByteBuffer
import java.util.*


@Configuration
class R2dbcConfig(val connectionFactory: ConnectionFactory) : AbstractR2dbcConfiguration() {

    override fun connectionFactory(): ConnectionFactory {
        return connectionFactory
    }

    @Bean
    override fun r2dbcCustomConversions(): R2dbcCustomConversions {
        val converters = listOf(ULIDToUUIDConverter(), UUIDToULIDConverter())
        return R2dbcCustomConversions.of(DialectResolver.getDialect(connectionFactory), converters)
    }
}



@Component
@WritingConverter
class ULIDToUUIDConverter : Converter<ULID.Value, UUID> {
    override fun convert(source: ULID.Value): UUID {
        val ulidString = source.toString()

        val ulidBytes = ULID.parseULID(ulidString).toBytes()

        val byteBuffer = ByteBuffer.wrap(ulidBytes)
        val mostSignificantBits = byteBuffer.long
        val leastSignificantBits = byteBuffer.long
        return UUID(mostSignificantBits, leastSignificantBits)
    }
}

@Component
@ReadingConverter
class UUIDToULIDConverter : Converter<UUID, ULID.Value> {

    override fun convert(source: UUID): ULID.Value {
        val ba = toByteArray(source)
        val ulid = ULID.fromBytes(ba)
        return ulid
    }

    fun toByteArray(uuid: UUID): ByteArray {
        val byteBuffer = ByteBuffer.wrap(ByteArray(16))
        byteBuffer.putLong(uuid.mostSignificantBits)
        byteBuffer.putLong(uuid.leastSignificantBits)
        return byteBuffer.array()
    }
}