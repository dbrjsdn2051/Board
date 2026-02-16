package com.example.service.data

import com.example.service.entity.Articles
import kuke.board.common.snowflake.Snowflake
import org.jetbrains.exposed.v1.jdbc.insert
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.support.TransactionTemplate
import java.time.LocalDateTime
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors

@SpringBootTest
class DataInitializer {
    @Autowired
    lateinit var transactionTemplate: TransactionTemplate

    val snowflake = Snowflake()
    val latch = CountDownLatch(EXECUTE_COUNT)

    companion object {
        const val BULK_INSERT_SIZE = 2000
        const val EXECUTE_COUNT = 6000
    }

    @Test
    fun initialize() {
        val executorService = Executors.newFixedThreadPool(10)
        for (i in 0 until EXECUTE_COUNT) {
            executorService.submit {
                insert()
                latch.countDown()
                println("latch.count = ${latch.count}")
            }
        }
        latch.await()
        executorService.shutdown()
    }

    fun insert() {
        transactionTemplate.executeWithoutResult {
            for (i in 0 until BULK_INSERT_SIZE) {
                val now = LocalDateTime.now()
                Articles.insert {
                    it[id] = snowflake.nextId()
                    it[title] = "title$i"
                    it[content] = "content$i"
                    it[boardId] = 1L
                    it[writerId] = 1L
                    it[createdAt] = now
                    it[modifiedAt] = now
                }
            }
        }
    }
}
