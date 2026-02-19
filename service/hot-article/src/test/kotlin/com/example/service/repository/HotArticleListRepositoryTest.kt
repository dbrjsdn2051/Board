package com.example.service.repository

import com.example.hotarticle.repository.HotArticleListRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.time.Duration
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit

@SpringBootTest
class HotArticleListRepositoryTest {

    @Autowired
    lateinit var hotArticleListRepository: HotArticleListRepository

    @Test
    fun addTest() {
        // given
        val time = LocalDateTime.of(2024, 7, 23, 0, 0)
        val limit = 3L

        // when
        hotArticleListRepository.add(1L, time, 2L, limit, Duration.ofSeconds(3))
        hotArticleListRepository.add(2L, time, 3L, limit, Duration.ofSeconds(3))
        hotArticleListRepository.add(3L, time, 1L, limit, Duration.ofSeconds(3))
        hotArticleListRepository.add(4L, time, 5L, limit, Duration.ofSeconds(3))
        hotArticleListRepository.add(5L, time, 4L, limit, Duration.ofSeconds(3))

        // then
        val articleIds = hotArticleListRepository.readAll("20240723")

        assertThat(articleIds).hasSize(limit.toInt())
        assertThat(articleIds[0]).isEqualTo(4L)
        assertThat(articleIds[1]).isEqualTo(5L)
        assertThat(articleIds[2]).isEqualTo(2L)

        TimeUnit.SECONDS.sleep(5)

        assertThat(hotArticleListRepository.readAll("20240723")).isEmpty()
    }
}
