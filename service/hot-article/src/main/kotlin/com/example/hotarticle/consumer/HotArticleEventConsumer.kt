package com.example.hotarticle.consumer

import com.example.hotarticle.service.HotArticleService
import kuke.board.common.event.Event
import kuke.board.common.event.EventType
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.stereotype.Component

@Component
class HotArticleEventConsumer(
    private val hotArticleService: HotArticleService
) {

    companion object {
        private val log = LoggerFactory.getLogger(HotArticleEventConsumer::class.java)
    }

    @KafkaListener(
        topics = [
            EventType.Topic.BOARD_ARTICLE,
            EventType.Topic.BOARD_COMMENT,
            EventType.Topic.BOARD_LIKE,
            EventType.Topic.BOARD_VIEW
        ]
    )
    fun listen(message: String, ack: Acknowledgment) {
        log.info("[HotArticleEventConsumer.listen] received message: $message")
        val event = Event.fromJson(message) ?: return
        hotArticleService.handleEvent(event)
        ack.acknowledge()
    }
}