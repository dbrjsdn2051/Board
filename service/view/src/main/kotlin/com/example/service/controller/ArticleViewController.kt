package com.example.service.controller

import com.example.service.service.ArticleViewService
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class ArticleViewController(
    private val articleViewService: ArticleViewService,
) {
    @PostMapping("/v1/article-views/articles/{articleId}/users/{userId}")
    fun increase(@PathVariable articleId: Long, @PathVariable userId: Long): Long {
        return articleViewService.increase(articleId, userId)
    }

    @PostMapping("/v1/article-views/articles/{articleId}/count")
    fun count(@PathVariable articleId: Long): Long {
        return articleViewService.count(articleId)
    }
}