package com.blockchain.cliente.blockchaincliente.util;

import com.blockchain.cliente.blockchaincliente.config.BlockchainNetworkAttributes;
import com.blockchain.cliente.blockchaincliente.model.DocumentsSigned;
import com.blockchain.cliente.blockchaincliente.model.TransactionHistory;
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
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
@Qualifier("Executer")
public class ChaincodeExecuterImp implements ChaincodeExecuter {

    private ChaincodeID chaincodeID;
    private final long waitTime = 5000;

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

        Logger.getLogger(ChaincodeExecuterImp.class.getName()).log(Level.INFO, String.format(
                "Sending transactionproposal to chaincode: function = " + func
                        + " args = " + String.join(", ", args)));
        Collection<ProposalResponse> proposalResponses = channel.sendTransactionProposal(transactionProposalRequest, channel.getPeers());

        String payload = "";

        for (ProposalResponse response : proposalResponses) {

            if (response.getStatus() == ChaincodeResponse.Status.SUCCESS) {
                payload = new String(response.getChaincodeActionResponsePayload());
                Logger.getLogger(ChaincodeExecuterImp.class.getName()).log(Level.INFO, String.format(
                        "[√] Got success response from peer " + response.getPeer().getName()
                                + " => Message : " + response.getMessage() + " Payload: %s ", payload));
                successful.add(response);
            } else {
                String status = response.getStatus().toString();
                String msg = response.getMessage();
                Logger.getLogger(ChaincodeExecuterImp.class.getName()).log(Level.SEVERE, String.format(
                        "[×] Got failed response from peer " + response.getPeer().getName()
                                + " => Message : " + msg + " Status :" + status));
                failed.add(response);
            }
        }
        if (invoke) {
            Logger.getLogger(ChaincodeExecuterImp.class.getName()).log(Level.INFO, "Send transaction to orderer...");

            try {
                CompletableFuture<BlockEvent.TransactionEvent> future = channel.sendTransaction(successful);
                BlockEvent.TransactionEvent transactionEvent = future.get();
                future.complete(transactionEvent);
                if (future.isDone()) {
                    Logger.getLogger(ChaincodeExecuterImp.class.getName()).log(Level.INFO, "Orderer response: txid: " + transactionEvent.getTransactionID());
                    Logger.getLogger(ChaincodeExecuterImp.class.getName()).log(Level.INFO, "Orderer response: block number: " + transactionEvent.getBlockEvent().getBlockNumber());
                    return payload;
                }
            } catch (InterruptedException | ExecutionException ex) {
                Logger.getLogger(ChaincodeExecuterImp.class.getName()).log(Level.SEVERE, "Orderer exception happened: " + ex);
                return null;
            }
        }

        return payload;
    }


    public String save(DocumentsSigned documentsSigned) {

        String result = "";
        if (documentsSigned.getId() == null || documentsSigned.getId().isEmpty()){
            documentsSigned.setId(UUID.randomUUID().toString());
        }
        String[] args = {documentsSigned.getId(), documentsSigned.toJSONString()};

        try {
            result = executeTransactionDS(true, "createDigitalSign", args);
        } catch (InvalidArgumentException | ProposalException ex) {
            Logger.getLogger(ChaincodeExecuterImp.class.getName()).log(Level.SEVERE, null, ex);
        }

        return result;
    }

    public String update(String key,  DocumentsSigned documentsSigned) {

        String result = "";
        String[] args = {key, documentsSigned.toJSONString()};

        try {
            result = executeTransactionDS(true, "updateDigitalSign", args);
        } catch (InvalidArgumentException | ProposalException ex) {
            Logger.getLogger(ChaincodeExecuterImp.class.getName()).log(Level.SEVERE, null, ex);
        }

        return result;
    }

    public String getObjectByKey(String key) {
        String result = "";
        String[] args = {key};
        try {
            result = executeTransactionDS(false, "readDigitalSign", args);
        } catch (InvalidArgumentException | ProposalException ex) {
            Logger.getLogger(ChaincodeExecuterImp.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }

    public String deleteObject(String key) {
        String result = "";
        String[] args = {key};
        try {
            result = executeTransactionDS(true, "deleteDigitalSign", args);
        } catch (InvalidArgumentException | ProposalException ex) {
            Logger.getLogger(ChaincodeExecuterImp.class.getName()).log(Level.SEVERE, null, ex);
        }

        return result;
    }

    public String query(RichQuery query) {
        String result = "";
        try {
            String[] args = {objectMapper.writeValueAsString(query)};
            result = executeTransactionDS(false, "queryDS", args);
        } catch (InvalidArgumentException | ProposalException | JsonProcessingException ex) {
            Logger.getLogger(ChaincodeExecuterImp.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }

    public List<TransactionHistory> getHistory(String key) {
        String result = "";
        String[] args = {key};
        try {
            result = executeTransactionDS(false, "getDSHistory", args);
        } catch (InvalidArgumentException | ProposalException ex) {
            Logger.getLogger(ChaincodeExecuterImp.class.getName()).log(Level.SEVERE, null, ex);
        }
        List<TransactionHistory> modifications = new ArrayList<>();
        TypeReference<List<TransactionHistory>> listType = new TypeReference<List<TransactionHistory>>() {
        };

        try {
            modifications = objectMapper.readValue(result, listType);
        } catch (IOException ex) {
            Logger.getLogger(ChaincodeExecuterImp.class.getName()).log(Level.SEVERE, null, ex);
        }
        return modifications;
    }
}
