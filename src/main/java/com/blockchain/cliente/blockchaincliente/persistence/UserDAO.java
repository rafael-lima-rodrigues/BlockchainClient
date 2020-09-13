package com.blockchain.cliente.blockchaincliente.persistence;

import com.blockchain.cliente.blockchaincliente.model.TransactionHistory;
import com.blockchain.cliente.blockchaincliente.model.UserIdentity;
import com.blockchain.cliente.blockchaincliente.model.query.RichQuery;

import java.util.List;

public interface UserDAO {

    UserIdentity getbyId(String id);

    void save(UserIdentity userIdentity);

    List<UserIdentity> query (RichQuery query);

    void  delete (String id);

    List<UserIdentity> getAll();

    List<TransactionHistory> getHistory (String id);
}
