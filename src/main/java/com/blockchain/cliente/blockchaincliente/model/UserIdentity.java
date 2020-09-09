package com.blockchain.cliente.blockchaincliente.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserIdentity implements Serializable {

    private static final long serialVersionUID = 1L;

    private String name;
    private String dateOfBirth;
    private String cpf;
    private String sex;
    private String civilState;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getCivilState() {
        return civilState;
    }

    public void setCivilState(String civilState) {
        this.civilState = civilState;
    }

    public String toJSONString(){
        ObjectMapper mapper = new ObjectMapper();
        try{
            return mapper.writeValueAsString(this);
        }catch (JsonProcessingException ex){
            Logger.getLogger(UserIdentity.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public UserIdentity fromJSONString(String json){
        ObjectMapper mapper = new ObjectMapper();
        UserIdentity userIdentity = null;
        try{
            userIdentity = mapper.readValue(json, UserIdentity.class);
        }catch (JsonProcessingException ex){
            Logger.getLogger(UserIdentity.class.getName()).log(Level.SEVERE, null, ex);
        }
        return userIdentity;
    }
}
