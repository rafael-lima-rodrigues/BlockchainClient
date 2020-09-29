package com.blockchain.cliente.blockchaincliente.service.implent;

import com.blockchain.cliente.blockchaincliente.model.DocumentsSigned;
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
    public DocumentsSigned getById(String id, String userId) {
        return digitalSignDAO.getbyId(id,userId);
    }

    @Override
    public void save(DocumentsSigned documentsSigned) {
        digitalSignDAO.save(documentsSigned);
    }

    @Override
    public void update(String key, String userId, DocumentsSigned documentsSigned) {
       digitalSignDAO.update(key, userId, documentsSigned);
    }

    @Override
    public List<DocumentsSigned> query(RichQuery query, String userId) {
        return digitalSignDAO.query(query, userId);
    }

    @Override
    public void delete(String userId, String documentId) {
        digitalSignDAO.delete(userId,documentId);
    }

    @Override
    public List<DocumentsSigned> getAll(String userId) {
        return digitalSignDAO.getAll(userId);
    }

    @Override
    public List<TransactionHistory> getHistory(String id) {
        return digitalSignDAO.getHistory(id);
    }
}
