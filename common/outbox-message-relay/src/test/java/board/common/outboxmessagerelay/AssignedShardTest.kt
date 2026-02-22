package board.common.outboxmessagerelay

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class AssignedShardTest {

    @Test
    fun ofTest() {
        // given
        val shardCount = 64L
        val appList = listOf("appId1", "appId2", "appId3")

        // when
        val assignedShard1 = AssignedShard.of(appList[0], appList, shardCount)
        val assignedShard2 = AssignedShard.of(appList[1], appList, shardCount)
        val assignedShard3 = AssignedShard.of(appList[2], appList, shardCount)
        val assignedShard4 = AssignedShard.of("invalid", appList, shardCount)

        // then
        val result = listOf(
            assignedShard1.shards,
            assignedShard2.shards,
            assignedShard3.shards,
            assignedShard4.shards
        ).flatten()

        assertThat(result).hasSize(shardCount.toInt())

        for (i in 0 until 64) {
            assertThat(result[i]).isEqualTo(i.toLong())
        }

        assertThat(assignedShard4.shards).isEmpty()
    }
}
