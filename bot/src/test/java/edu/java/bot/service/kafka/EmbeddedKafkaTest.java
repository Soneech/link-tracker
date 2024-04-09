package edu.java.bot.service.kafka;

import edu.java.bot.configuration.ApplicationConfig;
import edu.java.bot.dto.request.LinkUpdateRequest;
import edu.java.bot.service.UpdateService;
import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;

@SpringBootTest
@DirtiesContext
@EmbeddedKafka(partitions = 1)
@ActiveProfiles("test")
public class EmbeddedKafkaTest {

    @Autowired
    private DlqProducer dlqProducer;

    @SpyBean
    private UpdateService updateService;

    @Autowired
    private ApplicationConfig config;

    private static LinkUpdateRequest linkUpdateRequest;

    private static final String DLQ_CONSUMER_GROUP_ID = "dlq-messages";

    @BeforeAll
    public static void setUp() {
        linkUpdateRequest = new LinkUpdateRequest(1589L, "https://github.com/Soneech/link-tracker",
            "Новый коммит", List.of(98489L, 8947L)
        );
    }

    @Test
    public void testSendMessageToDlq() {
        try (KafkaConsumer<String, LinkUpdateRequest> kafkaConsumer = new KafkaConsumer<>(consumerProps())) {

            dlqProducer.send(linkUpdateRequest, config.kafka().dlqTopic().name());

            kafkaConsumer.subscribe(Collections.singletonList(config.kafka().dlqTopic().name()));

            ConsumerRecords<String, LinkUpdateRequest> record = kafkaConsumer.poll(Duration.ofMillis(5000L));
            kafkaConsumer.close();

            assertThat(record).isNotNull();
            assertThat(record.count()).isOne();
            assertThat(record.records(config.kafka().dlqTopic().name()).iterator().next().value())
                .isEqualTo(linkUpdateRequest);
        }
    }

    @Test
    public void testReceiveMessageFromUpdatesQueue() {
        try (KafkaProducer<String, LinkUpdateRequest> kafkaProducer = new KafkaProducer<>(producerProps())) {

            ProducerRecord<String, LinkUpdateRequest> record =
                new ProducerRecord<>(config.kafka().linkUpdatesTopic().name(), linkUpdateRequest);
            kafkaProducer.send(record);

            await()
                .atMost(Duration.ofMillis(5000L))
                    .untilAsserted(() -> verify(updateService).processUpdate(linkUpdateRequest));
        }
    }

    public Map<String, Object> consumerProps() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, config.kafka().bootstrapServers());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, DLQ_CONSUMER_GROUP_ID);

        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        props.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JsonDeserializer.class);

        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(JsonDeserializer.TRUSTED_PACKAGES, config.kafka().trustedPackages());
        props.put(JsonDeserializer.TYPE_MAPPINGS, config.kafka().typeMappings());

        return props;
    }

    public Map<String, Object> producerProps() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, config.kafka().bootstrapServers());
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        return props;
    }
}
