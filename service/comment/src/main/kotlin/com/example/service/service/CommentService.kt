package com.example.service.service

import com.example.service.entity.Comment
import com.example.service.repository.CommentRepository
import com.example.service.service.request.CommentCreateRequest
import com.example.service.service.response.CommentPageResponse
import com.example.service.service.response.CommentResponse
import kuke.board.common.snowflake.Snowflake
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class CommentService(
    private val commentRepository: CommentRepository,
) {
    private val snowflake = Snowflake()

    @Transactional
    fun create(request: CommentCreateRequest): CommentResponse {
        val parent = findParent(request)
        val comment = Comment.new(snowflake.nextId()) {
            content = request.content
            parentCommentId = parent?.id?.value ?: id.value
            articleId = request.articleId
            writerId = request.writerId
            deleted = false
            createdAt = LocalDateTime.now()
        }
        return CommentResponse.from(comment)
    }

    private fun findParent(request: CommentCreateRequest): Comment? {
        val parentCommentId = request.parentCommentId ?: return null
        val parent = Comment.findById(parentCommentId)
            ?: throw NoSuchElementException("Parent comment not found: $parentCommentId")
        if (parent.deleted) throw IllegalStateException("Parent comment is deleted")
        if (!parent.isRoot()) throw IllegalStateException("Parent comment is not root")
        return parent
    }

    @Transactional(readOnly = true)
    fun read(commentId: Long): CommentResponse {
        val comment = Comment.findById(commentId)
            ?: throw NoSuchElementException("Comment not found: $commentId")
        return CommentResponse.from(comment)
    }

    @Transactional
    fun delete(commentId: Long) {
        val comment = Comment.findById(commentId) ?: return
        if (comment.deleted) return

        if (hasChildren(comment)) {
            comment.markDeleted()
        } else {
            deleteRecursive(comment)
        }
    }

    private fun hasChildren(comment: Comment): Boolean {
        return commentRepository.countBy(comment.articleId, comment.id.value, 2L) == 2L
    }

    private fun deleteRecursive(comment: Comment) {
        comment.delete()
        if (!comment.isRoot()) {
            val parent = Comment.findById(comment.parentCommentId) ?: return
            if (parent.deleted && !hasChildren(parent)) {
                deleteRecursive(parent)
            }
        }
    }

    @Transactional(readOnly = true)
    fun readAll(articleId: Long, page: Long, pageSize: Long): CommentPageResponse {
        val comments = commentRepository.findAll(articleId, (page - 1) * pageSize, pageSize)
            .map { CommentResponse.from(it) }
        val commentCount = commentRepository.count(
            articleId,
            PageLimitCalculator.calculatePageLimit(page, pageSize, 10L),
        )
        return CommentPageResponse(comments, commentCount)
    }

    @Transactional(readOnly = true)
    fun readAllInfiniteScroll(
        articleId: Long,
        lastParentCommentId: Long?,
        lastCommentId: Long?,
        limit: Long,
    ): List<CommentResponse> {
        val comments = if (lastParentCommentId == null || lastCommentId == null) {
            commentRepository.findAllInfiniteScroll(articleId, limit)
        } else {
            commentRepository.findAllInfiniteScroll(articleId, lastParentCommentId, lastCommentId, limit)
        }
        return comments.map { CommentResponse.from(it) }
    }
}
