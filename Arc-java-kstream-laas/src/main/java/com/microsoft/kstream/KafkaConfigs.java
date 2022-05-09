package com.microsoft.kstream;

public class KafkaConfigs {
    final static String arcTopic = System.getenv("KAFKA_TOPIC");
    final static String broker = System.getenv("KAFKA_BROKER_ADDRESS");
    final static String applicationID = System.getenv("KAFKA_CONSUMER_NAME_SELF");
    final static String dbaasStandardTopic = "dbaas.standard";
}
