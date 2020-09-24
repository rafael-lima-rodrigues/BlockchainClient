package com.blockchain.cliente.blockchaincliente.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class DigitalSign implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String data;
    private String userIdOwner;
    private final String typeDoc = "DocSigned";

    private List<String> listUserMembers = new ArrayList<>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public List<String> getListUserMembers() {
        return listUserMembers;
    }

    public void setListUserMembers(List<String> listUserMembers) {
        this.listUserMembers = listUserMembers;
    }

    public String toJSONString(){
        ObjectMapper mapper = new ObjectMapper();
        try{
            return mapper.writeValueAsString(this);
        }catch (JsonProcessingException ex){
            Logger.getLogger(DigitalSign.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public DigitalSign fromJSONString(String json){
        ObjectMapper mapper = new ObjectMapper();
        DigitalSign digitalSign = null;
        try{
            digitalSign = mapper.readValue(json, DigitalSign.class);
        }catch (JsonProcessingException ex){
            Logger.getLogger(DigitalSign.class.getName()).log(Level.SEVERE, null, ex);
        }
        return digitalSign;
    }
}
