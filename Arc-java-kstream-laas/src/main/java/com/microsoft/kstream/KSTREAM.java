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

import com.microsoft.kstream.serde.JsonSerializer;
import com.microsoft.kstream.model.otlp.*;
import com.microsoft.kstream.serde.JsonDeserializer;


public class KSTREAM {

    public static void main(String[] args) {
        System.out.println("\n==========================================================");
        System.out.println("➡  INIT KSTREAM ➡➡➡");
        System.out.println("==========================================================");

        // 1. Create stream configuration
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, FanOutConfigs.applicationID);
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, FanOutConfigs.broker);
        props.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, "0");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        // 2. Stream Builder
        StreamsBuilder builder = new StreamsBuilder();

        // 3. Generate a SERDE from our JSON POJO - MOVE TO OWN CLASS
        Serde<OtlpJSON> otlpSerde = Serdes.serdeFrom(new JsonSerializer<>(),
                                                               new JsonDeserializer<>(OtlpJSON.class));

        KStream<String, OtlpJSON> otlp = builder.stream(FanOutConfigs.arcTopic, Consumed.with(Serdes.String(), otlpSerde));        

        // 4. Process Stream
        // Print as-is to console
        otlp.print(Printed.toSysOut());

        // Write as is to RAW topic
        otlp.to(FanOutConfigs.dbaasStandardTopic, Produced.with(Serdes.String(), otlpSerde));

        // Get resourceLogs.scopeLogs.logRecords.body.stringValue and write to dbaas.flat
        otlp.mapValues(value -> value.getResourceLogs())
            .flatMapValues(value -> value)
            .mapValues(value -> value.getScopeLogs())
            .flatMapValues(value -> value)
            .mapValues(value -> value.getLogRecords())
            .flatMapValues(value -> value)
            .mapValues(value -> value.getBody().getStringValue())
            .to("dbaas.flat", Produced.with(Serdes.String(), Serdes.String()));

        // 5. Create Topology and create Stream
        KafkaStreams streams = new KafkaStreams(builder.build(), props);
        System.out.println("➡  STARTING STREAM...");
        streams.start();

        // 6. Attach shutdown handler to runtime to catch SIGTERM (aka Ctrl-C)
        Runtime.getRuntime().addShutdownHook(new Thread( () -> {
            System.out.println("➡  STOPPING STREAM...");
            streams.close();
        }));

        System.out.println("\n==========================================================");
        System.out.println("➡➡➡  STARTED ✔");
        System.out.println("==========================================================");
    }
}