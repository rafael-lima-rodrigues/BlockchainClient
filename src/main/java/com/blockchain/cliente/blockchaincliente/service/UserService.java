package com.blockchain.cliente.blockchaincliente.service;

import com.blockchain.cliente.blockchaincliente.model.TransactionHistory;
import com.blockchain.cliente.blockchaincliente.model.UserIdentity;
import com.blockchain.cliente.blockchaincliente.model.query.RichQuery;

import java.util.List;

public interface UserService {

    UserIdentity getById(String id);

    void save(UserIdentity userIdentity);

    List<UserIdentity> query(RichQuery query);

    void delete(String id);

    List<UserIdentity> getAll();

    List<TransactionHistory> getHistory(String id);
}
