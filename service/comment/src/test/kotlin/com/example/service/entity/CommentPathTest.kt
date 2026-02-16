package com.example.service.entity

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test

class CommentPathTest {

    @Test
    fun createChildCommentTest() {
        // 00000 <- 생성
        createChildCommentTest(CommentPath.create(""), null, "00000")

        // 00000
        //      00000 <- 생성
        createChildCommentTest(CommentPath.create("00000"), null, "0000000000")

        // 00000
        // 00001 <- 생성
        createChildCommentTest(CommentPath.create(""), "00000", "00001")

        // 0000z
        //      abcdz
        //          zzzzz
        //              zzzzz
        //      abce0 <- 생성
        createChildCommentTest(CommentPath.create("0000z"), "0000zabcdzzzzzzzzzzz", "0000zabce0")
    }

    private fun createChildCommentTest(commentPath: CommentPath, descendantsTopPath: String?, expectedChildPath: String) {
        val childCommentPath = commentPath.createChildCommentPath(descendantsTopPath)
        assertThat(childCommentPath.path).isEqualTo(expectedChildPath)
    }

    @Test
    fun createChildCommentPathIfMaxDepthTest() {
        assertThatThrownBy {
            CommentPath.create("zzzzz".repeat(5)).createChildCommentPath(null)
        }.isInstanceOf(IllegalStateException::class.java)
    }

    @Test
    fun createChildCommentPathIfChunkOverflowTest() {
        val commentPath = CommentPath.create("")

        assertThatThrownBy { commentPath.createChildCommentPath("zzzzz") }
            .isInstanceOf(IllegalStateException::class.java)
    }
}
