package com.example.service.entity

class CommentPath private constructor(val path: String) {

    companion object {
        private const val CHARSET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"
        private const val DEPTH_CHUNK_SIZE = 5
        private const val MAX_DEPTH = 5

        private val MIN_CHUNK = CHARSET[0].toString().repeat(DEPTH_CHUNK_SIZE)
        private val MAX_CHUNK = CHARSET[CHARSET.length - 1].toString().repeat(DEPTH_CHUNK_SIZE)

        fun create(path: String): CommentPath {
            if (isDepthOverflowed(path)) {
                throw IllegalStateException("depth overflowed")
            }
            return CommentPath(path)
        }

        private fun isDepthOverflowed(path: String): Boolean {
            return calDepth(path) > MAX_DEPTH
        }

        private fun calDepth(path: String): Int {
            return path.length / DEPTH_CHUNK_SIZE
        }
    }

    val depth: Int get() = calDepth(path)

    fun isRoot(): Boolean = calDepth(path) == 1

    fun getParentPath(): String = path.substring(0, path.length - DEPTH_CHUNK_SIZE)

    fun createChildCommentPath(descendantsTopPath: String?): CommentPath {
        if (descendantsTopPath == null) {
            return create(path + MIN_CHUNK)
        }
        val childrenTopPath = findChildrenTopPath(descendantsTopPath)
        return create(increase(childrenTopPath))
    }

    private fun findChildrenTopPath(descendantsTopPath: String): String {
        return descendantsTopPath.substring(0, (depth + 1) * DEPTH_CHUNK_SIZE)
    }

    private fun increase(path: String): String {
        val lastChunk = path.substring(path.length - DEPTH_CHUNK_SIZE)
        if (isChunkOverflowed(lastChunk)) {
            throw IllegalStateException("chunk overflowed")
        }

        val charsetLength = CHARSET.length

        var value = 0
        for (ch in lastChunk) {
            value = value * charsetLength + CHARSET.indexOf(ch)
        }

        value += 1

        val result = StringBuilder()
        for (i in 0 until DEPTH_CHUNK_SIZE) {
            result.insert(0, CHARSET[value % charsetLength])
            value /= charsetLength
        }

        return path.substring(0, path.length - DEPTH_CHUNK_SIZE) + result.toString()
    }

    private fun isChunkOverflowed(lastChunk: String): Boolean {
        return MAX_CHUNK == lastChunk
    }
}
