package com.blockchain.cliente.blockchaincliente.service;

import com.blockchain.cliente.blockchaincliente.model.DocumentsSigned;
import com.blockchain.cliente.blockchaincliente.model.TransactionHistory;
import com.blockchain.cliente.blockchaincliente.model.UserIdentity;
import com.blockchain.cliente.blockchaincliente.model.query.RichQuery;

import java.util.List;

public interface DigitalSignService {

    DocumentsSigned getById(String id, String userId);

    void save(DocumentsSigned documentsSigned);

    void update(String key, String userId, DocumentsSigned documentsSigned);

    List<DocumentsSigned> query(RichQuery query, String useId);

    void delete(String userId, String documentId);

    List<DocumentsSigned> getAll(String useId);

    List<TransactionHistory> getHistory(String id);
}
