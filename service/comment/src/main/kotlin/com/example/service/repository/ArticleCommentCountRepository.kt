package com.example.service.repository

import com.example.service.entity.ArticleCommentCounts
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.plus
import org.jetbrains.exposed.v1.core.minus
import org.jetbrains.exposed.v1.jdbc.update
import org.springframework.stereotype.Repository

@Repository
class ArticleCommentCountRepository {

    fun increase(articleId: Long): Int {
        return ArticleCommentCounts.update({ ArticleCommentCounts.id eq articleId }) {
            it[commentCount] = commentCount + 1
        }
    }

    fun decrease(articleId: Long): Int {
        return ArticleCommentCounts.update({ ArticleCommentCounts.id eq articleId }) {
            it[commentCount] = commentCount - 1
        }
    }
}
