package com.example.service.controller

import com.example.service.service.CommentServiceV2
import com.example.service.service.request.CommentCreateRequestV2
import com.example.service.service.response.CommentPageResponse
import com.example.service.service.response.CommentResponse
import org.springframework.web.bind.annotation.*

@RestController
class CommentControllerV2(
    private val commentService: CommentServiceV2,
) {
    @GetMapping("/v2/comments/{commentId}")
    fun read(@PathVariable commentId: Long): CommentResponse {
        return commentService.read(commentId)
    }

    @PostMapping("/v2/comments")
    fun create(@RequestBody request: CommentCreateRequestV2): CommentResponse {
        return commentService.create(request)
    }

    @DeleteMapping("/v2/comments/{commentId}")
    fun delete(@PathVariable commentId: Long) {
        commentService.delete(commentId)
    }

    @GetMapping("/v2/comments")
    fun readAll(
        @RequestParam("articleId") articleId: Long,
        @RequestParam("page") page: Long,
        @RequestParam("pageSize") pageSize: Long,
    ): CommentPageResponse {
        return commentService.readAll(articleId, page, pageSize)
    }

    @GetMapping("/v2/comments/infinite-scroll")
    fun readAllInfiniteScroll(
        @RequestParam("articleId") articleId: Long,
        @RequestParam(value = "lastPath", required = false) lastPath: String?,
        @RequestParam("pageSize") pageSize: Long,
    ): List<CommentResponse> {
        return commentService.readAllInfiniteScroll(articleId, lastPath, pageSize)
    }

    @GetMapping("/v2/comments/articles/{articleId}/count")
    fun count(@PathVariable articleId: Long): Long {
        return commentService.count(articleId)
    }
}
