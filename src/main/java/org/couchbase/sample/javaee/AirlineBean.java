package org.couchbase.sample.javaee;

import com.couchbase.client.deps.com.fasterxml.jackson.core.JsonProcessingException;
import com.couchbase.client.deps.com.fasterxml.jackson.databind.ObjectMapper;
import com.couchbase.client.java.document.JsonDocument;
import com.couchbase.client.java.document.json.JsonObject;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author arungupta
 */
public class AirlineBean {
    private String country;
    private String iata;
    private String callsign;
    private String name;
    private String icao;
    private String id;
    private final String type = "airline";

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getIata() {
        return iata;
    }

    public void setIata(String iata) {
        this.iata = iata;
    }

    public String getCallsign() {
        return callsign;
    }

    public void setCallsign(String callsign) {
        this.callsign = callsign;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIcao() {
        return icao;
    }

    public void setIcao(String icao) {
        this.icao = icao;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }
    
    static JsonDocument toJson(AirlineBean bean, long counter) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        bean.setId(String.valueOf(counter));
        String json = mapper.writeValueAsString(bean);
        
        return JsonDocument.create("airline_" + bean.getId(), JsonObject.fromJson(json));
    }
    
    static JsonDocument toJson(AirlineBean bean) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(bean);
        
        return JsonDocument.create("airline_" + bean.getId(), JsonObject.fromJson(json));
    }
    
    static AirlineBean fromJson(JsonDocument json) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(json.content().toString(), AirlineBean.class);
    }
    
    @Override
    public String toString() {
        try {
            return toJson(this).content().toString();
        } catch (JsonProcessingException ex) {
            Logger.getLogger(AirlineBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
}
