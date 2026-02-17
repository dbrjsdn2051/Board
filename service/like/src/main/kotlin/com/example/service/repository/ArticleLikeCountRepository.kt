package com.example.service.repository

import com.example.service.entity.ArticleLikeCount
import com.example.service.entity.ArticleLikeCounts
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.minus
import org.jetbrains.exposed.v1.core.plus
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.update
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class ArticleLikeCountRepository {

    fun findLockedByArticleId(articleId: Long): ArticleLikeCount? {
        return ArticleLikeCounts.selectAll()
            .where {ArticleLikeCounts.id eq articleId}
            .forUpdate()
            .map { ArticleLikeCount.wrapRow(it) }
            .firstOrNull()
    }

    @Transactional
    fun increase(articleId: Long): Int {
        return ArticleLikeCounts.update({ ArticleLikeCounts.id eq articleId }) {
            it[likeCount] = likeCount + 1
        }
    }

    @Transactional
    fun decrease(articleId: Long): Int {
        return ArticleLikeCounts.update({ ArticleLikeCounts.id eq articleId }) {
            it[likeCount] = likeCount - 1
        }
    }

}