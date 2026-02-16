package com.example.service.entity

import org.jetbrains.exposed.v1.core.Column
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.core.dao.id.IdTable
import org.jetbrains.exposed.v1.dao.LongEntity
import org.jetbrains.exposed.v1.dao.LongEntityClass
import org.jetbrains.exposed.v1.javatime.datetime
import java.time.LocalDateTime

object CommentsV2 : IdTable<Long>("comment_v2") {
    override val id: Column<EntityID<Long>> = long("comment_id").entityId()
    val content = varchar("content", 3000)
    val articleId = long("article_id")
    val writerId = long("writer_id")
    val path = varchar("path", 25)
    val deleted = bool("deleted").default(false)
    val createdAt = datetime("created_at")
    override val primaryKey = PrimaryKey(id)
}

class CommentV2(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<CommentV2>(CommentsV2)

    var content by CommentsV2.content
    var articleId by CommentsV2.articleId
    var writerId by CommentsV2.writerId
    var path by CommentsV2.path
    var deleted by CommentsV2.deleted
    var createdAt by CommentsV2.createdAt

    fun isRoot(): Boolean = CommentPath.create(path).isRoot()

    fun markDeleted() {
        deleted = true
    }
}
