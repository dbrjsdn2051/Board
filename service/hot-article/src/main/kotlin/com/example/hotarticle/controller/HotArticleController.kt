package com.example.hotarticle.controller

import com.example.hotarticle.service.HotArticleService
import com.example.hotarticle.service.response.HotArticleResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class HotArticleController(
    private val hotArticleService: HotArticleService
) {

    @GetMapping("/v1/hot-articles/articles/date/{dateStr}")
    fun readAll(
        @PathVariable dateStr: String
    ): List<HotArticleResponse> {
        return hotArticleService.readAll(dateStr)
    }
}