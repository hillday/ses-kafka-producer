// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: MIT-0
package software.amazon.samples.kafka.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryResponse;
import software.amazon.lambda.powertools.logging.Logging;
import software.amazon.lambda.powertools.tracing.Tracing;
import com.amazonaws.services.lambda.runtime.events.SNSEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

public class SimpleKafkaProxy implements RequestHandler<SNSEvent, Object> {

    public static final String TOPIC_NAME = "messages";
    public static final String DDB_SES_TB = "ses_my_poc";
    public static final String DDB_SES_PK = "ses_pk";
    public static final String DDB_SES_SK = "ses_sk";

    private static final Logger log = LogManager.getLogger(SimpleKafkaProxy.class);
    public KafkaProducerPropertiesFactory kafkaProducerProperties = new KafkaProducerPropertiesFactoryImpl();
    private KafkaProducer<String, String> producer;

    @Override
    @Tracing
    // @Logging(logEvent = true)
    public Object handleRequest(SNSEvent request, Context context) {
        // APIGatewayProxyResponseEvent response = createEmptyResponse();
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JodaModule());

            // SES Event parse to Object
            SESEvent sesEvent = mapper.readValue(request.getRecords().get(0).getSNS().getMessage(), SESEvent.class);

            // Create Kafka topic msg
            KafkaTopicMsg topicMsg = createKafkaMsg(sesEvent);
            ObjectMapper mapperToStr = new ObjectMapper(); 

            String message = mapperToStr.writeValueAsString(topicMsg);

            log.info("push message to kafka:" + message);

            KafkaProducer<String, String> producer = createProducer();

            ProducerRecord<String, String> record = new ProducerRecord<String, String>(TOPIC_NAME,
                    context.getAwsRequestId(), message);

             // Send Kafka topic msg to Server
            Future<RecordMetadata> send = producer.send(record);
            producer.flush();

            RecordMetadata metadata = send.get();

            log.info(String.format("Message was send to partition %s", metadata.partition()));

            // return response.withStatusCode(200).withBody("Message successfully pushed to
            // kafka");

            /* 
            String timeStamp = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss").format(Calendar.getInstance().getTime());
            context.getLogger().log("Invocation started: " + timeStamp);
            context.getLogger().log(request.getRecords().get(0).getSNS().getMessage());

            timeStamp = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss").format(Calendar.getInstance().getTime());
            context.getLogger().log("Invocation completed: " + timeStamp);
            */
            return null;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    @Tracing
    private KafkaProducer<String, String> createProducer() {
        if (producer == null) {
            log.info("Connecting to kafka cluster");
            producer = new KafkaProducer<String, String>(kafkaProducerProperties.getProducerProperties());
        }
        return producer;
    }

    public static String getNow() {
        Date date = new Date(); // this object contains the current date value
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        return formatter.format(date);
    }

    public static KafkaTopicMsg createKafkaMsg(SESEvent sesEvent) {
        KafkaTopicMsg topicMsg = new KafkaTopicMsg();
        DynamoDbClient ddbClient = DynamoDbClient.create();

        long storeId = NumUtil.nextPkId();
        topicMsg.setStore_id(storeId);
        topicMsg.setDelivery_code(0);
        topicMsg.setDelivery_message("success");
        if (sesEvent.getNotificationType().equalsIgnoreCase("complaint")) {
            topicMsg.setDelivery_code(1);
            topicMsg.setDelivery_message("complaint");
            topicMsg.setNotify_subtype(sesEvent.getComplaint().getComplaintFeedbackType());
        }
        if (sesEvent.getNotificationType().equalsIgnoreCase("bounce")) {
            topicMsg.setDelivery_code(2);
            topicMsg.setDelivery_message("bounce");
            topicMsg.setNotify_subtype(sesEvent.getBounce().getBounceType());
        }
        topicMsg.setFrom_email(sesEvent.getMail().getSource());
        topicMsg.setCreated_at(getNow());
        topicMsg.setPlatform("SES");
        topicMsg.setEmail_id(sesEvent.getMail().getSource());
        topicMsg.setSend_ip(sesEvent.getMail().getSourceIp());
        topicMsg.setTo_email(sesEvent.getMail().getDestination().get(0));

        String sender = sesEvent.getMail().getSource();
        String messageId = sesEvent.getMail().getMessageId();
        // to poc sender and messgeId fixed
        sender = "fff@qq.com";
        messageId = "010001850f778974-bf04795b-1a00-473a-9332-da576ab704aa-000000";
        List<Map<String, AttributeValue>> items = queryTable(ddbClient, DDB_SES_TB, DDB_SES_PK, sender, DDB_SES_SK,
                messageId);

        if (items.size() > 0) {
            Map<String, AttributeValue> item = items.get(0);
            topicMsg.setTemplate_id(item.get("template_id").s());

        } else {
            log.error("Can not get item with message:" + messageId);
        }

        return topicMsg;
    }

    public static List<Map<String, AttributeValue>> queryTable(DynamoDbClient ddb, String tableName,
            String partitionKeyName, String partitionKeyVal, String SortKeyName, String SortKeyVal) {

        // Set up mapping of the partition name with the value.
        HashMap<String, AttributeValue> attrValues = new HashMap<>();

        attrValues.put(":" + partitionKeyName, AttributeValue.builder()
                .s(partitionKeyVal)
                .build());

        attrValues.put(":" + SortKeyName, AttributeValue.builder()
                .s(SortKeyVal)
                .build());

        QueryRequest queryReq = QueryRequest.builder()
                .tableName(tableName)
                .keyConditionExpression(partitionKeyName + " = :" + partitionKeyName + " and begins_with (" + SortKeyName
                        + ", :" + SortKeyName + ")")
                .expressionAttributeValues(attrValues)
                .build();

        try {
            QueryResponse response = ddb.query(queryReq);
            return response.items();

        } catch (DynamoDbException e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

}
