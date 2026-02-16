package com.example.service.api

import com.example.service.service.response.ArticlePageResponse
import com.example.service.service.response.ArticleResponse
import org.junit.jupiter.api.Test
import org.springframework.web.client.RestClient

class ArticleApiTest {
    val restClient: RestClient = RestClient.create("http://localhost:9000")

    @Test
    fun createTest() {
        val response = create(ArticleCreateRequest("hi", "my content", 1L, 1L))
        println("response = $response")
    }

    fun create(request: ArticleCreateRequest): ArticleResponse? {
        return restClient.post()
            .uri("/v1/articles")
            .body(request)
            .retrieve()
            .body(ArticleResponse::class.java)
    }

    @Test
    fun readTest() {
        val response = read(121530268440289280L)
        println("response = $response")
    }

    fun read(articleId: Long): ArticleResponse? {
        return restClient.get()
            .uri("/v1/articles/{articleId}", articleId)
            .retrieve()
            .body(ArticleResponse::class.java)
    }

    @Test
    fun updateTest() {
        update(121530268440289280L)
        val response = read(121530268440289280L)
        println("response = $response")
    }

    fun update(articleId: Long) {
        restClient.put()
            .uri("/v1/articles/{articleId}", articleId)
            .body(ArticleUpdateRequest("hi 2", "my content 22"))
            .retrieve()
    }

    @Test
    fun readAllTest() {
        val response = restClient.get()
            .uri("/v1/articles?boardId=1&pageSize=30&page=50000")
            .retrieve()
            .body(ArticlePageResponse::class.java)

        println("response.articleCount = ${response?.articleCount}")
        response?.articles?.forEach { article ->
            println("articleId = ${article.articleId}")
        }
    }

    @Test
    fun deleteTest() {
        restClient.delete()
            .uri("/v1/articles/{articleId}", 121530268440289280L)
            .retrieve()
    }

    data class ArticleCreateRequest(
        val title: String,
        val content: String,
        val writerId: Long,
        val boardId: Long,
    )

    data class ArticleUpdateRequest(
        val title: String,
        val content: String,
    )
}
