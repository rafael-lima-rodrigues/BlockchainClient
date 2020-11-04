package com.blockchain.cliente.blockchaincliente.persistence.blockchain;

import com.blockchain.cliente.blockchaincliente.model.DigitalDocument;
import com.blockchain.cliente.blockchaincliente.model.TransactionHistory;
import com.blockchain.cliente.blockchaincliente.model.query.RichQuery;
import com.blockchain.cliente.blockchaincliente.persistence.DigitalSignDAO;
import com.blockchain.cliente.blockchaincliente.util.ChaincodeExecuter;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hyperledger.fabric.sdk.BlockInfo;
import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.ProposalException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

@Repository
public class DigitalSignDAOImp implements DigitalSignDAO {

    @Autowired
    @Qualifier("Executer")
    ChaincodeExecuter chaincodeExecuter;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    Channel channel;

    @Override
    public DigitalDocument getbyId(String id) {
        String key = String.valueOf(id);
        String json = chaincodeExecuter.getObjectByKey(key);
        DigitalDocument digitalDocument = null;
        if (json != null && !json.isEmpty()) {
            try {
                digitalDocument = objectMapper.readValue(json, DigitalDocument.class);
            } catch (IOException ex) {
                Logger.getLogger(DigitalSignDAOImp.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return digitalDocument;
    }

    @Override
    public void save(DigitalDocument digitalDocument) {
            chaincodeExecuter.save(digitalDocument.getId(), digitalDocument);

//        String key = UUID.randomUUID().toString();
    }

    @Override
    public void update(String key, DigitalDocument digitalDocument) {
        chaincodeExecuter.update(key, digitalDocument);

    }

 /*   public void update(DigitalDocument documentsSigned, String userId){
        chaincodeExecuterDS.update(documentsSigned.getId(), userId, documentsSigned);
    }*/

    @Override
    public List<DigitalDocument> query(RichQuery query) {
        List<DigitalDocument> digitalDocumentList = new ArrayList<>();
        TypeReference<List<DigitalDocument>> listType = new TypeReference<List<DigitalDocument>>() {
        };

        String json = chaincodeExecuter.query(query);
        try {
            digitalDocumentList = objectMapper.readValue(json, listType);
        } catch (IOException ex) {
            Logger.getLogger(DigitalSignDAOImp.class.getName()).log(Level.SEVERE, null, ex);
        }
        return digitalDocumentList;
    }

    @Override
    public void delete(String id) {

        chaincodeExecuter.deleteObject(id);
    }

    @Override
    public List<DigitalDocument> getAll() {
        List<DigitalDocument> digitalDocumentList = new ArrayList<>();
        TypeReference<List<DigitalDocument>> listType = new TypeReference<List<DigitalDocument>>() {
        };

        RichQuery query = new RichQuery();
        Map<String, Object> selector = new HashMap<>();
        selector.put("typeDoc", "DocsCreated");
        query.setSelector(selector);

        String json = chaincodeExecuter.query(query);
        try {
            digitalDocumentList = objectMapper.readValue(json, listType);
        } catch (IOException ex) {
            Logger.getLogger(DigitalSignDAOImp.class.getName()).log(Level.SEVERE, null, ex);
        }

        return digitalDocumentList;
    }

    @Override
    public List<TransactionHistory> getHistory(String id) {
        //  String key = String.valueOf(id);
        List<TransactionHistory> list = chaincodeExecuter.getHistory(id);
        list.forEach((history) -> {
            try {
                //String userString = objectMapper.writeValueAsString(history.getAsset());
                // UserIdentity userIdentity = objectMapper.readValue(userString, UserIdentity.class);
                DigitalDocument digitalDocument = objectMapper.readValue(history.getAsset().toString(), DigitalDocument.class);
                history.setAsset(digitalDocument);
                BlockInfo info = channel.queryBlockByTransactionID(history.getTransactionId());
                for (BlockInfo.EnvelopeInfo envelopeInfo : info.getEnvelopeInfos()) {
                    if (envelopeInfo.getTransactionID().equals(history.getTransactionId())) {
                        String creator = envelopeInfo.getCreator().getId();
                        String mspId = envelopeInfo.getCreator().getMspid();
                        history.setCreatorId(creator);
                        history.setCreatorMspId(mspId);

                    }
                }
            } catch (IOException ex) {
                Logger.getLogger(DigitalSignDAOImp.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InvalidArgumentException ex) {
                Logger.getLogger(DigitalSignDAOImp.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ProposalException ex) {
                Logger.getLogger(DigitalSignDAOImp.class.getName()).log(Level.SEVERE, null, ex);
            }

        });
        return list;
    }
}
