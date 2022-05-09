package com.microsoft.kstream;

import com.microsoft.kstream.model.otlp.*;

import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.*;

import com.microsoft.kstream.serde.JsonSerializer;
import com.microsoft.kstream.serde.JsonDeserializer;

import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;

import java.util.*;

public class DbaasStreamApp {

    public static void main(String[] args) {
        // TODO:
        // 4. DbaaS/LaaS model
        // 5. Transformation

        System.out.println("\n==========================================================");
        System.out.println("➡  INIT KSTREAM ➡➡➡");
        System.out.println("==========================================================");

        // 1. Create stream configuration
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, FanOutConfigs.applicationID);
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, FanOutConfigs.broker);

        // 2. Stream Builder
        StreamsBuilder builder = new StreamsBuilder();

        // 3. Initialize OTEL specific Serdes
        Serde<OtlpJSON> otlpSerde = Serdes.serdeFrom(new JsonSerializer<>(), new JsonDeserializer<>(OtlpJSON.class));

        // 4. Create KStream with DbaaS Serdes
        KStream<String, OtlpJSON> KS0 = builder.stream(FanOutConfigs.arcTopic, Consumed.with(Serdes.String(), otlpSerde));

        // 5. Process Stream
        // Print as-is to console
        KS0.print(Printed.toSysOut());

        // Write as is to RAW topic
        KS0.to(FanOutConfigs.dbaasStandardTopic, Produced.with(Serdes.String(), otlpSerde));

        // Get resourceLogs.scopeLogs.logRecords.body.stringValue and write to dbaas.flat
        KS0.mapValues(value -> value.getResourceLogs())
            .flatMapValues(value -> value)
            .mapValues(value -> value.getScopeLogs())
            .flatMapValues(value -> value)
            .mapValues(value -> value.getLogRecords())
            .flatMapValues(value -> value)
            .mapValues(value -> value.getBody().getStringValue())
            .to("dbaas.flat", Produced.with(Serdes.String(), Serdes.String()));

        // 6. Create Topology and create Stream

        Topology dbaasTopology = builder.build();
        
        System.out.println("\n➡  STARTING THE FOLLOWING TOPOLOGY:");
        System.out.println(dbaasTopology.describe().toString());

        KafkaStreams dbaaStream = new KafkaStreams(dbaasTopology, props);
        dbaaStream.start();

        // 7. Attach shutdown handler to runtime to catch SIGTERM (aka Ctrl-C)
        Runtime.getRuntime().addShutdownHook(new Thread( () -> {
            System.out.println("➡  STOPPING STREAM...");
            dbaaStream.close();
        }));

        System.out.println("==========================================================");
        System.out.println("➡  STARTED ✔");
        System.out.println("==========================================================");
    }
}