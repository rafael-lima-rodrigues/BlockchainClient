package com.blockchain.cliente.blockchaincliente.service;

import com.blockchain.cliente.blockchaincliente.model.DigitalDocument;
import com.blockchain.cliente.blockchaincliente.model.TransactionHistory;
import com.blockchain.cliente.blockchaincliente.model.query.RichQuery;

import java.util.List;

public interface DigitalSignService {

    DigitalDocument getById(String id);

    void save(DigitalDocument digitalDocument);

    void update(String key, DigitalDocument digitalDocument);

    List<DigitalDocument> query(RichQuery query);

    void delete(String id);

    List<DigitalDocument> getAll();

    List<TransactionHistory> getHistory(String id);
}
