package com.blockchain.cliente.blockchaincliente.presentation.view;

import com.blockchain.cliente.blockchaincliente.config.BlockchainNetworkAttributes;
import com.blockchain.cliente.blockchaincliente.model.DigitalDocument;
import com.blockchain.cliente.blockchaincliente.model.TransactionHistory;
import com.blockchain.cliente.blockchaincliente.model.query.RichQuery;
import com.blockchain.cliente.blockchaincliente.service.DigitalSignService;
import com.blockchain.cliente.blockchaincliente.util.PDFGenerator;
import com.blockchain.cliente.blockchaincliente.util.ReaderKeys;
import com.itextpdf.text.DocumentException;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.util.*;

@Controller
public class IndexController {

    @Autowired
    DigitalSignService digitalSignService;

    @RequestMapping("/")
    public String welcome(Model model) {
        return "index";
    }

    @RequestMapping("/document/add")
    public String create(Model model) {

        model.addAttribute("digitalDocument", new DigitalDocument());
        return "create";
    }

    @RequestMapping("/documents/save")
    public String create(@RequestParam MultipartFile data, @RequestParam String description, Model model) {
        DigitalDocument digitalDocument = new DigitalDocument();
        PDFGenerator PDFGenerator = new PDFGenerator();

        try {
            ByteArrayInputStream stream = new ByteArrayInputStream(data.getBytes());
            String myString = IOUtils.toString(stream, StandardCharsets.UTF_8);
            MessageDigest algh = MessageDigest.getInstance("SHA-256");
            byte message[] = algh.digest(myString.getBytes(StandardCharsets.UTF_8));
            String encoded = Base64.getEncoder().encodeToString(message);

            digitalDocument.setData(encoded);
            digitalDocument.setDescription(description);
            digitalDocument.setId(UUID.randomUUID().toString());
            digitalDocument.setUserIdOwner(BlockchainNetworkAttributes.ORG1_NAME);
            digitalSignService.save(digitalDocument);
            PDFGenerator.gerarArquivo(data, digitalDocument.getId());

        } catch (IOException | NoSuchAlgorithmException | DocumentException e) {
            e.printStackTrace();
        }

        model.addAttribute("userIdOwner", digitalDocument.getUserIdOwner());
        return "redirect:/document/list";
    }

    @RequestMapping("/document/get")
    public String edit(@RequestParam String id,
                         Model model) {
        model.addAttribute(
                "digitalDocument",
                digitalSignService.getById(id));
        return "view";
    }

    @RequestMapping("/listDocs/sign")
    public String signDoc(@RequestParam String id,
                             Model model) {
        model.addAttribute(
                "digitalDocument",
                digitalSignService.getById(id));
        return "sign";
    }

    @RequestMapping("/document/search")
    public String searchDoc(Model model) {
        model.addAttribute("docs", new DigitalDocument());
        return "search";
    }

    @RequestMapping("/document/list")
    public String getAll(Model model) {

        model.addAttribute("docs", digitalSignService.getAll());
        return "listDocs";
    }

    @RequestMapping("/listDocs/delete")
    public String delete(@RequestParam String id) {
        digitalSignService.delete(id);
        return "redirect:/document/list";
    }

    @RequestMapping("/documents/update")
    public String save(
            @RequestParam String id,
            @RequestParam String data,
            @RequestParam String description,
            @RequestParam String userIdOwner,
            Model model
    ) {

        DigitalDocument digitalDocument = digitalSignService.getById(id);

        digitalDocument.setDescription(description);
        digitalDocument.setData(data);
        digitalDocument.setUserIdOwner(userIdOwner);

        digitalSignService.update(id, digitalDocument);
        model.addAttribute("userIdOwner", userIdOwner);
        return "redirect:/listDocs";
    }

    @RequestMapping("/listDocs/saveSignature")
    public String SignDocument(@RequestParam String id) {

        ReaderKeys readerKeys = new ReaderKeys();
        DigitalDocument digitalDocument = digitalSignService.getById(id);
        PDFGenerator PDFGenerator = new PDFGenerator();
        System.out.println("=====Assinando=====");
        try {
            PrivateKey privateKey = readerKeys.readPrivateKey();
            String publicKey = readerKeys.readPublicKey();
            System.out.println("Public key: " + publicKey);
            String signature = readerKeys
                    .signatureGenerator(digitalDocument.getData(), privateKey);
            System.out.println("signature: " + signature);

            digitalDocument.addMemberSignature(BlockchainNetworkAttributes.ORG1_NAME, true);
            digitalSignService.update(id, digitalDocument);
            PDFGenerator.editarPdf(id, publicKey, signature, digitalDocument.getData());

        } catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException |
                InvalidKeyException | SignatureException |
                DocumentException e) {
        }
        return "redirect:/document/list";
    }

    @RequestMapping("/document/verify")
    public String verify() {
        return "verify";
    }

    @RequestMapping("/document/validate")
    public String verificarDocumento(@RequestParam(value = "publicKey") String publicKey,
                                     @RequestParam(value = "hashDoc") String hashDoc,
                                     @RequestParam(value = "signature") String signature,
                                     RedirectAttributes attributes) {

        ReaderKeys readerKeys = new ReaderKeys();
        boolean verify;
        try {
            verify = readerKeys.verifySign(hashDoc, publicKey, signature);
            System.out.println(verify);
            if (verify) {
                attributes.addFlashAttribute("success", "Valid signature");
                return "redirect:/document/verify";
            }

        } catch (InvalidKeySpecException | NoSuchAlgorithmException | IOException
                | SignatureException | InvalidKeyException e) {
            e.printStackTrace();
        }
        attributes.addFlashAttribute("failed", "Valid signature");
        return "redirect:/document/verify";

    }

    @RequestMapping("/documents/query")
    public String query(
            @RequestParam(required = false) String id,
            @RequestParam(required = false) String description, Model model) {

        RichQuery query = new RichQuery();
        Map<String, Object> selector = new HashMap<>();
        if (id != null && !id.isEmpty()) {
            selector.put("id", id);
        }
        if (description != null && !description.isEmpty()) {
            selector.put("description", description);
        }

        query.setSelector(selector);
        model.addAttribute("docs", digitalSignService.query(query));
        return "listDocs";
    }

    @RequestMapping("/listDocs/history")
    public String getHistory(@RequestParam String id, Model model) {
        List<TransactionHistory> history = digitalSignService.getHistory(id);
        model.addAttribute("history", history);
        return "history";
    }
}