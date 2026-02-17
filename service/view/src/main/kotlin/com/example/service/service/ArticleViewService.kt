package com.example.service.service

import com.example.service.repository.ArticleViewCountRepository
import com.example.service.repository.ArticleViewDistributedLockRepository
import org.springframework.stereotype.Service
import java.time.Duration

@Service
class ArticleViewService(
    private val articleViewCountRepository: ArticleViewCountRepository,
    private val articleViewCountBackUpProcessor: ArticleViewCountBackUpProcessor,
    private val articleViewDistributedLockRepository: ArticleViewDistributedLockRepository
) {

    companion object {
        private const val BACK_UP_BATCH_SIZE = 100
        private val TTL: Duration = Duration.ofMinutes(10)
    }

    fun increase(articleId: Long, userId: Long): Long {
        if (!articleViewDistributedLockRepository.lock(articleId, userId, TTL)) {
            return articleViewCountRepository.read(articleId)
        }

        val count = articleViewCountRepository.increase(articleId)
        if ((count % BACK_UP_BATCH_SIZE).toInt() == 0) {
            articleViewCountBackUpProcessor.backUp(articleId, userId)
        }

        return count
    }

    fun count(articleId: Long): Long {
        return articleViewCountRepository.read(articleId)
    }
}