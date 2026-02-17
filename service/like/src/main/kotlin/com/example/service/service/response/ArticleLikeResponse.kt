package com.example.service.service.response

import com.example.service.entity.ArticleLike
import java.time.LocalDateTime


data class ArticleLikeResponse(
    val articleLikeId: Long,
    val articleId: Long,
    val userId: Long,
    val createdAt: LocalDateTime,
) {

    companion object{
        fun from(articleLike: ArticleLike): ArticleLikeResponse{
            return ArticleLikeResponse(
                articleLike.id.value,
                articleLike.articleId,
                articleLike.userId,
                articleLike.createdAt
            )
        }
    }
}