package com.blockchain.cliente.blockchaincliente.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class DigitalDocument implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String description;
    private String data;
    private String userIdOwner;
    private final String typeDoc = "DocsCreated";

    private Map<String,Boolean> signature = new HashMap<>();

    public void addMemberSignature(String member, boolean sign){
        this.signature.put(member, sign);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getUserIdOwner() {
        return userIdOwner;
    }

    public void setUserIdOwner(String userIdOwner) {
        this.userIdOwner = userIdOwner;
    }

    public String getTypeDoc() {
        return typeDoc;
    }

    public Map<String, Boolean> getSignature() {
        return signature;
    }

    public void setSignature(Map<String, Boolean> signature) {
        this.signature = signature;
    }

    public String toJSONString(){
        ObjectMapper mapper = new ObjectMapper();
        try{
            return mapper.writeValueAsString(this);
        }catch (JsonProcessingException ex){
            Logger.getLogger(DigitalDocument.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public DigitalDocument fromJSONString(String json){
        ObjectMapper mapper = new ObjectMapper();
        DigitalDocument digitalDocument = null;
        try{
            digitalDocument = mapper.readValue(json, DigitalDocument.class);
        }catch (JsonProcessingException ex){
            Logger.getLogger(DigitalDocument.class.getName()).log(Level.SEVERE, null, ex);
        }
        return digitalDocument;
    }
}
