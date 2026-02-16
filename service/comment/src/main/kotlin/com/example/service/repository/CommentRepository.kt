package com.example.service.repository

import com.example.service.entity.Comment
import com.example.service.entity.Comments
import org.jetbrains.exposed.v1.core.JoinType
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.core.alias
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.greater
import org.jetbrains.exposed.v1.core.or
import org.jetbrains.exposed.v1.jdbc.select
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.springframework.stereotype.Repository

@Repository
class CommentRepository {

    fun countBy(articleId: Long, parentCommentId: Long, limit: Long): Long {
        val subquery = Comments
            .select(Comments.id)
            .where { (Comments.articleId eq articleId) and (Comments.parentCommentId eq parentCommentId) }
            .limit(limit.toInt())
            .alias("t")

        return subquery.selectAll().count()
    }

    fun findAll(articleId: Long, offset: Long, limit: Long): List<Comment> {
        val subquery = Comments
            .select(Comments.id)
            .where { Comments.articleId eq articleId }
            .orderBy(Comments.parentCommentId, SortOrder.ASC)
            .orderBy(Comments.id, SortOrder.ASC)
            .limit(limit.toInt())
            .offset(offset)
            .alias("t")

        return subquery
            .join(Comments, JoinType.LEFT, subquery[Comments.id], Comments.id)
            .selectAll()
            .map { Comment.wrapRow(it) }
    }

    fun count(articleId: Long, limit: Long): Long {
        val subquery = Comments
            .select(Comments.id)
            .where { Comments.articleId eq articleId }
            .limit(limit.toInt())
            .alias("t")

        return subquery.selectAll().count()
    }

    fun findAllInfiniteScroll(articleId: Long, limit: Long): List<Comment> {
        return Comment.find { Comments.articleId eq articleId }
            .orderBy(Comments.parentCommentId to SortOrder.ASC, Comments.id to SortOrder.ASC)
            .limit(limit.toInt())
            .toList()
    }

    fun findAllInfiniteScroll(
        articleId: Long,
        lastParentCommentId: Long,
        lastCommentId: Long,
        limit: Long,
    ): List<Comment> {
        return Comment.find {
            (Comments.articleId eq articleId) and (
                (Comments.parentCommentId greater lastParentCommentId) or
                    ((Comments.parentCommentId eq lastParentCommentId) and (Comments.id greater lastCommentId))
                )
        }
            .orderBy(Comments.parentCommentId to SortOrder.ASC, Comments.id to SortOrder.ASC)
            .limit(limit.toInt())
            .toList()
    }
}
