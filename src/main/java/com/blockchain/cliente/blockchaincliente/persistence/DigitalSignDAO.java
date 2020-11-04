package com.blockchain.cliente.blockchaincliente.persistence;

import com.blockchain.cliente.blockchaincliente.model.DigitalDocument;
import com.blockchain.cliente.blockchaincliente.model.TransactionHistory;
import com.blockchain.cliente.blockchaincliente.model.query.RichQuery;

import java.util.List;

public interface DigitalSignDAO {

    DigitalDocument getbyId(String id);

    void save(DigitalDocument digitalDocument);

    void update(String key, DigitalDocument digitalDocument);

    List<DigitalDocument> query (RichQuery query);

    void  delete (String id);

    List<DigitalDocument> getAll();

    List<TransactionHistory> getHistory (String id);
}
