package com.blockchain.cliente.blockchaincliente.util;

import com.blockchain.cliente.blockchaincliente.model.DocumentsSigned;
import com.blockchain.cliente.blockchaincliente.model.TransactionHistory;
import com.blockchain.cliente.blockchaincliente.model.query.RichQuery;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.ProposalException;

import java.util.List;

public interface ChaincodeExecuter {

    String save(DocumentsSigned documentsSigned);
    String executeTransactionDS(boolean invoke, String func, String[] args) throws InvalidArgumentException, ProposalException;
    public String update(String key, DocumentsSigned  newDocument);
    public String getObjectByKey(String digitalSignId);
    public String deleteObject(String id);
    public String query(RichQuery query);
    public List<TransactionHistory> getHistory(String key);


}
