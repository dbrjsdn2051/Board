package com.example.service.controller

import com.example.service.service.CommentService
import com.example.service.service.request.CommentCreateRequest
import com.example.service.service.response.CommentPageResponse
import com.example.service.service.response.CommentResponse
import org.springframework.web.bind.annotation.*

@RestController
class CommentController(
    private val commentService: CommentService,
) {
    @GetMapping("/v1/comments/{commentId}")
    fun read(@PathVariable commentId: Long): CommentResponse {
        return commentService.read(commentId)
    }

    @PostMapping("/v1/comments")
    fun create(@RequestBody request: CommentCreateRequest): CommentResponse {
        return commentService.create(request)
    }

    @DeleteMapping("/v1/comments/{commentId}")
    fun delete(@PathVariable commentId: Long) {
        commentService.delete(commentId)
    }

    @GetMapping("/v1/comments")
    fun readAll(
        @RequestParam("articleId") articleId: Long,
        @RequestParam("page") page: Long,
        @RequestParam("pageSize") pageSize: Long,
    ): CommentPageResponse {
        return commentService.readAll(articleId, page, pageSize)
    }

    @GetMapping("/v1/comments/infinite-scroll")
    fun readAllInfiniteScroll(
        @RequestParam("articleId") articleId: Long,
        @RequestParam(value = "lastParentCommentId", required = false) lastParentCommentId: Long?,
        @RequestParam(value = "lastCommentId", required = false) lastCommentId: Long?,
        @RequestParam("pageSize") pageSize: Long,
    ): List<CommentResponse> {
        return commentService.readAllInfiniteScroll(articleId, lastParentCommentId, lastCommentId, pageSize)
    }
}
