package com.debouncewrite.debouncev2.infrastructure.config.serialization

import de.huxhorn.sulky.ulid.ULID
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.util.*

object UlidSerializer : KSerializer<ULID.Value> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("ULID", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: ULID.Value) {
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder): ULID.Value {
        return ULID.parseULID(decoder.decodeString())
    }

    fun parseUuid(uuid: UUID): ULID.Value {
        return ULID.Value(uuid.mostSignificantBits, uuid.leastSignificantBits)
    }
}