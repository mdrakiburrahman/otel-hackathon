package com.microsoft.kstream;

import org.apache.kafka.clients.consumer.ConsumerConfig;

import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;

import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Printed;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Produced;

import java.util.Arrays;
import java.util.Properties;

import com.microsoft.kstream.model.*;
import com.microsoft.kstream.serde.JsonSerializer;
import com.microsoft.kstream.serde.JsonDeserializer;


public final class KSTREAM {

    public static void main(String[] args) {
        // = = = = = = = = = = =
        // Environment Variables
        // = = = = = = = = = = =
        final var topic = System.getenv("KAFKA_TOPIC");
        final var broker = System.getenv("KAFKA_BROKER_ADDRESS");
        final var consumer_self = System.getenv("KAFKA_CONSUMER_NAME_SELF");

        System.out.println("\n==========================================================");
        System.out.println("➡  KSTREAM");
        System.out.println("==========================================================");

        // Kafka Properties
        final Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, consumer_self);
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, broker);
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, "0");

        // Stream Builder
        final StreamsBuilder streamsBuilder = new StreamsBuilder();

        // SERDE from our POJO
        final Serde<ResourceLog> resourceLogSerde = Serdes.serdeFrom(new JsonSerializer<>(),
                                                               new JsonDeserializer<>(ResourceLog.class));

        final KStream<Long, ResourceLog> resourceLog = streamsBuilder.stream(topic, Consumed.with(Serdes.Long(), resourceLogSerde));        
        resourceLog.print(Printed.toSysOut());

        final KafkaStreams streams = new KafkaStreams(streamsBuilder.build(), props);
        System.out.println("➡  STARTING STREAM...");
        streams.start();

        // Attach shutdown handler to catch control-c
        Runtime.getRuntime().addShutdownHook(new Thread( () -> {
            System.out.println("➡  STOPPING STREAM...");
            streams.close();
        }));

        System.out.println("\n==========================================================");
        System.out.println("➡  STARTED");
        System.out.println("==========================================================");
    }
}