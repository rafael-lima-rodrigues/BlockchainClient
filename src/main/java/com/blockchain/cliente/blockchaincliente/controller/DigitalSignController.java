package com.blockchain.cliente.blockchaincliente.controller;

import com.blockchain.cliente.blockchaincliente.config.BlockchainNetworkAttributes;
import com.blockchain.cliente.blockchaincliente.model.DocumentsSigned;
import com.blockchain.cliente.blockchaincliente.model.query.RichQuery;
import com.blockchain.cliente.blockchaincliente.service.DigitalSignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@CrossOrigin(origins = "http://localhost:8081")
@RestController
@RequestMapping("/api")
public class DigitalSignController {

    @Autowired
    DigitalSignService digitalSignService;

    @GetMapping("/get/{id}")
    @ResponseBody
    ResponseEntity<DocumentsSigned> get(@PathVariable("id") String id) {
        Optional<DocumentsSigned> documentsData = Optional.ofNullable(digitalSignService.getById(id));

        if (documentsData.isPresent()){
            return new ResponseEntity<>(documentsData.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("getAll")
    ResponseEntity<List<DocumentsSigned>> getAll() {
        try {
            List<DocumentsSigned> documentsSigneds = new ArrayList<>();
            digitalSignService.getAll().forEach(documentsSigneds::add);
            if (documentsSigneds.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(documentsSigneds, HttpStatus.OK);
        } catch (Exception e) {

            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/save")
    ResponseEntity<DocumentsSigned> save(@RequestBody DocumentsSigned documentsSigned) {

        try {
            digitalSignService.save(documentsSigned);
            return new ResponseEntity<>(documentsSigned, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @PutMapping("/update/{id}")
    ResponseEntity<DocumentsSigned> update(@PathVariable("id") String id,
                                           @RequestBody DocumentsSigned documentsSigned){
        Optional<DocumentsSigned> documentData = Optional.ofNullable(digitalSignService.getById(id));

        if (documentData.isPresent()){

            DocumentsSigned _documentsSigned = documentData.get();
            _documentsSigned.setDescricao(documentsSigned.getDescricao());
            _documentsSigned.setDados(documentsSigned.getDados());
            _documentsSigned.setUserIdOwner(documentsSigned.getUserIdOwner());
            _documentsSigned.setSign(documentsSigned.getSign());
            digitalSignService.update(id,_documentsSigned);
            return new ResponseEntity<>(_documentsSigned, HttpStatus.OK);
        }else{
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

    }

    @DeleteMapping("/delete/{id}")
    ResponseEntity<DocumentsSigned> delete(@PathVariable ("id") String dsId) {
        try {
            digitalSignService.delete(dsId);
            return new ResponseEntity<>(HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

    }

    @GetMapping("/query")
    List<DocumentsSigned> queryDS(@RequestParam(required = false) String type,
                                  @RequestParam String userId) {
        RichQuery query = new RichQuery();
        Map<String, Object> selector = new HashMap<>();
        if (type != null && !type.isEmpty()) {
            selector.put("typeDoc", "DocsCreated");
            query.setSelector(selector);


        }
        return digitalSignService.query(query);
    }

}
