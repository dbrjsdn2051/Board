package com.example.service.entity

import org.jetbrains.exposed.v1.core.Column
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.core.dao.id.IdTable
import org.jetbrains.exposed.v1.dao.LongEntity
import org.jetbrains.exposed.v1.dao.LongEntityClass
import org.jetbrains.exposed.v1.javatime.datetime
import java.time.LocalDateTime

object Articles : IdTable<Long>("article") {
    override val id: Column<EntityID<Long>> = long("article_id").entityId()
    val title = varchar("title", 100)
    val content = varchar("content", 3000)
    val boardId = long("board_id")
    val writerId = long("writer_id")
    val createdAt = datetime("created_at")
    val modifiedAt = datetime("modified_at")

    override val primaryKey = PrimaryKey(id)
}

class Article(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<Article>(Articles)

    var title by Articles.title
    var content by Articles.content
    var boardId by Articles.boardId
    var writerId by Articles.writerId
    var createdAt by Articles.createdAt
    var modifiedAt by Articles.modifiedAt

    fun update(title: String, content: String) {
        this.title = title
        this.content = content
        this.modifiedAt = LocalDateTime.now()
    }
}
