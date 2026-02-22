package board.common.outboxmessagerelay

class AssignedShard(
    val shards: List<Long>
) {
    companion object {
        fun of(appId: String, appIds: List<String>, shardCount: Long): AssignedShard {
            return AssignedShard(assign(appId, appIds, shardCount))
        }

        private fun assign(appId: String, appIds: List<String>, shardCount: Long): List<Long> {
            val appIndex = appIds.indexOf(appId)
            if (appIndex == -1) {
                return emptyList()
            }

            val start = appIndex * shardCount / appIds.size
            val end = (appIndex + 1) * shardCount / appIds.size - 1

            return (start..end).toList()
        }
    }
}
