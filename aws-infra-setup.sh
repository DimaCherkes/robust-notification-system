#!/bin/bash
# Settings
REGION="eu-central-1"
PROFILE="bachelor"

# Check if AWS CLI is installed and profile exists
if ! aws sts get-caller-identity --profile $PROFILE >/dev/null 2>&1; then
    echo "Error: AWS profile '$PROFILE' not found or credentials invalid."
    exit 1
fi

ACCOUNT_ID=$(aws sts get-caller-identity --query Account --output text --profile $PROFILE)

echo "------------------------------------------"
echo "Initializing AWS Infrastructure..."
echo "Account ID: $ACCOUNT_ID"
echo "Region:     $REGION"
echo "Profile:    $PROFILE"
echo "------------------------------------------"

# 1. Create SNS Topics
echo "Creating SNS Topics..."
IAM_TOPIC_ARN=$(aws sns create-topic --name iam-produce-topic --region $REGION --profile $PROFILE --query TopicArn --output text)
SUB_TOPIC_ARN=$(aws sns create-topic --name subscription-produce-topic --region $REGION --profile $PROFILE --query TopicArn --output text)

echo "SNS Topics created:"
echo " - $IAM_TOPIC_ARN"
echo " - $SUB_TOPIC_ARN"

# 2. Create SQS Queues
echo "Creating SQS Queues..."
SUB_QUEUE_URL=$(aws sqs create-queue --queue-name subscription-consume-queue --region $REGION --profile $PROFILE --query QueueUrl --output text)
WEATHER_QUEUE_URL=$(aws sqs create-queue --queue-name weather-consume-queue --region $REGION --profile $PROFILE --query QueueUrl --output text)
DECISION_QUEUE_URL=$(aws sqs create-queue --queue-name decision-consume-queue --region $REGION --profile $PROFILE --query QueueUrl --output text)
NOTIF_QUEUE_URL=$(aws sqs create-queue --queue-name notification-consume-queue --region $REGION --profile $PROFILE --query QueueUrl --output text)

# Get queue ARNs (required for subscriptions)
SUB_QUEUE_ARN=$(aws sqs get-queue-attributes --queue-url $SUB_QUEUE_URL --attribute-names QueueArn --region $REGION --profile $PROFILE --query Attributes.QueueArn --output text)
WEATHER_QUEUE_ARN=$(aws sqs get-queue-attributes --queue-url $WEATHER_QUEUE_URL --attribute-names QueueArn --region $REGION --profile $PROFILE --query Attributes.QueueArn --output text)
DECISION_QUEUE_ARN=$(aws sqs get-queue-attributes --queue-url $DECISION_QUEUE_URL --attribute-names QueueArn --region $REGION --profile $PROFILE --query Attributes.QueueArn --output text)
NOTIF_QUEUE_ARN=$(aws sqs get-queue-attributes --queue-url $NOTIF_QUEUE_URL --attribute-names QueueArn --region $REGION --profile $PROFILE --query Attributes.QueueArn --output text)

echo "SQS Queues created."

# 3. Configure permissions (Allow SNS to send to SQS)
echo "Setting Access Policies (allowing SNS to send messages to SQS)..."
for q_url in $SUB_QUEUE_URL $WEATHER_QUEUE_URL $DECISION_QUEUE_URL $NOTIF_QUEUE_URL; do
    # Get current queue ARN for the policy
    CURRENT_Q_ARN=$(aws sqs get-queue-attributes --queue-url $q_url --attribute-names QueueArn --region $REGION --profile $PROFILE --query Attributes.QueueArn --output text)
    
    POLICY='{
        "Version":"2012-10-17",
        "Statement":[{
            "Effect":"Allow",
            "Principal":"*",
            "Action":"sqs:SendMessage",
            "Resource":"'$CURRENT_Q_ARN'",
            "Condition":{
                "ArnLike":{
                    "aws:SourceArn":"arn:aws:sns:'$REGION':'$ACCOUNT_ID':*"
                }
            }
        }]
    }'
    aws sqs set-queue-attributes --queue-url $q_url --attributes "{\"Policy\":$(echo $POLICY | jq -R .)}" --region $REGION --profile $PROFILE 2>/dev/null || \
    aws sqs set-queue-attributes --queue-url $q_url --attributes "{\"Policy\":\"$(echo $POLICY | sed 's/"/\\"/g')\"}" --region $REGION --profile $PROFILE
done

# 4. Configure Subscriptions with filters
echo "Configuring Subscriptions..."

# IAM Topic -> Subscriptions
aws sns subscribe --topic-arn $IAM_TOPIC_ARN --protocol sqs --notification-endpoint $SUB_QUEUE_ARN --attributes '{"FilterPolicy": "{\"action\": [\"user_delete\"]}"}' --region $REGION --profile $PROFILE > /dev/null
aws sns subscribe --topic-arn $IAM_TOPIC_ARN --protocol sqs --notification-endpoint $NOTIF_QUEUE_ARN --attributes '{"FilterPolicy": "{\"action\": [\"user_create\", \"user_update\", \"user_delete\"]}"}' --region $REGION --profile $PROFILE > /dev/null
aws sns subscribe --topic-arn $IAM_TOPIC_ARN --protocol sqs --notification-endpoint $DECISION_QUEUE_ARN --attributes '{"FilterPolicy": "{\"action\": [\"user_delete\"]}"}' --region $REGION --profile $PROFILE > /dev/null

# Subscription Topic -> Subscriptions
aws sns subscribe --topic-arn $SUB_TOPIC_ARN --protocol sqs --notification-endpoint $WEATHER_QUEUE_ARN --attributes '{"FilterPolicy": "{\"action\": [\"city_activate\", \"city_deactivate\"]}"}' --region $REGION --profile $PROFILE > /dev/null
aws sns subscribe --topic-arn $SUB_TOPIC_ARN --protocol sqs --notification-endpoint $DECISION_QUEUE_ARN --attributes '{"FilterPolicy": "{\"action\": [\"subscription_created\", \"subscription_updated\", \"subscription_deleted\"]}"}' --region $REGION --profile $PROFILE > /dev/null

echo "------------------------------------------"
echo "INFRASTRUCTURE READY!"
echo "------------------------------------------"
echo "Copy these values to your Task Definitions / .env:"
echo ""
echo "SNS_IAM_TOPIC_ARN:    $IAM_TOPIC_ARN"
echo "SNS_SUB_TOPIC_ARN:    $SUB_TOPIC_ARN"
echo ""
echo "SQS_SUB_QUEUE_URL:    $SUB_QUEUE_URL"
echo "SQS_WEATHER_QUEUE_URL: $WEATHER_QUEUE_URL"
echo "SQS_DECISION_QUEUE_URL: $DECISION_QUEUE_URL"
echo "SQS_NOTIF_QUEUE_URL:   $NOTIF_QUEUE_URL"
echo "------------------------------------------"
