package kuke.board.common.dataserializer

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.slf4j.LoggerFactory

class DataSerializer private constructor() {

    companion object {
        private val log = LoggerFactory.getLogger(DataSerializer::class.java)
        private val objectMapper: ObjectMapper = initialize()

        private fun initialize(): ObjectMapper {
            return ObjectMapper()
                .registerModule(JavaTimeModule())
                .registerModule(KotlinModule.Builder().build())
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        }

        fun <T> deserialize(data: String, clazz: Class<T>): T? {
            return try {
                objectMapper.readValue(data, clazz)
            } catch (e: JsonProcessingException) {
                log.error("[DataSerializer.deserialize] data={}, clazz={}", data, clazz, e)
                null
            }
        }

        fun <T> deserialize(data: Any, clazz: Class<T>): T {
            return objectMapper.convertValue(data, clazz)
        }

        fun serialize(obj: Any): String? {
            return try {
                objectMapper.writeValueAsString(obj)
            } catch (e: JsonProcessingException) {
                log.error("[DataSerializer.serialize] object={}", obj, e)
                null
            }
        }
    }
}
