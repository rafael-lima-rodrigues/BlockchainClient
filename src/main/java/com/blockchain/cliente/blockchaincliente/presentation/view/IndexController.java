package com.blockchain.cliente.blockchaincliente.presentation.view;

import com.blockchain.cliente.blockchaincliente.model.UserIdentity;
import com.blockchain.cliente.blockchaincliente.service.UserService;
import org.bouncycastle.math.raw.Mod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class IndexController {

    @Autowired
    UserService userService;

    @RequestMapping("/")
    public String welcome(Model model) {
        return "index";
    }

    @RequestMapping("/userId/add")
    public String createUser(Model model) {
        model.addAttribute("userIdentity", new UserIdentity());
        return "edit";
    }

    @RequestMapping("/userId/edit")
    public String editUser(@RequestParam String id, Model model) {
        model.addAttribute("userIdentity", userService.getById(id));
        return "edit";
    }
    @RequestMapping("/userId/search")
    public String searchUser(Model model) {
        model.addAttribute("userIdentity", new UserIdentity());
        return "search";
    }

    @RequestMapping("/userId")
    public String getAllFish(Model model) {
        model.addAttribute("users", userService.getAll());
        return "userId";
    }

    @RequestMapping("/userId/delete")
    public String deleteFish(@RequestParam String id) {
        userService.delete(id);
        return "redirect:/userId";
    }

    @RequestMapping("/userId/save")
    public String saveUser(
            @RequestParam String id,
            @RequestParam String name,
            @RequestParam String dateOfBirth,
            @RequestParam String cpf,
            @RequestParam String sex,
            @RequestParam String civilState
    ) {

        UserIdentity userIdentity;
        if(id == null){
            userIdentity = new UserIdentity();
        } else {
            userIdentity = userService.getById(id);
        }
        userIdentity.setName(name);
        userIdentity.setDateOfBirth(dateOfBirth);
        userIdentity.setCpf(cpf);
        userIdentity.setSex(sex);
        userIdentity.setCivilState(civilState);
        userService.save(userIdentity);

        return "redirect:/userId";
    }
}
