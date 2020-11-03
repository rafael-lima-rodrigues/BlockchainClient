package com.blockchain.cliente.blockchaincliente.service.implent;

import com.blockchain.cliente.blockchaincliente.model.DocumentsSigned;
import com.blockchain.cliente.blockchaincliente.model.TransactionHistory;
import com.blockchain.cliente.blockchaincliente.model.query.RichQuery;
import com.blockchain.cliente.blockchaincliente.repository.DigitalSignDAO;
import com.blockchain.cliente.blockchaincliente.service.DigitalSignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DigitalSignServiceImpl implements DigitalSignService {

    @Autowired
    DigitalSignDAO digitalSignDAO;

    @Override
    public DocumentsSigned getById(String id) {
        return digitalSignDAO.getbyId(id);
    }

    @Override
    public void save(DocumentsSigned documentsSigned) {
        digitalSignDAO.save(documentsSigned);
    }

    @Override
    public void update(String key, DocumentsSigned documentsSigned) {
       digitalSignDAO.update(key, documentsSigned);
    }

    @Override
    public List<DocumentsSigned> query(RichQuery query) {
        return digitalSignDAO.query(query);
    }

    @Override
    public void delete(String id) {
        digitalSignDAO.delete(id);
    }

    @Override
    public List<DocumentsSigned> getAll() {
        return digitalSignDAO.getAll();
    }

    @Override
    public List<TransactionHistory> getHistory(String id) {
        return digitalSignDAO.getHistory(id);
    }
}
