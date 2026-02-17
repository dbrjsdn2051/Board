package com.example.service.service

import com.example.service.ArticleViewCount
import com.example.service.repository.ArticleViewCountBackUpRepository
import org.springframework.stereotype.Component

@Component
class ArticleViewCountBackUpProcessor(
    private val articleViewCountBackUpRepository: ArticleViewCountBackUpRepository
) {

    fun backUp(articleId: Long, viewCount: Long) {
        val result = articleViewCountBackUpRepository.updateViewCount(articleId, viewCount)
        if (result == 0) {
            ArticleViewCount.findById(articleId)
                ?: ArticleViewCount.new {
                    this.articleId = articleId
                    this.viewCount = viewCount
                }
        }
    }
}