package com.example.service.repository

import com.example.service.entity.Article
import com.example.service.entity.Articles
import org.jetbrains.exposed.v1.core.JoinType
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.core.alias
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.select
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.springframework.stereotype.Repository

@Repository
class ArticleRepository {

    fun findAll(boardId: Long, offset: Long, limit: Long): List<Article> {
        val subquery = Articles
            .select(Articles.id)
            .where { Articles.boardId eq boardId }
            .orderBy(Articles.id, SortOrder.DESC)
            .limit(limit.toInt())
            .offset(offset)
            .alias("t")

        return subquery
            .join(Articles, JoinType.LEFT, subquery[Articles.id], Articles.id)
            .selectAll()
            .map { Article.wrapRow(it) }
    }

    fun count(boardId: Long, limit: Long): Long {
        val subquery = Articles
            .select(Articles.id)
            .where { Articles.boardId eq boardId }
            .limit(limit.toInt())
            .alias("t")

        return subquery
            .selectAll()
            .count()
    }
}
