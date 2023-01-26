package com.example.samistax.astra.service;


import com.example.samistax.astra.data.StockPrice;
import org.apache.pulsar.client.api.*;
import org.apache.pulsar.client.impl.schema.JSONSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import javax.annotation.PreDestroy;
import java.io.IOException;

@Service
public class AstraStreaming {

    @Value("${pulsar.cdc-topic-url")
    private String PULSAR_CDC_TOPIC_URL;
    @Value("${pulsar.topic-url}")
    private String PULSAR_TOPIC_URL;
    @Value("${pulsar.service.url}")
    private String SERVICE_URL;
    @Value("${pulsar.service.token}")
    private String PULSAR_TOKEN;
    @Value("${pulsar.service.subscription-custom-name:DataStock App}")
    private String PULSAR_SUBSCRIPTION_NAME;

    private PulsarClient client;
    private Producer<StockPrice> producer;
    private Consumer<StockPrice> consumer;
    private Reader<StockPrice> reader;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final Schema<StockPrice> schema = JSONSchema.of(StockPrice.class);

    public AstraStreaming() {}

    @Bean
    public PulsarClient createPulsarClient() {
        try {
            this.client =  PulsarClient.builder()
                    .serviceUrl(SERVICE_URL)
                    .authentication(AuthenticationFactory.token(PULSAR_TOKEN))
                    .build();

        } catch (PulsarClientException pce) {
            logger.info("Pulsar Client exception", pce);
        }
        return this.client;
    }

    public Reader<StockPrice> getReader() {
    // Create reusable producer instance to stream chat messages to other consumers
        if ( client != null && reader == null ) {
            // Create reader on a topic
            try {
                reader = client.newReader(schema)
                        .readerName(PULSAR_SUBSCRIPTION_NAME + " reader")
                        .topic(PULSAR_TOPIC_URL)
                        .startMessageId(MessageId.latest)
                        .create();
            } catch (PulsarClientException e) {
                throw new RuntimeException(e);
            }
        }
        return reader;
    }

    public Consumer<StockPrice> getConsumer() {

        // Create reusable producer instance to stream chat messages to other consumers
        if ( client != null && consumer == null ) {
            try {
                consumer = client.newConsumer(schema)
                        .consumerName("DataStock App consumer")
                        .subscriptionType(SubscriptionType.Key_Shared)
                        .topic(PULSAR_TOPIC_URL)
                        .subscriptionName(PULSAR_SUBSCRIPTION_NAME + " consumer")
                        .subscribe();
            } catch (PulsarClientException e) {
                throw new RuntimeException(e);
            }
        }
        return consumer;
    }
    public Producer<StockPrice> getProducer() {

        // Create reusable producer instance to stream chat messages to other consumers
        if ( client != null && producer == null ) {
            try {
                producer = client.newProducer(schema)
                        .topic(PULSAR_TOPIC_URL)
                        .producerName(PULSAR_SUBSCRIPTION_NAME + " producer")
                        .create();
            } catch (PulsarClientException e) {
                throw new RuntimeException(e);
            }
        }
        return producer;
    }

    @PreDestroy
    public void preDestroy() {
        logger.info("Closing Pulsar connections");
        if ( producer != null ) {
            try {
                producer.close();
            } catch (PulsarClientException e) {
                logger.debug("Exception while closing Pulsar Producer", e);
            }
        }
        if ( consumer != null ) {
            try {
                consumer.close();
            } catch (PulsarClientException e) {
                logger.debug("Exception while closing Pulsar Consumer", e);
            }
        }
        if ( reader != null ) {
            try {
                reader.close();
            } catch (IOException e) {
                logger.debug("Exception while closing Pulsar Reader", e);
            }
        }
        try {
            if (! client.isClosed() ) {
                client.close();
            }
        } catch (PulsarClientException e) {
            logger.debug("Exception while closing Pulsar Client", e);
        }
    }
}
