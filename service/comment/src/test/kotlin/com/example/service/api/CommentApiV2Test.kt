package com.example.service.api

import com.example.service.service.response.CommentPageResponse
import com.example.service.service.response.CommentResponse
import org.junit.jupiter.api.Test
import org.springframework.core.ParameterizedTypeReference
import org.springframework.web.client.RestClient

class CommentApiV2Test {
    val restClient: RestClient = RestClient.create("http://localhost:9001")

    @Test
    fun create() {
        val response1 = createComment(CommentCreateRequestV2(1L, "my comment1", null, 1L))
        val response2 = createComment(CommentCreateRequestV2(1L, "my comment2", response1?.path, 1L))
        val response3 = createComment(CommentCreateRequestV2(1L, "my comment3", response2?.path, 1L))

        println("response1.path = ${response1?.path}")
        println("response1.commentId = ${response1?.commentId}")
        println("\tresponse2.path = ${response2?.path}")
        println("\tresponse2.commentId = ${response2?.commentId}")
        println("\t\tresponse3.path = ${response3?.path}")
        println("\t\tresponse3.commentId = ${response3?.commentId}")
    }

    fun createComment(request: CommentCreateRequestV2): CommentResponse? {
        return restClient.post()
            .uri("/v2/comments")
            .body(request)
            .retrieve()
            .body(CommentResponse::class.java)
    }

    @Test
    fun read() {
        val response = restClient.get()
            .uri("/v2/comments/{commentId}", 124136886845272064L)
            .retrieve()
            .body(CommentResponse::class.java)
        println("response = $response")
    }

    @Test
    fun delete() {
        restClient.delete()
            .uri("/v2/comments/{commentId}", 124136886845272064L)
            .retrieve()
    }

    @Test
    fun readAll() {
        val response = restClient.get()
            .uri("/v2/comments?articleId=1&pageSize=10&page=1")
            .retrieve()
            .body(CommentPageResponse::class.java)

        println("response.commentCount = ${response?.commentCount}")
        response?.comments?.forEach { comment ->
            println("comment.commentId = ${comment.commentId}")
        }
    }

    @Test
    fun readAllInfiniteScroll() {
        val responses1 = restClient.get()
            .uri("/v2/comments/infinite-scroll?articleId=1&pageSize=5")
            .retrieve()
            .body(object : ParameterizedTypeReference<List<CommentResponse>>() {})

        println("firstPage")
        responses1?.forEach { response ->
            println("response.commentId = ${response.commentId}")
        }

        val lastPath = responses1?.last()?.path

        val responses2 = restClient.get()
            .uri("/v2/comments/infinite-scroll?articleId=1&pageSize=5&lastPath=$lastPath")
            .retrieve()
            .body(object : ParameterizedTypeReference<List<CommentResponse>>() {})

        println("secondPage")
        responses2?.forEach { response ->
            println("response.commentId = ${response.commentId}")
        }
    }

    @Test
    fun countTest() {
        val commentResponse = createComment(CommentCreateRequestV2(2L, "my comment1", null, 1L))

        val count1 = restClient.get()
            .uri("/v2/comments/articles/{articleId}/count", 2L)
            .retrieve()
            .body(Long::class.java)
        println("count1 = $count1") // 1

        restClient.delete()
            .uri("/v2/comments/{commentId}", commentResponse?.commentId)
            .retrieve()

        val count2 = restClient.get()
            .uri("/v2/comments/articles/{articleId}/count", 2L)
            .retrieve()
            .body(Long::class.java)
        println("count2 = $count2") // 0
    }

    data class CommentCreateRequestV2(
        val articleId: Long,
        val content: String,
        val parentPath: String?,
        val writerId: Long,
    )
}
