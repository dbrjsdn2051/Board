package com.example.service.service.response

import com.example.service.entity.Comment
import com.example.service.entity.CommentV2
import java.time.LocalDateTime

data class CommentResponse(
    val commentId: Long,
    val content: String,
    val parentCommentId: Long?,
    val articleId: Long,
    val writerId: Long,
    val deleted: Boolean,
    val path: String?,
    val createdAt: LocalDateTime,
) {
    companion object {
        fun from(comment: Comment): CommentResponse = CommentResponse(
            commentId = comment.id.value,
            content = comment.content,
            parentCommentId = comment.parentCommentId,
            articleId = comment.articleId,
            writerId = comment.writerId,
            deleted = comment.deleted,
            path = null,
            createdAt = comment.createdAt,
        )

        fun from(comment: CommentV2): CommentResponse = CommentResponse(
            commentId = comment.id.value,
            content = comment.content,
            parentCommentId = null,
            articleId = comment.articleId,
            writerId = comment.writerId,
            deleted = comment.deleted,
            path = comment.path,
            createdAt = comment.createdAt,
        )
    }
}
