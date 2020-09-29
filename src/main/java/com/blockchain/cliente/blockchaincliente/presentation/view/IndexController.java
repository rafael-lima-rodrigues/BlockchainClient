package com.blockchain.cliente.blockchaincliente.presentation.view;

import com.blockchain.cliente.blockchaincliente.model.DocumentsSigned;
import com.blockchain.cliente.blockchaincliente.model.TransactionHistory;
import com.blockchain.cliente.blockchaincliente.model.UserIdentity;
import com.blockchain.cliente.blockchaincliente.model.query.RichQuery;
import com.blockchain.cliente.blockchaincliente.service.DigitalSignService;
import com.blockchain.cliente.blockchaincliente.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

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

    @RequestMapping("/documents/add")
    public String createDs(Model model) {

        model.addAttribute("documentsSigned", new DocumentsSigned());
        return "create-ds";
    }

    @RequestMapping("/products/edit")
    public String editUser(@RequestParam String id, Model model) {
        model.addAttribute("userIdentity", userService.getById(id));
        return "edit";
    }

    @RequestMapping("/listDocs/edit")
    public String editDS(@RequestParam String id,
                         @RequestParam(required = false) String userIdOwner,
                         Model model) {
        userIdOwner = "b33429a4-aca9-4e98-b0bf-bf50d92c5032";
        model.addAttribute(
                "documentsSigned",
                digitalSignService.getById(id, userIdOwner));
        return "edit-ds";
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

    @RequestMapping("/listDocs")
    public String getAllDS(Model model, @RequestParam(required = false) String userIdOwner) {
        userIdOwner = "aafda989-46af-439e-9bd2-639263c99a23";
        model.addAttribute("docs", digitalSignService.getAll(userIdOwner));
        return "listDocs";
    }

    @RequestMapping("/products/delete")
    public String deleteFish(@RequestParam String id) {
        userService.delete(id);
        return "redirect:/products";
    }

    @RequestMapping("/listDocs/delete")
    public String deleteDS(@RequestParam String id) {
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
        if (id == "") {
            userIdentity = new UserIdentity();
        } else {
            userIdentity = userService.getById(id);
        }

        userIdentity.setName(name);
        userIdentity.setDateOfBirth(dateOfBirth);
        userIdentity.setCpf(cpf);

        userIdentity.setCivilState(civilState);
        userService.save(userIdentity);

        return "redirect:/products";
    }

    @RequestMapping("/documents/update")
    public String saveDs(
            @RequestParam String id,
            @RequestParam String dados,
            @RequestParam String userIdOwner,
            @RequestParam List<String> listUserMembers, Model model
    ) {

        DocumentsSigned documentsSigned = digitalSignService.getById(id, userIdOwner);

        documentsSigned.setDados(dados);
        documentsSigned.setUserIdOwner(userIdOwner);
        documentsSigned.setListUserMembers(listUserMembers);

        if (documentsSigned.getId().isEmpty()) {
            digitalSignService.save(documentsSigned);
        } else {
            digitalSignService.update(id, userIdOwner, documentsSigned);

        }

        model.addAttribute("userIdOwner", userIdOwner);
        return "redirect:/listDocs";
    }

    @RequestMapping("/documents/save")
    public String createDs(
            @RequestParam (required = false) String id,
            @RequestParam String dados,
            @RequestParam String userIdOwner, Model model
    ) {

        DocumentsSigned documentsSigned = new DocumentsSigned();

        // userIdentity.setId(id);
        documentsSigned.setDados(dados);
        documentsSigned.setUserIdOwner(userIdOwner);
        documentsSigned.addMember(userIdOwner);
        // userIdentity.setSex(sex);

        digitalSignService.save(documentsSigned);

        model.addAttribute("userIdOwner", userIdOwner);
        return "redirect:/listDocs";
    }

    @RequestMapping("/userId/query")
    public String queryFish(
            @RequestParam(required = false) String id,
            @RequestParam(required = false) String name,
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

    @RequestMapping("/documents/query")
    public String queryDS(
            @RequestParam(required = false) String id,
            @RequestParam(required = false) String userIdOwner, Model model) {

        RichQuery query = new RichQuery();
        Map<String, Object> selector = new HashMap<>();
        if (id != null && !id.isEmpty()) {
            selector.put("id", id);
        }
        if (userIdOwner != null && !userIdOwner.isEmpty()) {
            selector.put("userIdOwner", userIdOwner);
        }

        query.setSelector(selector);
        model.addAttribute("docs", digitalSignService.query(query, userIdOwner));
        return "listDocs";
    }

    @RequestMapping("/products/history")
    public String getHistory(@RequestParam String id, Model model) {
        List<TransactionHistory> history = userService.getHistory(id);
        model.addAttribute("history", history);
        return "history";
    }

    @RequestMapping("/listDocs/history")
    public String getHistoryDS(@RequestParam String id, Model model) {
        List<TransactionHistory> history = digitalSignService.getHistory(id);
        model.addAttribute("history", history);
        return "history-ds";
    }

}
