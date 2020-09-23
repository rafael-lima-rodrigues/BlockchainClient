package com.blockchain.cliente.blockchaincliente.util;

import com.blockchain.cliente.blockchaincliente.model.TransactionHistory;
import com.blockchain.cliente.blockchaincliente.model.query.RichQuery;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.ProposalException;

import java.util.List;

public interface ChaincodeExecuter <T> {

    String save(String key, T userIdentity);
    String executeTransaction(boolean invoke, String func, String[] args) throws InvalidArgumentException, ProposalException;
    public String update(String key, T newUserID);
    public String getObjectByKey(String key);
    public String deleteObject(String key);
    public String query(RichQuery query);
    public List<TransactionHistory> getHistory(String key);


}
