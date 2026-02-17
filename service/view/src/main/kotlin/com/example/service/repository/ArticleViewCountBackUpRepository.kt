package com.example.service.repository

import com.example.service.ArticleViewCounts
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.less
import org.jetbrains.exposed.v1.jdbc.update
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class ArticleViewCountBackUpRepository {

    @Transactional
    fun updateViewCount(articleId: Long, viewCount: Long): Int {
        return ArticleViewCounts.update({
            (ArticleViewCounts.articleId eq articleId) and (ArticleViewCounts.viewCount less viewCount)
        }) {
            it[ArticleViewCounts.viewCount] = viewCount
        }
    }
}