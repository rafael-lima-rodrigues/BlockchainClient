package com.blockchain.cliente.blockchaincliente.persistence;

import com.blockchain.cliente.blockchaincliente.model.DocumentsSigned;
import com.blockchain.cliente.blockchaincliente.model.TransactionHistory;
import com.blockchain.cliente.blockchaincliente.model.UserIdentity;
import com.blockchain.cliente.blockchaincliente.model.query.RichQuery;

import java.util.List;

public interface DigitalSignDAO {

    DocumentsSigned getbyId(String id, String userId);

    void save(DocumentsSigned documentsSigned);

    void update(String key, String userId, DocumentsSigned documentsSigned);

    List<DocumentsSigned> query (RichQuery query, String userId);

    void  delete (String idUser, String idDocuments);

    List<DocumentsSigned> getAll(String userId);

    List<TransactionHistory> getHistory (String id);
}
