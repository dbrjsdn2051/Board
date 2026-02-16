package com.example.service.controller

import com.example.service.service.ArticleService
import com.example.service.service.request.ArticleCreateRequest
import com.example.service.service.request.ArticleUpdateRequest
import com.example.service.service.response.ArticlePageResponse
import com.example.service.service.response.ArticleResponse
import org.springframework.web.bind.annotation.*

@RestController
class ArticleController(
    private val articleService: ArticleService,
) {
    @GetMapping("/v1/articles/{articleId}")
    fun read(@PathVariable articleId: Long): ArticleResponse {
        return articleService.read(articleId)
    }

    @GetMapping("/v1/articles")
    fun readAll(
        @RequestParam("boardId") boardId: Long,
        @RequestParam("page") page: Long,
        @RequestParam("pageSize") pageSize: Long,
    ): ArticlePageResponse {
        return articleService.readAll(boardId, page, pageSize)
    }

    @PostMapping("/v1/articles")
    fun create(@RequestBody request: ArticleCreateRequest): ArticleResponse {
        return articleService.create(request)
    }

    @PutMapping("/v1/articles/{articleId}")
    fun update(
        @PathVariable articleId: Long,
        @RequestBody request: ArticleUpdateRequest,
    ): ArticleResponse {
        return articleService.update(articleId, request)
    }

    @DeleteMapping("/v1/articles/{articleId}")
    fun delete(@PathVariable articleId: Long) {
        articleService.delete(articleId)
    }
}
