package com.example.service.api

import com.example.service.service.response.CommentPageResponse
import com.example.service.service.response.CommentResponse
import org.junit.jupiter.api.Test
import org.springframework.core.ParameterizedTypeReference
import org.springframework.web.client.RestClient

class CommentApiTest {
    val restClient: RestClient = RestClient.create("http://localhost:9001")

    @Test
    fun create() {
        val response1 = createComment(CommentCreateRequest(1L, "my comment1", null, 1L))
        val response2 = createComment(CommentCreateRequest(1L, "my comment2", response1?.commentId, 1L))
        val response3 = createComment(CommentCreateRequest(1L, "my comment3", response1?.commentId, 1L))

        println("commentId=${response1?.commentId}")
        println("\tcommentId=${response2?.commentId}")
        println("\tcommentId=${response3?.commentId}")
    }

    fun createComment(request: CommentCreateRequest): CommentResponse? {
        return restClient.post()
            .uri("/v1/comments")
            .body(request)
            .retrieve()
            .body(CommentResponse::class.java)
    }

    @Test
    fun read() {
        val response = restClient.get()
            .uri("/v1/comments/{commentId}", 123694721668214784L)
            .retrieve()
            .body(CommentResponse::class.java)
        println("response = $response")
    }

    @Test
    fun delete() {
        restClient.delete()
            .uri("/v1/comments/{commentId}", 123694722045702144L)
            .retrieve()
    }

    @Test
    fun readAll() {
        val response = restClient.get()
            .uri("/v1/comments?articleId=1&page=1&pageSize=10")
            .retrieve()
            .body(CommentPageResponse::class.java)

        println("response.commentCount = ${response?.commentCount}")
        response?.comments?.forEach { comment ->
            if (comment.commentId != comment.parentCommentId) {
                print("\t")
            }
            println("comment.commentId = ${comment.commentId}")
        }
    }

    @Test
    fun readAllInfiniteScroll() {
        val responses1 = restClient.get()
            .uri("/v1/comments/infinite-scroll?articleId=1&pageSize=5")
            .retrieve()
            .body(object : ParameterizedTypeReference<List<CommentResponse>>() {})

        println("firstPage")
        responses1?.forEach { comment ->
            if (comment.commentId != comment.parentCommentId) {
                print("\t")
            }
            println("comment.commentId = ${comment.commentId}")
        }

        val last = responses1?.last()
        val lastParentCommentId = last?.parentCommentId
        val lastCommentId = last?.commentId

        val responses2 = restClient.get()
            .uri("/v1/comments/infinite-scroll?articleId=1&pageSize=5&lastParentCommentId=$lastParentCommentId&lastCommentId=$lastCommentId")
            .retrieve()
            .body(object : ParameterizedTypeReference<List<CommentResponse>>() {})

        println("secondPage")
        responses2?.forEach { comment ->
            if (comment.commentId != comment.parentCommentId) {
                print("\t")
            }
            println("comment.commentId = ${comment.commentId}")
        }
    }

    data class CommentCreateRequest(
        val articleId: Long,
        val content: String,
        val parentCommentId: Long?,
        val writerId: Long,
    )
}
