package com.example.service.service.response

data class CommentPageResponse(
    val comments: List<CommentResponse>,
    val commentCount: Long,
)
