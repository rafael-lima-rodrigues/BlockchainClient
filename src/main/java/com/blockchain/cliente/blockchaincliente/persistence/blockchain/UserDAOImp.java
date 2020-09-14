package com.blockchain.cliente.blockchaincliente.persistence.blockchain;

import com.blockchain.cliente.blockchaincliente.model.TransactionHistory;
import com.blockchain.cliente.blockchaincliente.model.UserIdentity;
import com.blockchain.cliente.blockchaincliente.model.query.RichQuery;
import com.blockchain.cliente.blockchaincliente.persistence.UserDAO;
import com.blockchain.cliente.blockchaincliente.util.ChaincodeExecuter;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hyperledger.fabric.sdk.BlockInfo;
import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.ProposalException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

@Repository
public class UserDAOImp implements UserDAO {

    @Autowired
    ChaincodeExecuter chaincodeExecuter;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    Channel channel;

    @Override
    public UserIdentity getbyId(String id) {
        String key = String.valueOf(id);
        String json = chaincodeExecuter.getObjectByKey(key);
        UserIdentity userIdentity = null;
        if(json != null && !json.isEmpty()){
            try {
                userIdentity = objectMapper.readValue(json, UserIdentity.class);
            }catch (IOException ex){
                Logger.getLogger(UserDAOImp.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return userIdentity;
    }

    @Override
    public void save(UserIdentity userIdentity) {

        String key = UUID.randomUUID().toString();
        chaincodeExecuter.saveUser(key, userIdentity);
    }

    @Override
    public List<UserIdentity> query(RichQuery query) {
        List<UserIdentity> identityList = new ArrayList<>();
        TypeReference<List<UserIdentity>> listType = new TypeReference<List<UserIdentity>>(){};

        String json = chaincodeExecuter.query(query);
        try {
            identityList = objectMapper.readValue(json, listType);
        } catch (IOException ex){
            Logger.getLogger(UserDAOImp.class.getName()).log(Level.SEVERE, null, ex);
        }
        return identityList;
    }

    @Override
    public void delete(String id) {

        chaincodeExecuter.deleteObject(String.valueOf(id));
    }

    @Override
    public List<UserIdentity> getAll() {
        List<UserIdentity> identityList = new ArrayList<>();
        TypeReference<List<UserIdentity>> listType = new TypeReference<List<UserIdentity>>() {
        };

        RichQuery query = new RichQuery();
        Map<String, Object> selector = new HashMap<>();
        selector.put("docType","userIdentity");
        query.setSelector(selector);

        String json = chaincodeExecuter.query(query);
        try {
            identityList = objectMapper.readValue(json, listType);
        }catch (IOException ex){
            Logger.getLogger(UserDAOImp.class.getName()).log(Level.SEVERE, null, ex);
        }

        return identityList;
    }

    @Override
    public List<TransactionHistory> getHistory(String id) {
        String key = String.valueOf(id);
        List<TransactionHistory> list = chaincodeExecuter.getHistory(key);
        list.forEach((history) -> {
            try{
                String userString = objectMapper.writeValueAsString(history.getAsset());
                UserIdentity userIdentity = objectMapper.readValue(userString, UserIdentity.class);
                history.setAsset(userIdentity);
                BlockInfo info = channel.queryBlockByTransactionID(history.getTransactionId());
                for(BlockInfo.EnvelopeInfo envelopeInfo : info.getEnvelopeInfos()){
                    if (envelopeInfo.getTransactionID().equals(history.getTransactionId())) {
                        String creator = envelopeInfo.getCreator().getId();
                        String mspId = envelopeInfo.getCreator().getMspid();
                        history.setCreatorId(creator);
                        history.setCreatorMspId(mspId);
                    }
                }
            } catch (IOException ex) {
                Logger.getLogger(UserDAOImp.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InvalidArgumentException ex) {
                Logger.getLogger(UserDAOImp.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ProposalException ex) {
                Logger.getLogger(UserDAOImp.class.getName()).log(Level.SEVERE, null, ex);
            }

        });
        return list;
    }
}
