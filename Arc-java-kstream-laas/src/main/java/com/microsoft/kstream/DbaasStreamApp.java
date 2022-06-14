package com.microsoft.kstream;

import com.microsoft.kstream.model.otlp.*;
import com.microsoft.kstream.model.dbaas.*;

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
        System.out.println("\n==========================================================");
        System.out.println("➡  INIT KSTREAM ➡➡➡");
        System.out.println("==========================================================");

        // 1. Create stream configuration
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, KafkaConfigs.applicationID);
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, KafkaConfigs.broker);

        // 2. Stream Builder
        StreamsBuilder builder = new StreamsBuilder();

        // 3. Initialize SERializer DESerializers
        Serde<OtlpJSON> otlpSerde = Serdes.serdeFrom(new JsonSerializer<>(), new JsonDeserializer<>(OtlpJSON.class));
        Serde<LaasJSON> dbaasSerde = Serdes.serdeFrom(new JsonSerializer<>(), new JsonDeserializer<>(LaasJSON.class));

        // 4. Create KStream with DbaaS Serdes
        KStream<String, OtlpJSON> KS0 = builder.stream(KafkaConfigs.arcTopic, Consumed.with(Serdes.String(), otlpSerde));
        
        // 5. Transform to DbaaS Topic
        KStream<String, LaasJSON> KS1 = KS0.flatMapValues(otlp -> RecordBuilder.getDbaasRecords(otlp));
        KS1.print(Printed.toSysOut());

        KS1.to(KafkaConfigs.dbaasStandardTopic, Produced.with(Serdes.String(), dbaasSerde));

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