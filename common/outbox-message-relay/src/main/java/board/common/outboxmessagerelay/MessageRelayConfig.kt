package board.common.outboxmessagerelay

import org.apache.kafka.clients.producer.ProducerConfig
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import org.apache.kafka.common.serialization.StringSerializer
import java.util.concurrent.Executor
import java.util.concurrent.Executors

@EnableAsync
@Configuration
@EnableScheduling
@ComponentScan("board.common.outboxmessagerelay")
class MessageRelayConfig {
    @Value("\${spring.kafka.bootstrap-servers}")
    private lateinit var bootstrapServers: String

    @Bean
    fun messageRelayKafkaTemplate(): KafkaTemplate<String, String> {
        val configProps = mutableMapOf<String, Any>()
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers)
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer::class.java.name)
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer::class.java.name)
        configProps.put(ProducerConfig.ACKS_CONFIG, "all")
        return KafkaTemplate(DefaultKafkaProducerFactory<String, String>(configProps))
    }

    @Bean
    fun messageRelayPublishEventExecutor(): Executor {
        return ThreadPoolTaskExecutor().apply {
            corePoolSize = 20
            maxPoolSize = 50
            queueCapacity = 100
            setThreadNamePrefix("mr-pub-event-")
        }
    }

    @Bean
    fun messageRelayPublishPendingEventExecutor(): Executor {
        return Executors.newSingleThreadExecutor()
    }
}