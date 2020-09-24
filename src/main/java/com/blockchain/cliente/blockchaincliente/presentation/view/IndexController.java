package com.blockchain.cliente.blockchaincliente.presentation.view;

import com.blockchain.cliente.blockchaincliente.model.DigitalSign;
import com.blockchain.cliente.blockchaincliente.model.TransactionHistory;
import com.blockchain.cliente.blockchaincliente.model.UserIdentity;
import com.blockchain.cliente.blockchaincliente.model.query.RichQuery;
import com.blockchain.cliente.blockchaincliente.service.DigitalSignService;
import com.blockchain.cliente.blockchaincliente.service.UserService;
import org.bouncycastle.math.raw.Mod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class IndexController {

    @Autowired
    UserService userService;

    @Autowired
    DigitalSignService digitalSignService;

    @RequestMapping("/")
    public String welcome(Model model) {
        return "index";
    }

    @RequestMapping("/products/add")
    public String createUser(Model model) {
        model.addAttribute("userIdentity", new UserIdentity());
        return "edit";
    }

    @RequestMapping("digitalsign/add")
    public  String createDigitalSign(Model model){
        model.addAttribute("digitalSign", new DigitalSign());
        return "editDigitalSign";
    }

    @RequestMapping("/products/edit")
    public String editUser(@RequestParam String id, Model model) {
        model.addAttribute("userIdentity", userService.getById(id));
        return "edit";
    }
    @RequestMapping("/products/search")
    public String searchUser(Model model) {
        model.addAttribute("user", new UserIdentity());
        return "search";
    }

    @RequestMapping("/products")
    public String getAllUser(Model model) {
        model.addAttribute("users", userService.getAll());
        return "products";
    }

    @RequestMapping("/products/delete")
    public String deleteFish(@RequestParam String id) {
        userService.delete(id);
        return "redirect:/products";
    }

    @RequestMapping("/userId/save")
    public String saveUser(
            @RequestParam String id,
            @RequestParam String name,
            @RequestParam String dateOfBirth,
            @RequestParam String cpf,
          //  @RequestParam String sex,
            @RequestParam String civilState
    ) {

        UserIdentity userIdentity = new UserIdentity();
       if(id == ""){
            userIdentity = new UserIdentity();
        } else {
            userIdentity = userService.getById(id);
        }
       // userIdentity.setId(id);
        userIdentity.setName(name);
        userIdentity.setDateOfBirth(dateOfBirth);
        userIdentity.setCpf(cpf);
       // userIdentity.setSex(sex);
        userIdentity.setCivilState(civilState);
        userService.save(userIdentity);

        return "redirect:/products";
    }

    @RequestMapping("/userId/query")
    public String queryFish(
            @RequestParam(required = false) String id,
            @RequestParam(required = false) String name  ,
            @RequestParam(required = false) String cpf, Model model) {

        RichQuery query = new RichQuery();
        Map<String, Object> selector = new HashMap<>();
        if (id != null && !id.isEmpty()) {
            selector.put("id", id);
        }

        if (name != null && !name.isEmpty()) {

            selector.put("name", name);
        }

        if (cpf != null && !cpf.isEmpty()) {
            selector.put("cpf", cpf);
        }

        query.setSelector(selector);

        model.addAttribute("users", userService.query(query));
        return "products";
    }
    @RequestMapping("/products/history")
    public String getHistory(@RequestParam String id, Model model) {
        List<TransactionHistory> history = userService.getHistory(id);
        model.addAttribute("history", history);
        return "history";
    }

}
