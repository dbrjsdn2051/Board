package com.example.service.repository

import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
class ArticleRepositoryTest {

    private val log = LoggerFactory.getLogger(javaClass)

    @Autowired
    lateinit var articleRepository: ArticleRepository

    @Test
    @Transactional(readOnly = true)
    fun findAllTest() {
        val articles = articleRepository.findAll(1L, 1499970L, 30L)
        log.info("articles.size = {}", articles.size)
        for (article in articles) {
            log.info("article.id = {}", article.id)
        }
    }

    @Test
    @Transactional(readOnly = true)
    fun countTest() {
        val count = articleRepository.count(1L, 10000L)
        log.info("count = {}", count)
    }
}
