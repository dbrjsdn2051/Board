package com.example.service.entity

import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.core.dao.id.IdTable
import org.jetbrains.exposed.v1.dao.LongEntity
import org.jetbrains.exposed.v1.dao.LongEntityClass

object ArticleLikeCounts : IdTable<Long>("article_like_count") {
    override val id = long("article_id").entityId()
    val likeCount = long("like_count")
    val version = long("version").default(0L)
}

class ArticleLikeCount(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<ArticleLikeCount>(ArticleLikeCounts)

    var likeCount by ArticleLikeCounts.likeCount
    var version by ArticleLikeCounts.version

    fun increase() {
        this.likeCount++
    }

    fun decrease() {
        this.likeCount--
    }
}
