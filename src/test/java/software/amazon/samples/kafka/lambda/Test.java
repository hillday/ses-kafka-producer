package software.amazon.samples.kafka.lambda;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;

public class Test {
    public static void main(String[] args) throws JsonProcessingException{
        KafkaTopicMsg topicMsg = new KafkaTopicMsg();
        topicMsg.setStore_id(NumUtil.nextPkId());
        topicMsg.setFrom_email("sddd");
        ObjectMapper mapperToStr = new ObjectMapper(); 

        String message = mapperToStr.writeValueAsString(topicMsg);
        System.out.println(message);


        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JodaModule());

        SESEvent sesEvent = mapper.readValue(data_json, SESEvent.class);

        System.out.println(sesEvent.getNotificationType());
        
    }

    static String data_json = "{\"notificationType\":\"Bounce\",\"bounce\":{\"feedbackId\":\"01000185252980d8-b1384fd9-ad6a-4a5f-a650-d52768d2f983-000000\",\"bounceType\":\"Permanent\",\"bounceSubType\":\"General\",\"bouncedRecipients\":[{\"emailAddress\":\"bounce@simulator.amazonses.com\",\"action\":\"failed\",\"status\":\"5.1.1\",\"diagnosticCode\":\"smtp; 550 5.1.1 user unknown\"}],\"timestamp\":\"2022-12-18T12:15:55.000Z\",\"remoteMtaIp\":\"34.237.62.86\",\"reportingMTA\":\"dns; a8-62.smtp-out.amazonses.com\"},\"mail\":{\"timestamp\":\"2022-12-18T12:15:54.654Z\",\"source\":\"adasfad@ses.love.gho3d.com\",\"sourceArn\":\"arn:aws:ses:us-east-1:472676849672:identity/ses.love.gho3d.com\",\"sourceIp\":\"113.118.117.170\",\"callerIdentity\":\"qchunhai04\",\"sendingAccountId\":\"472676849672\",\"messageId\":\"0100018525297f1e-407d8fbc-1ef5-43e9-9e4c-07334a574265-000000\",\"destination\":[\"bounce@simulator.amazonses.com\"],\"headersTruncated\":false,\"headers\":[{\"name\":\"From\",\"value\":\"adasfad@ses.love.gho3d.com\"},{\"name\":\"To\",\"value\":\"bounce@simulator.amazonses.com\"},{\"name\":\"Subject\",\"value\":\"eeeee\"},{\"name\":\"MIME-Version\",\"value\":\"1.0\"},{\"name\":\"Content-Type\",\"value\":\"multipart/alternative;  boundary=\\\"----=_Part_1374639_280826622.1671365754661\\\"\"}],\"commonHeaders\":{\"from\":[\"adasfad@ses.love.gho3d.com\"],\"to\":[\"bounce@simulator.amazonses.com\"],\"subject\":\"eeeee\"}}}"; 
}
