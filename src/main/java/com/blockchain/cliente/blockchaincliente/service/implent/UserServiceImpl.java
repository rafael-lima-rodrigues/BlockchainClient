package com.blockchain.cliente.blockchaincliente.service.implent;

import com.blockchain.cliente.blockchaincliente.model.TransactionHistory;
import com.blockchain.cliente.blockchaincliente.model.UserIdentity;
import com.blockchain.cliente.blockchaincliente.model.query.RichQuery;
import com.blockchain.cliente.blockchaincliente.persistence.UserDAO;
import com.blockchain.cliente.blockchaincliente.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserDAO userDAO;

    @Override
    public UserIdentity getById(String id) {
        return userDAO.getbyId(id);
    }

    @Override
    public void save(UserIdentity userIdentity) {
        userDAO.save(userIdentity);

    }

    @Override
    public List<UserIdentity> query(RichQuery query) {
        return userDAO.query(query);
    }

    @Override
    public void delete(String id) {
        userDAO.delete(id);
    }

    @Override
    public List<UserIdentity> getAll() {
        return userDAO.getAll();
    }

    @Override
    public List<TransactionHistory> getHistory(String id) {
        return userDAO.getHistory(id);
    }
}
