package com.blockchain.cliente.blockchaincliente.util;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.security.*;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class ReaderKeys {
    private final String PUBLIC_KEY = "./Org1/admin/172c07f1fc45310f0be3e200820f039341527d1ce4298dc7885951c5811ba961-pub";
    private final String PRIVATE_KEY = "./Org1/admin/172c07f1fc45310f0be3e200820f039341527d1ce4298dc7885951c5811ba961-priv";

    public String readPublicKey() throws IOException, InvalidKeySpecException, NoSuchAlgorithmException {
        String key = new String(Files.readAllBytes(new File(PUBLIC_KEY).toPath()), Charset.defaultCharset());

        String publicKeyPEM = key
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replaceAll(System.lineSeparator(), "")
                .replace("-----END PUBLIC KEY-----", "");

        byte[] encoded = Base64.decodeBase64(publicKeyPEM);

        KeyFactory keyFactory = KeyFactory.getInstance("EC");
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(encoded);
        PublicKey publicKey = keyFactory.generatePublic(keySpec);
        String pub = java.util.Base64.getEncoder().encodeToString(publicKey.getEncoded());

        return pub;
    }

    public PrivateKey readPrivateKey() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException{
        String key = new String(Files.readAllBytes(new File(PRIVATE_KEY).toPath()), Charset.defaultCharset());

        String privateKeyPEM = key
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replaceAll(System.lineSeparator(), "")
                .replace("-----END PRIVATE KEY-----", "");

        byte[] encoded = Base64.decodeBase64(privateKeyPEM);

        KeyFactory keyFactory = KeyFactory.getInstance("EC");
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encoded);
        PrivateKey privateKey = keyFactory.generatePrivate(keySpec);
        //System.out.println(privateKey);
        return privateKey;
    }

    public String signatureGenerator(String message, PrivateKey privateKey) throws NoSuchAlgorithmException, IOException, InvalidKeyException, SignatureException {
        //String message = "Hello-world";

        Signature ecdsaSign = Signature.getInstance("SHA256withECDSA");
        ecdsaSign.initSign(privateKey);
        ecdsaSign.update(message.getBytes("UTF-8"));
        byte [] digitalSignature = ecdsaSign.sign();
        String signature = java.util.Base64.getEncoder().encodeToString(digitalSignature);
        return signature;
    }

    public boolean verifySign(String message, String publicKey, String signature) throws InvalidKeySpecException, NoSuchAlgorithmException, IOException, SignatureException, InvalidKeyException {
        Signature signatureVerify = Signature.getInstance("SHA256withECDSA");

        EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(java.util.Base64.getDecoder().decode(publicKey));

        KeyFactory keyFactory = KeyFactory.getInstance("EC");
        PublicKey pubKey = keyFactory.generatePublic(publicKeySpec);

        signatureVerify.initVerify(pubKey);
        signatureVerify.update(message.getBytes("UTF-8"));
        boolean result = signatureVerify.verify(java.util.Base64.getDecoder().decode(signature));
        return result;
    }


}
