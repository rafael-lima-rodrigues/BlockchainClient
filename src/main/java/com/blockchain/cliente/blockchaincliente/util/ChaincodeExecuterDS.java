package com.blockchain.cliente.blockchaincliente.util;

import com.blockchain.cliente.blockchaincliente.config.BlockchainNetworkAttributes;
import com.blockchain.cliente.blockchaincliente.model.DocumentsSigned;
import com.blockchain.cliente.blockchaincliente.model.TransactionHistory;
import com.blockchain.cliente.blockchaincliente.model.UserIdentity;
import com.blockchain.cliente.blockchaincliente.model.query.RichQuery;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hyperledger.fabric.sdk.*;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.ProposalException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
@Qualifier("DsExecuter")
public class ChaincodeExecuterDS implements IChaincodeExecuterDS {

    private ChaincodeID chaincodeID;
    private final long waitTime = 2000;

    @Autowired
    @Qualifier("channel1")
    Channel channel;

    @Autowired
    HFClient hfClient;

    @Autowired
    ObjectMapper objectMapper;

    public String executeTransactionDS(boolean invoke, String func, String[] args) throws InvalidArgumentException, ProposalException {

        ChaincodeID.Builder chaincodeIDBuilder = ChaincodeID.newBuilder()
                .setName(BlockchainNetworkAttributes.CHAINCODE_1_NAME)
                .setVersion(BlockchainNetworkAttributes.CHAINCODE_1_VERSION);
        chaincodeID = chaincodeIDBuilder.build();

        TransactionProposalRequest transactionProposalRequest = hfClient.newTransactionProposalRequest();
        transactionProposalRequest.setChaincodeID(chaincodeID);
        transactionProposalRequest.setChaincodeLanguage(TransactionRequest.Type.JAVA);

        transactionProposalRequest.setFcn(func);
        transactionProposalRequest.setArgs(args);
        transactionProposalRequest.setProposalWaitTime(waitTime);

        List<ProposalResponse> successful = new LinkedList<>();
        List<ProposalResponse> failed = new LinkedList<>();

        Logger.getLogger(ChaincodeExecuterDS.class.getName()).log(Level.INFO, String.format(
                "Sending transactionproposal to chaincode: function = " + func
                        + " args = " + String.join(", ", args)));
        Collection<ProposalResponse> proposalResponses = channel.sendTransactionProposal(transactionProposalRequest, channel.getPeers());

        String payload = "";

        for (ProposalResponse response : proposalResponses) {

            if (response.getStatus() == ChaincodeResponse.Status.SUCCESS) {
                payload = new String(response.getChaincodeActionResponsePayload());
                Logger.getLogger(ChaincodeExecuterDS.class.getName()).log(Level.INFO, String.format(
                        "[√] Got success response from peer " + response.getPeer().getName()
                                + " => Message : " + response.getMessage() + " Payload: %s ", payload));
                successful.add(response);
            } else {
                String status = response.getStatus().toString();
                String msg = response.getMessage();
                Logger.getLogger(ChaincodeExecuterDS.class.getName()).log(Level.SEVERE, String.format(
                        "[×] Got failed response from peer " + response.getPeer().getName()
                                + " => Message : " + msg + " Status :" + status));
                failed.add(response);
            }
        }
        if (invoke) {
            Logger.getLogger(ChaincodeExecuterDS.class.getName()).log(Level.INFO, "Send transaction to orderer...");

            try {
                CompletableFuture<BlockEvent.TransactionEvent> future = channel.sendTransaction(successful);
                BlockEvent.TransactionEvent transactionEvent = future.get();
                future.complete(transactionEvent);
                if (future.isDone()) {
                    Logger.getLogger(ChaincodeExecuterDS.class.getName()).log(Level.INFO, "Orderer response: txid: " + transactionEvent.getTransactionID());
                    Logger.getLogger(ChaincodeExecuterDS.class.getName()).log(Level.INFO, "Orderer response: block number: " + transactionEvent.getBlockEvent().getBlockNumber());
                    return payload;
                }
            } catch (InterruptedException | ExecutionException ex) {
                Logger.getLogger(ChaincodeExecuterDS.class.getName()).log(Level.SEVERE, "Orderer exception happened: " + ex);
                return null;
            }
        }

        return payload;
    }

    public String saveObject(String key, String json) {

        String result = "";
        String[] args = {key, json};
        try {
            result = executeTransactionDS(true, "createDigitalSign", args);
        } catch (InvalidArgumentException | ProposalException ex) {
            Logger.getLogger(ChaincodeExecuterDS.class.getName()).log(Level.SEVERE, null, ex);
        }

        return result;
    }

    public String save(String key, DocumentsSigned documentsSigned) {

        String result = "";
        String[] args = {key, documentsSigned.toJSONString()};

        try {
            result = executeTransactionDS(true, "createDigitalSign", args);
        } catch (InvalidArgumentException | ProposalException ex) {
            Logger.getLogger(ChaincodeExecuterDS.class.getName()).log(Level.SEVERE, null, ex);
        }

        return result;
    }

    public String update(String key, String userId, DocumentsSigned documentsSigned) {

        String result = "";
        String[] args = {key, userId, documentsSigned.toJSONString()};

        try {
            result = executeTransactionDS(true, "updateDigitalSign", args);
        } catch (InvalidArgumentException | ProposalException ex) {
            Logger.getLogger(ChaincodeExecuterDS.class.getName()).log(Level.SEVERE, null, ex);
        }

        return result;
    }

    public String getObjectByKey(String key, String userId) {
        String result = "";
        String[] args = {key, userId};
        try {
            result = executeTransactionDS(false, "readDigitalSign", args);
        } catch (InvalidArgumentException | ProposalException ex) {
            Logger.getLogger(ChaincodeExecuterDS.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }

    public String deleteObject(String userId, String key) {
        String result = "";
        String[] args = {key};
        try {
            result = executeTransactionDS(true, "deleteDigitalSign", args);
        } catch (InvalidArgumentException | ProposalException ex) {
            Logger.getLogger(ChaincodeExecuterDS.class.getName()).log(Level.SEVERE, null, ex);
        }

        return result;
    }

    public String query(RichQuery query, String userId) {
        String result = "";
        try {
            String[] args = {objectMapper.writeValueAsString(query), userId};
            result = executeTransactionDS(false, "queryDS", args);
        } catch (InvalidArgumentException | ProposalException | JsonProcessingException ex) {
            Logger.getLogger(ChaincodeExecuterDS.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }

    public List<TransactionHistory> getHistory(String key) {
        String result = "";
        String[] args = {key};
        try {
            result = executeTransactionDS(false, "getDSHistory", args);
        } catch (InvalidArgumentException | ProposalException ex) {
            Logger.getLogger(ChaincodeExecuterDS.class.getName()).log(Level.SEVERE, null, ex);
        }
        List<TransactionHistory> modifications = new ArrayList<>();
        TypeReference<List<TransactionHistory>> listType = new TypeReference<List<TransactionHistory>>() {
        };

        try {
            modifications = objectMapper.readValue(result, listType);
        } catch (IOException ex) {
            Logger.getLogger(ChaincodeExecuterDS.class.getName()).log(Level.SEVERE, null, ex);
        }
        return modifications;
    }
}
