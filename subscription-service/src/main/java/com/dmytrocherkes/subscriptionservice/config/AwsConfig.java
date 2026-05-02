package com.dmytrocherkes.subscriptionservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.SqsClient;

import java.net.URI;

/**
 * AWS SDK v2 low-level client configuration.
 * This class provides the base AWS clients used by the application.
 * It supports switching between static credentials (for local development)
 * and DefaultCredentialsProvider (for AWS IAM roles in cloud environments).
 */
@Configuration
public class AwsConfig {

    @Value("${spring.cloud.aws.region.static}")
    private String region;

    @Value("${spring.cloud.aws.endpoint}")
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

    /**
     * Asynchronous SQS client required by Spring Cloud AWS 3.x for non-blocking message processing.
     * This is the primary client used by @SqsListener and SqsTemplate.
     */
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

    /**
     * Synchronous SQS client for blocking operations or legacy SDK support.
     * Manual SDK calls often require this blocking client.
     */
    @Bean
    public SqsClient sqsClient(AwsCredentialsProvider credentialsProvider) {
        var builder = SqsClient.builder()
                .region(Region.of(region))
                .credentialsProvider(credentialsProvider);
        if (!endpoint.isEmpty()) {
            builder.endpointOverride(URI.create(endpoint));
        }
        return builder.build();
    }

    /**
     * Synchronous SNS client for publishing messages to topics.
     */
    @Bean
    public SnsClient snsClient(AwsCredentialsProvider credentialsProvider) {
        var builder = SnsClient.builder()
                .region(Region.of(region))
                .credentialsProvider(credentialsProvider);
        if (!endpoint.isEmpty()) {
            builder.endpointOverride(URI.create(endpoint));
        }
        return builder.build();
    }
}
