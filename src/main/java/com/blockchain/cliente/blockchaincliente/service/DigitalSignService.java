package com.blockchain.cliente.blockchaincliente.service;

import com.blockchain.cliente.blockchaincliente.model.DigitalSign;
import com.blockchain.cliente.blockchaincliente.model.TransactionHistory;
import com.blockchain.cliente.blockchaincliente.model.UserIdentity;
import com.blockchain.cliente.blockchaincliente.model.query.RichQuery;

import java.util.List;

public interface DigitalSignService {

    DigitalSign getById(String id);

    void save(DigitalSign digitalSign);

    List<DigitalSign> query(RichQuery query);

    void delete(String id);

    List<DigitalSign> getAll();

    List<TransactionHistory> getHistory(String id);
}
