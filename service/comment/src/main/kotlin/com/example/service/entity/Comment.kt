package com.example.service.entity

import org.jetbrains.exposed.v1.core.Column
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.core.dao.id.IdTable
import org.jetbrains.exposed.v1.dao.LongEntity
import org.jetbrains.exposed.v1.dao.LongEntityClass
import org.jetbrains.exposed.v1.javatime.datetime
import java.time.LocalDateTime

object Comments : IdTable<Long>("comment") {
    override val id: Column<EntityID<Long>> = long("comment_id").entityId()
    val content = varchar("content", 3000)
    val articleId = long("article_id")
    val parentCommentId = long("parent_comment_id")
    val writerId = long("writer_id")
    val deleted = bool("deleted").default(false)
    val createdAt = datetime("created_at")
    override val primaryKey = PrimaryKey(id)
}

class Comment(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<Comment>(Comments)

    var content by Comments.content
    var articleId by Comments.articleId
    var parentCommentId by Comments.parentCommentId
    var writerId by Comments.writerId
    var deleted by Comments.deleted
    var createdAt by Comments.createdAt

    fun isRoot(): Boolean = parentCommentId == id.value

    fun markDeleted() {
        deleted = true
    }
}
