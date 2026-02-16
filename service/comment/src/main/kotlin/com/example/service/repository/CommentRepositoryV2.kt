package com.example.service.repository

import com.example.service.entity.CommentV2
import com.example.service.entity.CommentsV2
import org.jetbrains.exposed.v1.core.JoinType
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.core.alias
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.greater
import org.jetbrains.exposed.v1.core.like
import org.jetbrains.exposed.v1.jdbc.select
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.springframework.stereotype.Repository

@Repository
class CommentRepositoryV2 {

    fun findByPath(path: String): CommentV2? {
        return CommentV2.find { CommentsV2.path eq path }.firstOrNull()
    }

    fun findDescendantsTopPath(articleId: Long, pathPrefix: String): String? {
        return CommentsV2
            .select(CommentsV2.path)
            .where { (CommentsV2.articleId eq articleId) and (CommentsV2.path greater pathPrefix) and (CommentsV2.path like "$pathPrefix%") }
            .orderBy(CommentsV2.path, SortOrder.DESC)
            .limit(1)
            .map { it[CommentsV2.path] }
            .firstOrNull()
    }

    fun findAll(articleId: Long, offset: Long, limit: Long): List<CommentV2> {
        val subquery = CommentsV2
            .select(CommentsV2.id)
            .where { CommentsV2.articleId eq articleId }
            .orderBy(CommentsV2.path, SortOrder.ASC)
            .limit(limit.toInt())
            .offset(offset)
            .alias("t")

        return subquery
            .join(CommentsV2, JoinType.LEFT, subquery[CommentsV2.id], CommentsV2.id)
            .selectAll()
            .map { CommentV2.wrapRow(it) }
    }

    fun count(articleId: Long, limit: Long): Long {
        val subquery = CommentsV2
            .select(CommentsV2.id)
            .where { CommentsV2.articleId eq articleId }
            .limit(limit.toInt())
            .alias("t")

        return subquery.selectAll().count()
    }

    fun findAllInfiniteScroll(articleId: Long, limit: Long): List<CommentV2> {
        return CommentV2.find { CommentsV2.articleId eq articleId }
            .orderBy(CommentsV2.path to SortOrder.ASC)
            .limit(limit.toInt())
            .toList()
    }

    fun findAllInfiniteScroll(articleId: Long, lastPath: String, limit: Long): List<CommentV2> {
        return CommentV2.find {
            (CommentsV2.articleId eq articleId) and (CommentsV2.path greater lastPath)
        }
            .orderBy(CommentsV2.path to SortOrder.ASC)
            .limit(limit.toInt())
            .toList()
    }
}
