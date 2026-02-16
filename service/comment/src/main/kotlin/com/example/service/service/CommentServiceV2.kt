package com.example.service.service

import com.example.service.entity.ArticleCommentCount
import com.example.service.entity.CommentPath
import com.example.service.entity.CommentV2
import com.example.service.repository.ArticleCommentCountRepository
import com.example.service.repository.CommentRepositoryV2
import com.example.service.service.request.CommentCreateRequestV2
import com.example.service.service.response.CommentPageResponse
import com.example.service.service.response.CommentResponse
import kuke.board.common.snowflake.Snowflake
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class CommentServiceV2(
    private val commentRepository: CommentRepositoryV2,
    private val articleCommentCountRepository: ArticleCommentCountRepository,
) {
    private val snowflake = Snowflake()

    @Transactional
    fun create(request: CommentCreateRequestV2): CommentResponse {
        val parent = findParent(request)
        val parentCommentPath = if (parent == null) CommentPath.create("") else CommentPath.create(parent.path)

        val childPath = parentCommentPath.createChildCommentPath(
            commentRepository.findDescendantsTopPath(request.articleId, parentCommentPath.path)
        )

        val comment = CommentV2.new(snowflake.nextId()) {
            content = request.content
            articleId = request.articleId
            writerId = request.writerId
            path = childPath.path
            deleted = false
            createdAt = LocalDateTime.now()
        }

        val result = articleCommentCountRepository.increase(request.articleId)
        if (result == 0) {
            ArticleCommentCount.new(request.articleId) {
                commentCount = 1L
            }
        }

        return CommentResponse.from(comment)
    }

    private fun findParent(request: CommentCreateRequestV2): CommentV2? {
        val parentPath = request.parentPath ?: return null
        val parent = commentRepository.findByPath(parentPath)
            ?: throw NoSuchElementException("Parent comment not found: $parentPath")
        if (parent.deleted) throw IllegalStateException("Parent comment is deleted")
        return parent
    }

    @Transactional(readOnly = true)
    fun read(commentId: Long): CommentResponse {
        val comment = CommentV2.findById(commentId)
            ?: throw NoSuchElementException("Comment not found: $commentId")
        return CommentResponse.from(comment)
    }

    @Transactional
    fun delete(commentId: Long) {
        val comment = CommentV2.findById(commentId) ?: return
        if (comment.deleted) return

        if (hasChildren(comment)) {
            comment.markDeleted()
        } else {
            deleteRecursive(comment)
        }
    }

    private fun hasChildren(comment: CommentV2): Boolean {
        return commentRepository.findDescendantsTopPath(
            comment.articleId,
            comment.path,
        ) != null
    }

    private fun deleteRecursive(comment: CommentV2) {
        comment.delete()
        articleCommentCountRepository.decrease(comment.articleId)
        if (!comment.isRoot()) {
            val parentPath = CommentPath.create(comment.path).getParentPath()
            val parent = commentRepository.findByPath(parentPath) ?: return
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
    fun readAllInfiniteScroll(articleId: Long, lastPath: String?, pageSize: Long): List<CommentResponse> {
        val comments = if (lastPath == null) {
            commentRepository.findAllInfiniteScroll(articleId, pageSize)
        } else {
            commentRepository.findAllInfiniteScroll(articleId, lastPath, pageSize)
        }
        return comments.map { CommentResponse.from(it) }
    }

    @Transactional(readOnly = true)
    fun count(articleId: Long): Long {
        return ArticleCommentCount.findById(articleId)?.commentCount ?: 0L
    }
}
