package software.amazon.samples.kafka.lambda;

import java.io.Serializable;

public class KafkaTopicMsg implements Serializable, Cloneable{
    private long store_id;
    private String from_email;
    private String to_email;
    private String send_domain;
    private String recipient_domain;
    private String send_ip;
    private String notify_type;
    private String platform;
    private String created_at;
    private int delivery_code;
    private String delivery_message;
    private String message_id;
    private String template_id;
    private String notify_subtype;
    private String send_at;
    private String email_id;
    private String event_day;


    public long getStore_id() {
        return store_id;
    }

    public void setStore_id(long store_id) {
        this.store_id = store_id;
    }

    public String getFrom_email() {
        return from_email;
    }

    public void setFrom_email(String from_email) {
        this.from_email = from_email;
    }

    public String getTo_email() {
        return to_email;
    }

    public void setTo_email(String to_email) {
        this.to_email = to_email;
    }

    public String getSend_domain() {
        return send_domain;
    }

    public void setSend_domain(String send_domain) {
        this.send_domain = send_domain;
    }

    public String getRecipient_domain() {
        return recipient_domain;
    }

    public void setRecipient_domain(String recipient_domain) {
        this.recipient_domain = recipient_domain;
    }

    public String getSend_ip() {
        return send_ip;
    }

    public void setSend_ip(String send_ip) {
        this.send_ip = send_ip;
    }

    public String getNotify_type() {
        return notify_type;
    }

    public void setNotify_type(String notify_type) {
        this.notify_type = notify_type;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public int getDelivery_code() {
        return delivery_code;
    }

    public void setDelivery_code(int delivery_code) {
        this.delivery_code = delivery_code;
    }

    public String getDelivery_message() {
        return delivery_message;
    }

    public void setDelivery_message(String delivery_message) {
        this.delivery_message = delivery_message;
    }

    public String getMessage_id() {
        return message_id;
    }

    public void setMessage_id(String message_id) {
        this.message_id = message_id;
    }

    public String getTemplate_id() {
        return template_id;
    }

    public void setTemplate_id(String template_id) {
        this.template_id = template_id;
    }

    public String getNotify_subtype() {
        return notify_subtype;
    }

    public void setNotify_subtype(String notify_subtype) {
        this.notify_subtype = notify_subtype;
    }

    public String getSend_at() {
        return send_at;
    }

    public void setSend_at(String send_at) {
        this.send_at = send_at;
    }

    public String getEmail_id() {
        return email_id;
    }

    public void setEmail_id(String email_id) {
        this.email_id = email_id;
    }

    public String getEvent_day() {
        return event_day;
    }
    
    public void setEvent_day(String event_day) {
        this.event_day = event_day;
    }

    
}