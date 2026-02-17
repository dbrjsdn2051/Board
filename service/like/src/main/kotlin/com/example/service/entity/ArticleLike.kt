package com.example.service.entity

import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.core.dao.id.IdTable
import org.jetbrains.exposed.v1.dao.LongEntity
import org.jetbrains.exposed.v1.dao.LongEntityClass
import org.jetbrains.exposed.v1.javatime.datetime

object ArticleLikes : IdTable<Long>("article_like") {
    override val id = long("article_like_id").entityId()
    val articleId = long("article_id")
    val userId = long("user_id")
    val createdAt = datetime("created_at")
}

class ArticleLike(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<ArticleLike>(ArticleLikes)

    var articleId by ArticleLikes.articleId
    var userId by ArticleLikes.userId
    var createdAt by ArticleLikes.createdAt
}