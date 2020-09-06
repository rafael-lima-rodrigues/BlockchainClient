package com.blockchain.cliente.blockchaincliente.util;

import com.blockchain.cliente.blockchaincliente.config.BlockchainNetworkAttributes;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jdk.internal.org.objectweb.asm.TypeReference;
import org.hyperledger.fabric.sdk.*;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.ProposalException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
public class ChaincodeExecuter {

    private ChaincodeID chaincodeID;
    private final long waitTime = 2000;

    @Autowired
    @Qualifier("channel1")
    Channel channel;

    @Autowired
    HFClient hfClient;

    @Autowired
    ObjectMapper objectMapper;

    public String executeTransaction(boolean invoke, String func, String[] args) throws InvalidArgumentException, ProposalException {

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

        Logger.getLogger(ChaincodeExecuter.class.getName()).log(Level.INFO, String.format(
                "Sending transactionproposal to chaincode: function = " + func
                        + " args = " + String.join(", ", args)));
        Collection<ProposalResponse> proposalResponses = channel.sendTransactionProposal(transactionProposalRequest, channel.getPeers());

        String payload = "";

        for(ProposalResponse response : proposalResponses){

            if(response.getStatus() == ChaincodeResponse.Status.SUCCESS){
                payload = new String(response.getChaincodeActionResponsePayload());
                Logger.getLogger(ChaincodeExecuter.class.getName()).log(Level.INFO, String.format(
                        "[√] Got success response from peer " + response.getPeer().getName()
                                + " => Message : " + response.getMessage() + " Payload: %s ", payload));
                successful.add(response);
            } else {
                String status  = response.getStatus().toString();
                String msg = response.getMessage();
                Logger.getLogger(ChaincodeExecuter.class.getName()).log(Level.SEVERE, String.format(
                        "[×] Got failed response from peer " + response.getPeer().getName()
                                + " => Message : " + msg + " Status :" + status));
                failed.add(response);
            }
        }
        if(invoke){
            Logger.getLogger(ChaincodeExecuter.class.getName()).log(Level.INFO, "Send transaction to orderer...");

            try {
                CompletableFuture<BlockEvent.TransactionEvent> future = channel.sendTransaction(successful);
                BlockEvent.TransactionEvent transactionEvent = future.get();
                future.complete(transactionEvent);
                if (future.isDone()){
                    Logger.getLogger(ChaincodeExecuter.class.getName()).log(Level.INFO, "Orderer response: txid: " + transactionEvent.getTransactionID());
                    Logger.getLogger(ChaincodeExecuter.class.getName()).log(Level.INFO, "Orderer response: block number: " + transactionEvent.getBlockEvent().getBlockNumber());
                    return payload;
                }
            } catch (InterruptedException | ExecutionException ex) {
                Logger.getLogger(ChaincodeExecuter.class.getName()).log(Level.SEVERE, "Orderer exception happened: " + ex);
                return null;
            }
        }

        return  payload;
    }

    public String saveObject(String key, String json) {

        String result = "";
        String[] args = {key, json};
        try {
            result = executeTransaction(true, "createUserIdentity", args);
        } catch (InvalidArgumentException | ProposalException ex) {
            Logger.getLogger(ChaincodeExecuter.class.getName()).log(Level.SEVERE, null, ex);
        }

        return result;
    }

    public String saveUser(String key, UserIdentity userID) {

        String result = "";
        String[] args = {key, userID.toJSONString()};

        try {
            result = executeTransaction(true, "createUserIdentity", args);
        } catch (InvalidArgumentException | ProposalException ex) {
            Logger.getLogger(ChaincodeExecuter.class.getName()).log(Level.SEVERE, null, ex);
        }

        return result;
    }

    public String query(RichQuery query){
        String result = "";
        try {
            String[] args = {objectMapper.writeValueAsString(query)};
            result = executeTransaction(false, "query", args);
        }catch (InvalidArgumentException | ProposalException | UnsupportedEncodingException | InterruptedException | ExecutionException | TimeoutException | JsonProcessingException ex){
            Logger.getLogger(ChaincodeExecuter.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }

    public List<TransactionHistory> geHistories(String key) {
        String result = "";
        String [] args = {key};
        try{
            result = executeTransaction(false,"geHistories", args);
        }catch (InvalidArgumentException | ProposalException ex) {
            Logger.getLogger(ChaincodeExecuter.class.getName()).log(Level.SEVERE, null, ex);
        }
        List<TrasactionHistory> modifications = new ArrayList<>();
        TypeReference<List<TransactionHistory>> listType = new TypeReference<List<TrsnsactionHistory>>(){};

        try {
            modifications = objectMapper.readValue(result, listType);
        }catch (IOException ex){
            Logger.getLogger(ChaincodeExecuter.class.getName()).log(Level.SEVERE, null, ex);
        }
        return modifications;
    }
}
