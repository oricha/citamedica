package com.citamedica.backend.integration.calcom;

import com.fasterxml.jackson.annotation.JsonProperty;

public class WebhookEvent {
    @JsonProperty("triggerEvent")
    private String triggerEvent;

    private Object payload;

    public String getTriggerEvent() {
        return triggerEvent;
    }

    public void setTriggerEvent(String triggerEvent) {
        this.triggerEvent = triggerEvent;
    }

    public Object getPayload() {
        return payload;
    }

    public void setPayload(Object payload) {
        this.payload = payload;
    }
}