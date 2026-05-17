package com.dmitrycherkes.notificationservice.config;

import io.awspring.cloud.sqs.config.SqsMessageListenerContainerFactory;
import io.awspring.cloud.sqs.support.converter.SqsMessagingMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;

import java.net.URI;

@Configuration
public class AwsConfig {

    @Value("${spring.cloud.aws.region.static:eu-central-1}")
    private String region;

    @Value("${spring.cloud.aws.endpoint:}")
    private String endpoint;

    @Value("${spring.cloud.aws.credentials.access-key:}")
    private String accessKey;

    @Value("${spring.cloud.aws.credentials.secret-key:}")
    private String secretKey;

    @Bean
    public AwsCredentialsProvider awsCredentialsProvider() {
        if (!accessKey.isEmpty() && !secretKey.isEmpty()) {
            return StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey));
        }
        return DefaultCredentialsProvider.create();
    }

    @Bean
    public SqsAsyncClient sqsAsyncClient(AwsCredentialsProvider credentialsProvider) {
        var builder = SqsAsyncClient.builder()
                .region(Region.of(region))
                .credentialsProvider(credentialsProvider);
        if (!endpoint.isEmpty()) {
            builder.endpointOverride(URI.create(endpoint));
        }
        return builder.build();
    }

    @Bean
    public SqsMessageListenerContainerFactory<Object> defaultSqsListenerContainerFactory(SqsAsyncClient sqsAsyncClient) {
        return SqsMessageListenerContainerFactory
                .builder()
                .sqsAsyncClient(sqsAsyncClient)
                .configure(options -> options
                        .messageConverter(new SqsMessagingMessageConverter()))
                .build();
    }
}
