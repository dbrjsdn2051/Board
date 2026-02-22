package com.example.hotarticle.service.eventhandler

import board.common.event.Event
import board.common.event.EventPayload

interface EventHandler<T : EventPayload> {
    fun handle(event: Event<out EventPayload>)
    fun supports(event: Event<out EventPayload>): Boolean
    fun findArticleId(event: Event<out EventPayload>): Long
}