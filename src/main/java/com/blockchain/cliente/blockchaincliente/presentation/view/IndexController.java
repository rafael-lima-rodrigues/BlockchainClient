package com.blockchain.cliente.blockchaincliente.presentation.view;

import com.blockchain.cliente.blockchaincliente.config.BlockchainNetworkAttributes;
import com.blockchain.cliente.blockchaincliente.model.DocumentsSigned;
import com.blockchain.cliente.blockchaincliente.model.TransactionHistory;
import com.blockchain.cliente.blockchaincliente.model.query.RichQuery;
import com.blockchain.cliente.blockchaincliente.service.DigitalSignService;
import com.blockchain.cliente.blockchaincliente.util.GeradorPDF;
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

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
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
    public String createDs(Model model) {

        model.addAttribute("documentsSigned", new DocumentsSigned());
        return "create";
    }

    @RequestMapping("/documents/save")
    public String createDs(@RequestParam MultipartFile dados, Model model) {
        DocumentsSigned documentsSigned = new DocumentsSigned();
        GeradorPDF geradorPDF = new GeradorPDF();

        try {
            ByteArrayInputStream stream = new ByteArrayInputStream(dados.getBytes());
            String myString = IOUtils.toString(stream, StandardCharsets.UTF_8);
            MessageDigest algh = MessageDigest.getInstance("SHA-256");
            byte message[] = algh.digest(myString.getBytes(StandardCharsets.UTF_8));
            String encoded = Base64.getEncoder().encodeToString(message);

            documentsSigned.setDados(encoded);
            documentsSigned.setId(UUID.randomUUID().toString());
            documentsSigned.setUserIdOwner(BlockchainNetworkAttributes.ORG1_NAME);
            digitalSignService.save(documentsSigned);
            geradorPDF.gerarArquivo(dados, documentsSigned.getId());

        } catch (IOException | NoSuchAlgorithmException | DocumentException e) {
            e.printStackTrace();
        }

        model.addAttribute("userIdOwner", documentsSigned.getUserIdOwner());
        return "redirect:/document/list";
    }

    @RequestMapping("/listDocs/edit")
    public String editDS(@RequestParam String id,
                         Model model) {
        model.addAttribute(
                "documentsSigned",
                digitalSignService.getById(id));
        return "edit";
    }

    @RequestMapping("/listDocs/assinar")
    public String assinarDoc(@RequestParam String id,
                             Model model) {
        model.addAttribute(
                "documentsSigned",
                digitalSignService.getById(id));
        return "assinar";
    }

    @RequestMapping("/document/search")
    public String searchDoc(Model model) {
        model.addAttribute("docs", new DocumentsSigned());
        return "search";
    }

    @RequestMapping("/document/list")
    public String getAllDS(Model model) {

        model.addAttribute("docs", digitalSignService.getAll());
        return "listDocs";
    }

    @RequestMapping("/listDocs/delete")
    public String deleteDS(@RequestParam String id) {
        digitalSignService.delete(id);
        return "redirect:/document/list";
    }

    @RequestMapping("/documents/update")
    public String saveDs(
            @RequestParam String id,
            @RequestParam String dados,
            @RequestParam String userIdOwner,
            @RequestParam Boolean sign, Model model
    ) {

        DocumentsSigned documentsSigned = digitalSignService.getById(id);

        documentsSigned.setDados(dados);
        documentsSigned.setUserIdOwner(userIdOwner);

        digitalSignService.update(id, documentsSigned);
        model.addAttribute("userIdOwner", userIdOwner);
        return "redirect:/listDocs";
    }

    @RequestMapping("/listDocs/saveSign")
    public String AssinarDocumento(@RequestParam String id) {

        ReaderKeys readerKeys = new ReaderKeys();
        DocumentsSigned documentsSigned = digitalSignService.getById(id);
        GeradorPDF geradorPDF = new GeradorPDF();

        try {
            PrivateKey privateKey = readerKeys.readPrivateKey();
            String publicKey = readerKeys.readPublicKey();
            System.out.println("Chave Publica: " + publicKey);
            String signature = readerKeys
                    .signatureGenerator(documentsSigned.getDados(), privateKey);
            System.out.println("Assinatura: " + signature);

            documentsSigned.addMemberSign(BlockchainNetworkAttributes.ORG1_NAME, true);
            digitalSignService.update(id, documentsSigned);
            geradorPDF.editarPdf(id, publicKey, signature);

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

    @RequestMapping("/document/validar")
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
                attributes.addFlashAttribute("success", "Assinatura Valida");
                return "redirect:/document/verify";
            }

        } catch (InvalidKeySpecException | NoSuchAlgorithmException | IOException
                | SignatureException | InvalidKeyException e) {
            e.printStackTrace();
        }
        attributes.addFlashAttribute("failed", "Assinatura Invalida");
        return "redirect:/document/verify";

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