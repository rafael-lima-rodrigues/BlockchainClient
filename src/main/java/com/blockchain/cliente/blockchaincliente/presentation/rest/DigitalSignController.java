package com.blockchain.cliente.blockchaincliente.presentation.rest;

import com.blockchain.cliente.blockchaincliente.model.DigitalSign;
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
    DigitalSign getDigitalSign(@RequestParam String id) {
        return digitalSignService.getById(id);
    }

    @RequestMapping("/getAll")
    List<DigitalSign> getAllDigitalSign() {
        return digitalSignService.getAll();
    }

    @RequestMapping("/save")
    DigitalSign saveUser(
            @RequestParam(required = false) String id,
            @RequestParam(required = false) String data,
            @RequestParam(required = false) String owner,
            @RequestParam(required = false) List<String> listMembers)
            //@RequestParam(required = false) String sex,
            //@RequestParam(required = false) String civilState)
            {

        DigitalSign digitalSign = new DigitalSign();

        if (id != null) {
            digitalSign = digitalSignService.getById(id);
        }
        digitalSign.setData(data);
        digitalSign.setUserIdOwner(owner);
        digitalSign.setListUserMembers(listMembers);
        //userIdentity.setSex(sex);
        //userIdentity.setCivilState(civilState);
        digitalSignService.save(digitalSign);

        return digitalSign;
    }

    @RequestMapping("/delete")
    String deleteDigitalSign(@RequestParam String id) {
        digitalSignService.delete(id);
        return id;
    }

    @RequestMapping("/query")
    List<DigitalSign> queryDigitalSign(@RequestParam(required = false) String type){
        RichQuery query = new RichQuery();
        Map<String, Object> selector = new HashMap<>();
        if(type != null && !type.isEmpty()){
            selector.put("typeDoc","usersDoc");
            query.setSelector(selector);


        }
        return digitalSignService.query(query);
    }

}
