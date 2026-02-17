package com.example.service

import org.jetbrains.exposed.v1.core.Column
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.core.dao.id.IdTable
import org.jetbrains.exposed.v1.dao.LongEntity
import org.jetbrains.exposed.v1.dao.LongEntityClass

object ArticleViewCounts : IdTable<Long>("article_view_count") {
    override val id: Column<EntityID<Long>> = long("article_view_count_id").autoIncrement().entityId()
    val articleId = long("article_id")
    val viewCount = long("view_count")
}

class ArticleViewCount(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<ArticleViewCount>(ArticleViewCounts)

    var articleId by ArticleViewCounts.articleId
    var viewCount by ArticleViewCounts.viewCount
}