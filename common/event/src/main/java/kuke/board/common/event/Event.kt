package kuke.board.common.event

import kuke.board.common.dataserializer.DataSerializer

class Event<T : EventPayload> {
    var eventId: Long? = null
    var type: EventType? = null
    var payload: T? = null

    companion object {
        fun of(eventId: Long, type: EventType, payload: EventPayload): Event<EventPayload> {
            return Event<EventPayload>().apply {
                this.eventId = eventId
                this.type = type
                this.payload = payload
            }
        }

        fun fromJson(json: String): Event<EventPayload>? {
            val eventRaw = DataSerializer.deserialize(json, EventRaw::class.java) ?: return null
            val eventType = EventType.from(eventRaw.type ?: return null) ?: return null
            return Event<EventPayload>().apply {
                eventId = eventRaw.eventId
                type = eventType
                payload = DataSerializer.deserialize(eventRaw.payload!!, eventType.payloadClass)
            }
        }
    }

    fun toJson(): String? = DataSerializer.serialize(this)

    private class EventRaw {
        var eventId: Long? = null
        var type: String? = null
        var payload: Any? = null
    }
}