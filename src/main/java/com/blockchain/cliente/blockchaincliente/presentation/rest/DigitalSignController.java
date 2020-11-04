package com.blockchain.cliente.blockchaincliente.presentation.rest;

import com.blockchain.cliente.blockchaincliente.config.BlockchainNetworkAttributes;
import com.blockchain.cliente.blockchaincliente.model.DigitalDocument;
import com.blockchain.cliente.blockchaincliente.model.query.RichQuery;
import com.blockchain.cliente.blockchaincliente.service.DigitalSignService;
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
    DigitalDocument getDS(@RequestParam String id){
        return digitalSignService.getById(id);
    }

    @RequestMapping("/getAll")
    List<DigitalDocument> getAllDS(@RequestParam String userId) {
        return digitalSignService.getAll();
    }

    @RequestMapping("/save")
    DigitalDocument saveDS(
            @RequestParam(required = false) String id,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String data,
            @RequestParam(required = false) String userIdOwner
            ) {

        DigitalDocument digitalDocument = new DigitalDocument();

        digitalDocument.setDescription(description);
        digitalDocument.setDescription(data);
        digitalDocument.setUserIdOwner(userIdOwner);
        digitalDocument.addMemberSignature(BlockchainNetworkAttributes.ORG1_NAME, true);
        digitalSignService.save(digitalDocument);

        return digitalDocument;
    }

    @RequestMapping("/delete")
    String deleteDS(@RequestParam String dsId) {
        digitalSignService.delete(dsId);
        return dsId;
    }

    @RequestMapping("/query")
    List<DigitalDocument> queryDS(@RequestParam(required = false) String type,
                                  @RequestParam String userId){
        RichQuery query = new RichQuery();
        Map<String, Object> selector = new HashMap<>();
        if(type != null && !type.isEmpty()){
            selector.put("typeDoc","DocsCreated");
            query.setSelector(selector);


        }
        return digitalSignService.query(query);
    }

}
