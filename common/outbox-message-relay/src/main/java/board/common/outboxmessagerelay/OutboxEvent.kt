package board.common.outboxmessagerelay

data class OutboxEvent(
    val outbox: Outbox
) {
    companion object {
        fun of(outbox: Outbox): OutboxEvent {
            return OutboxEvent(outbox)
        }
    }
}
