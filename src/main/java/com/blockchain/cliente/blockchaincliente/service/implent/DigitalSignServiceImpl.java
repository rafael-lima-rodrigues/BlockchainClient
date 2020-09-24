package com.blockchain.cliente.blockchaincliente.service.implent;

import com.blockchain.cliente.blockchaincliente.model.DigitalSign;
import com.blockchain.cliente.blockchaincliente.model.TransactionHistory;
import com.blockchain.cliente.blockchaincliente.model.UserIdentity;
import com.blockchain.cliente.blockchaincliente.model.query.RichQuery;
import com.blockchain.cliente.blockchaincliente.persistence.DigitalSignDAO;
import com.blockchain.cliente.blockchaincliente.persistence.UserDAO;
import com.blockchain.cliente.blockchaincliente.service.DigitalSignService;
import com.blockchain.cliente.blockchaincliente.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DigitalSignServiceImpl implements DigitalSignService {

    @Autowired
    DigitalSignDAO digitalSignDAO;

    @Override
    public DigitalSign getById(String id) {
        return digitalSignDAO.getbyId(id);
    }

    @Override
    public void save(DigitalSign digitalSign) {
        digitalSignDAO.save(digitalSign);

    }

    @Override
    public List<DigitalSign> query(RichQuery query) {
        return digitalSignDAO.query(query);
    }

    @Override
    public void delete(String id) {
        digitalSignDAO.delete(id);
    }

    @Override
    public List<DigitalSign> getAll() {
        return digitalSignDAO.getAll();
    }

    @Override
    public List<TransactionHistory> getHistory(String id) {
        return digitalSignDAO.getHistory(id);
    }
}
