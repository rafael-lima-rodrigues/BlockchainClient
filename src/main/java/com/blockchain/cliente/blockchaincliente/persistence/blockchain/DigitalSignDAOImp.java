package com.blockchain.cliente.blockchaincliente.persistence.blockchain;

import com.blockchain.cliente.blockchaincliente.model.DocumentsSigned;
import com.blockchain.cliente.blockchaincliente.model.TransactionHistory;
import com.blockchain.cliente.blockchaincliente.model.UserIdentity;
import com.blockchain.cliente.blockchaincliente.model.query.RichQuery;
import com.blockchain.cliente.blockchaincliente.persistence.DigitalSignDAO;
import com.blockchain.cliente.blockchaincliente.persistence.UserDAO;
import com.blockchain.cliente.blockchaincliente.util.IChaincodeExecuter;
import com.blockchain.cliente.blockchaincliente.util.IChaincodeExecuterDS;
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
    @Qualifier("DsExecuter")
    IChaincodeExecuterDS chaincodeExecuterDS;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    Channel channel;

    @Override
    public DocumentsSigned getbyId(String id, String userId) {
        String key = String.valueOf(id);
        String json = chaincodeExecuterDS.getObjectByKey(key, userId);
        DocumentsSigned documentsSigned = null;
        if (json != null && !json.isEmpty()) {
            try {
                documentsSigned = objectMapper.readValue(json, DocumentsSigned.class);
            } catch (IOException ex) {
                Logger.getLogger(DigitalSignDAOImp.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return documentsSigned;
    }

    @Override
    public void save(DocumentsSigned documentsSigned) {

            documentsSigned.setId(UUID.randomUUID().toString());
            chaincodeExecuterDS.save(documentsSigned.getId(), documentsSigned);

//        String key = UUID.randomUUID().toString();
    }

    @Override
    public void update(String key, String userId, DocumentsSigned documentsSigned) {
        chaincodeExecuterDS.update(key, userId,documentsSigned);

    }

    public void update(DocumentsSigned documentsSigned, String userId){
        chaincodeExecuterDS.update(documentsSigned.getId(), userId, documentsSigned);
    }

    @Override
    public List<DocumentsSigned> query(RichQuery query, String userId) {
        List<DocumentsSigned> documentsSignedList = new ArrayList<>();
        TypeReference<List<DocumentsSigned>> listType = new TypeReference<List<DocumentsSigned>>() {
        };

        String json = chaincodeExecuterDS.query(query, userId);
        try {
            documentsSignedList = objectMapper.readValue(json, listType);
        } catch (IOException ex) {
            Logger.getLogger(DigitalSignDAOImp.class.getName()).log(Level.SEVERE, null, ex);
        }
        return documentsSignedList;
    }

    @Override
    public void delete(String idUser, String idDocument) {

        chaincodeExecuterDS.deleteObject(idUser,String.valueOf(idDocument));
    }

    @Override
    public List<DocumentsSigned> getAll(String userId) {
        List<DocumentsSigned> documentsSignedList = new ArrayList<>();
        TypeReference<List<DocumentsSigned>> listType = new TypeReference<List<DocumentsSigned>>() {
        };

        RichQuery query = new RichQuery();
        Map<String, Object> selector = new HashMap<>();
        selector.put("typeDoc", "DocsCreated");
        query.setSelector(selector);

        String json = chaincodeExecuterDS.query(query, userId);
        try {
            documentsSignedList = objectMapper.readValue(json, listType);
        } catch (IOException ex) {
            Logger.getLogger(DigitalSignDAOImp.class.getName()).log(Level.SEVERE, null, ex);
        }

        return documentsSignedList;
    }

    @Override
    public List<TransactionHistory> getHistory(String id) {
        //  String key = String.valueOf(id);
        List<TransactionHistory> list = chaincodeExecuterDS.getHistory(id);
        list.forEach((history) -> {
            try {
                //String userString = objectMapper.writeValueAsString(history.getAsset());
                // UserIdentity userIdentity = objectMapper.readValue(userString, UserIdentity.class);
                DocumentsSigned documentsSigned = objectMapper.readValue(history.getAsset().toString(), DocumentsSigned.class);
                history.setAsset(documentsSigned);
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
