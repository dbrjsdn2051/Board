package com.example.service.entity

import org.jetbrains.exposed.v1.core.Column
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.core.dao.id.IdTable
import org.jetbrains.exposed.v1.dao.LongEntity
import org.jetbrains.exposed.v1.dao.LongEntityClass

object ArticleCommentCounts : IdTable<Long>("article_comment_count") {
    override val id: Column<EntityID<Long>> = long("article_id").entityId()
    val commentCount = long("comment_count")
    override val primaryKey = PrimaryKey(id)
}

class ArticleCommentCount(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<ArticleCommentCount>(ArticleCommentCounts)

    var commentCount by ArticleCommentCounts.commentCount
}
