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
public class DocumentsSigned implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String descricao;
    private String dados;
    private String userIdOwner;
    private final String typeDoc = "DocsCreated";

    private Map<String,Boolean> sign = new HashMap<>();

    public void addMemberSign(String member, boolean sign){
        this.sign.put(member, sign);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getDados() {
        return dados;
    }

    public void setDados(String dados) {
        this.dados = dados;
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

    //public String getTypeDoc() {
       // return typeDoc;
    //}

    public Map<String, Boolean> getSign() {
        return sign;
    }

    public void setSign(Map<String, Boolean> sign) {
        this.sign = sign;
    }

    public String toJSONString(){
        ObjectMapper mapper = new ObjectMapper();
        try{
            return mapper.writeValueAsString(this);
        }catch (JsonProcessingException ex){
            Logger.getLogger(DocumentsSigned.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public DocumentsSigned fromJSONString(String json){
        ObjectMapper mapper = new ObjectMapper();
        DocumentsSigned documentsSigned = null;
        try{
            documentsSigned = mapper.readValue(json, DocumentsSigned.class);
        }catch (JsonProcessingException ex){
            Logger.getLogger(DocumentsSigned.class.getName()).log(Level.SEVERE, null, ex);
        }
        return documentsSigned;
    }
}
