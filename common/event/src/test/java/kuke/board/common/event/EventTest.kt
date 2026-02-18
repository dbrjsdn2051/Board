package kuke.board.common.event

import kuke.board.common.event.payload.ArticleCreatedEventPayload
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class EventTest {

    @Test
    fun serde() {
        // given
        val payload = ArticleCreatedEventPayload(
            articleId = 1L,
            title = "title",
            content = "content",
            boardId = 1L,
            writerId = 1L,
            createdAt = LocalDateTime.now(),
            modifiedAt = LocalDateTime.now(),
            boardArticleCount = 23L,
        )

        val event = Event.of(1234L, EventType.ARTICLE_CREATED, payload)

        val json = event.toJson()
        println("json = $json")

        // when
        val result = Event.fromJson(json!!)

        // then
        assertThat(result?.eventId).isEqualTo(event.eventId)
        assertThat(result?.type).isEqualTo(event.type)
        assertThat(result?.payload).isInstanceOf(ArticleCreatedEventPayload::class.java)

        val resultPayload = result?.payload as ArticleCreatedEventPayload
        assertThat(resultPayload.articleId).isEqualTo(payload.articleId)
        assertThat(resultPayload.title).isEqualTo(payload.title)
        assertThat(resultPayload.createdAt).isEqualTo(payload.createdAt)
    }
}
