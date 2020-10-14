package com.blockchain.cliente.blockchaincliente.util;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.*;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.Security;


public class GeradorPDF {

    private final String UPLOAD_DIR = "./uploads/";


    public void gerarArquivo(MultipartFile file, String documentId) throws IOException, DocumentException {
        PdfReader pdfReader = new PdfReader(file.getBytes());
        int n = pdfReader.getNumberOfPages();

        Rectangle psize = pdfReader.getPageSize(1);
        float width = psize.getWidth();
        float height = psize.getHeight();

        Document document = new Document(new Rectangle(width, height));
        PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(UPLOAD_DIR + "//" + documentId));

        document.open();

        PdfContentByte cb = writer.getDirectContent();
        int i = 0;
        int p = 0;

        while (i < n) {
            document.newPage();
            p++;
            i++;

            PdfImportedPage page1 = writer.getImportedPage(pdfReader, i);
            cb.addTemplate(page1, .0f, 0, 0, .0f, 60, 120);
            document.addAuthor("Rafael");


            //document.add(new Paragraph("ID: "+documentId));
            if (i < n) {
                i++;
                PdfImportedPage page2 = writer.getImportedPage(pdfReader, i);
                cb.addTemplate(page2, .0f, 0, 0, .0f, width / 2 + 60, 120);
            }
            BaseFont bf = BaseFont
                    .createFont(BaseFont.HELVETICA,
                            BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
            cb.beginText();
            cb.setFontAndSize(bf, 7);
            cb.showTextAligned(PdfContentByte.ALIGN_CENTER, "ID: " + documentId, width / 2, 100, 0);
            cb.showTextAligned(PdfContentByte
                    .ALIGN_RIGHT, "page " + p + " of " + ((n / 2) + (n % 2 >
                    0 ? 1 : 0)), width, 40, 0);
            cb.endText();
        }
        document.close();
    }

    public void editarPdf(String id, String publicKey, String signature, String hash) throws IOException, DocumentException {
        PdfReader reader = new PdfReader(UPLOAD_DIR + id);
        int n = reader.getNumberOfPages();

        Rectangle psize = reader.getPageSize(1);
        float width = psize.getWidth();
        float height = psize.getHeight();

        Document document = new Document(new Rectangle(width, height));
        PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(UPLOAD_DIR + id + "_signed.pdf"));
        document.open();
        PdfContentByte cb = writer.getDirectContent();
        int i = 0;
        int p = 0;
        while (i < n) {
            document.newPage();
            //document.add(new Paragraph("hello world"));
            p++;
            i++;
            PdfImportedPage page1 = writer.getImportedPage(reader, i);
            cb.addTemplate(page1, .0f, 0, 0, .0f, 60, 120);


            if (i < n) {
                i++;
                PdfImportedPage page2 = writer.getImportedPage(reader, i);
                cb.addTemplate(page2, .2f, 0, 0, .2f, width / 2 + 60, 120);
            }
            BaseFont bf = BaseFont
                    .createFont(BaseFont.HELVETICA,
                            BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
            cb.beginText();
            cb.setFontAndSize(bf, 7);
            //cb.showTextAligned(PdfContentByte.ALIGN_CENTER,"ID"+documentId,width/2,120,0);
            cb.showTextAligned(PdfContentByte.ALIGN_CENTER, "chave: " + publicKey, width / 2, 90, 0);
            cb.showTextAligned(PdfContentByte.ALIGN_CENTER, "Hash: " + publicKey, width / 2, 80, 0);
            cb.showTextAligned(PdfContentByte.ALIGN_CENTER, "Signature: " + signature, width / 2, 70, 0);
            /*cb.showTextAligned(PdfContentByte
                    .ALIGN_CENTER, "page " + p + " of " + ((n / 2) + (n % 2 >
                    0 ? 1 : 0)), width / 2, 40, 0);*/
            cb.endText();
        }
        document.close();
    }

    public void assinarPdf(String id) throws IOException, DocumentException {
        PdfReader pdfReader = new PdfReader(UPLOAD_DIR + id);
        FileOutputStream stream = new FileOutputStream(UPLOAD_DIR + id + "_signed.pdf");
        PdfStamper stamper = PdfStamper.createSignature(pdfReader, stream, '\0');
        PdfSignatureAppearance appearance = stamper.getSignatureAppearance();
        appearance.setReason("digital_signature");
        appearance.setLocation("Brasil");
        appearance.setVisibleSignature(new Rectangle(0, 300, 300, 109), 1, "sign");

    }
}

