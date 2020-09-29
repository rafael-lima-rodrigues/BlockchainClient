package com.blockchain.cliente.blockchaincliente.presentation.rest;

import com.blockchain.cliente.blockchaincliente.model.DocumentsSigned;
import com.blockchain.cliente.blockchaincliente.model.UserIdentity;
import com.blockchain.cliente.blockchaincliente.model.query.RichQuery;
import com.blockchain.cliente.blockchaincliente.service.DigitalSignService;
import com.blockchain.cliente.blockchaincliente.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/digitalSign")
public class DigitalSignController {

    @Autowired
    DigitalSignService digitalSignService;

    @RequestMapping("/get")
    DocumentsSigned getDS(@RequestParam String id,
                          @RequestParam String userId) {
        return digitalSignService.getById(id,userId);
    }

    @RequestMapping("/getAll")
    List<DocumentsSigned> getAllDS(@RequestParam String userId) {
        return digitalSignService.getAll(userId);
    }

    @RequestMapping("/save")
    DocumentsSigned saveDS(
            @RequestParam(required = false) String id,
            @RequestParam(required = false) String dados,
            @RequestParam(required = false) String userIdOwner,
           @RequestParam(required = false) List<String> listUserMembers
            ) {

        DocumentsSigned documentsSigned = new DocumentsSigned();

        /*if (id != null) {
            userIdentity = digitalSignService.getById(id);
        }*/
        documentsSigned.setDados(dados);
        documentsSigned.setUserIdOwner(userIdOwner);
        documentsSigned.setListUserMembers(listUserMembers);
        digitalSignService.save(documentsSigned);

        return documentsSigned;
    }

    @RequestMapping("/delete")
    String deleteDS(@RequestParam String dsId,
                    @RequestParam String userId) {
        digitalSignService.delete(userId, dsId);
        return dsId;
    }

    @RequestMapping("/query")
    List<DocumentsSigned> queryDS(@RequestParam(required = false) String type,
                                  @RequestParam String userId){
        RichQuery query = new RichQuery();
        Map<String, Object> selector = new HashMap<>();
        if(type != null && !type.isEmpty()){
            selector.put("typeDoc","DocsCreated");
            query.setSelector(selector);


        }
        return digitalSignService.query(query, userId);
    }

}
