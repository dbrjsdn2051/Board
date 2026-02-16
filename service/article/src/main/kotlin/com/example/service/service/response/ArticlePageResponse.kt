package com.example.service.service.response

data class ArticlePageResponse(
    val articles: List<ArticleResponse>,
    val articleCount: Long,
)
