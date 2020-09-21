package com.blockchain.cliente.blockchaincliente.presentation.rest;

import com.blockchain.cliente.blockchaincliente.model.UserIdentity;
import com.blockchain.cliente.blockchaincliente.model.query.RichQuery;
import com.blockchain.cliente.blockchaincliente.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/userIdentity")
public class UserIdentityController {

    @Autowired
    UserService userService;

    @RequestMapping("/get")
    UserIdentity getUser(@RequestParam String id) {
        return userService.getById(id);
    }

    @RequestMapping("/getAll")
    List<UserIdentity> getAllUser() {
        return userService.getAll();
    }

    @RequestMapping("/save")
    UserIdentity saveUser(
            @RequestParam(required = false) String id,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String dateOfBirth,
            @RequestParam(required = false) String cpf,
            //@RequestParam(required = false) String sex,
            @RequestParam(required = false) String civilState) {

        UserIdentity userIdentity = new UserIdentity();

        if (id != null) {
            userIdentity = userService.getById(id);
        }
        userIdentity.setName(name);
        userIdentity.setDateOfBirth(dateOfBirth);
        userIdentity.setCpf(cpf);
        //userIdentity.setSex(sex);
        userIdentity.setCivilState(civilState);
        userService.save(userIdentity);

        return userIdentity;
    }

    @RequestMapping("/delete")
    String deleteUser(@RequestParam String id) {
        userService.delete(id);
        return id;
    }

    @RequestMapping("/query")
    List<UserIdentity> queryUserIdentities(@RequestParam(required = false) String type){
        RichQuery query = new RichQuery();
        Map<String, Object> selector = new HashMap<>();
        if(type != null && !type.isEmpty()){
            selector.put("typeDoc","usersDoc");
            query.setSelector(selector);


        }
        return userService.query(query);
    }

}
