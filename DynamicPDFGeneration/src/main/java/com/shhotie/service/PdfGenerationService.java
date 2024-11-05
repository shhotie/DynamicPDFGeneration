package com.shhotie.service;

import com.lowagie.text.Document;
import com.lowagie.text.html.simpleparser.HTMLWorker;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.util.Map;

@Service
public class PdfGenerationService {

    private final TemplateEngine templateEngine;

    public PdfGenerationService(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    // using flying-saucer
    public String generatePdf(Map<String, Object> data, String pdfFilePath) throws Exception {
        Context context = new Context();
        context.setVariables(data);

        // Generate HTML from Thymeleaf template
        String htmlContent = templateEngine.process("invoice", context);

        // Generate PDF using Flying Saucer
        try (OutputStream os = new FileOutputStream(pdfFilePath)) {
            ITextRenderer renderer = new ITextRenderer();
            renderer.setDocumentFromString(htmlContent);
            renderer.layout();
            renderer.createPDF(os);
        }
        return pdfFilePath;
    }

    public String generatePdfusingOpenPDF(Map<String, Object> data, String pdfFilePath) throws Exception {
        Context context = new Context();
        context.setVariables(data);

        String htmlContent = templateEngine.process("invoice", context);

        try (FileOutputStream fos = new FileOutputStream(pdfFilePath)) {
            Document document = new Document();
            PdfWriter.getInstance(document, fos);
            document.open();
            HTMLWorker htmlWorker = new HTMLWorker(document);
            htmlWorker.parse(new StringReader(htmlContent));
            document.close();
        }
        return pdfFilePath;
    }
}

