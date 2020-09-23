package com.blockchain.cliente.blockchaincliente.persistence.blockchain;

import com.blockchain.cliente.blockchaincliente.model.DigitalSign;
import com.blockchain.cliente.blockchaincliente.model.TransactionHistory;
import com.blockchain.cliente.blockchaincliente.model.query.RichQuery;
import com.blockchain.cliente.blockchaincliente.persistence.DigitalSignDAO;
import com.blockchain.cliente.blockchaincliente.util.ChaincodeExecuter;
import com.blockchain.cliente.blockchaincliente.util.ChaincodeExecuterUser;
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
    @Qualifier("digtalSignExecuter")
    ChaincodeExecuter chaincodeExecuter;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    Channel channel;

    @Override
    public DigitalSign getbyId(String id) {
        String key = String.valueOf(id);
        String json = chaincodeExecuter.getObjectByKey(key);
        DigitalSign digitalSign = null;
        if (json != null && !json.isEmpty()) {
            try {
                digitalSign = objectMapper.readValue(json, DigitalSign.class);
            } catch (IOException ex) {
                Logger.getLogger(DigitalSignDAOImp.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return digitalSign;
    }

    @Override
    public void save(DigitalSign digitalSign) {

            digitalSign.setId(UUID.fromString(digitalSign.getUserIdOwner()).toString());
            chaincodeExecuter.save(digitalSign.getId(), digitalSign);


//        String key = UUID.randomUUID().toString();
    }

    @Override
    public List<DigitalSign> query(RichQuery query) {
        List<DigitalSign> digitalSigns = new ArrayList<>();
        TypeReference<List<DigitalSign>> listType = new TypeReference<List<DigitalSign>>() {
        };

        String json = chaincodeExecuter.query(query);
        try {
            digitalSigns = objectMapper.readValue(json, listType);
        } catch (IOException ex) {
            Logger.getLogger(DigitalSignDAOImp.class.getName()).log(Level.SEVERE, null, ex);
        }
        return digitalSigns;
    }

    @Override
    public void delete(String id) {

        chaincodeExecuter.deleteObject(String.valueOf(id));
    }

    @Override
    public List<DigitalSign> getAll() {
        List<DigitalSign> identityList = new ArrayList<>();
        TypeReference<List<DigitalSign>> listType = new TypeReference<List<DigitalSign>>() {
        };

        RichQuery query = new RichQuery();
        Map<String, Object> selector = new HashMap<>();
        selector.put("typeDoc", "DocSigned");
        query.setSelector(selector);

        String json = chaincodeExecuter.query(query);
        try {
            identityList = objectMapper.readValue(json, listType);
        } catch (IOException ex) {
            Logger.getLogger(DigitalSignDAOImp.class.getName()).log(Level.SEVERE, null, ex);
        }

        return identityList;
    }

    @Override
    public List<TransactionHistory> getHistory(String id) {
        //  String key = String.valueOf(id);
        List<TransactionHistory> list = chaincodeExecuter.getHistory(id);
        list.forEach((history) -> {
            try {
                //String userString = objectMapper.writeValueAsString(history.getAsset());
                // digitalSign digitalSign = objectMapper.readValue(userString, digitalSign.class);
                DigitalSign digitalSign = objectMapper.readValue(history.getAsset().toString(), DigitalSign.class);
                history.setAsset(digitalSign);
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
