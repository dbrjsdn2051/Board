package com.example.service.service.request

data class CommentCreateRequestV2(
    val articleId: Long,
    val content: String,
    val parentPath: String?,
    val writerId: Long,
)
