package com.shhotie.controller;

import com.shhotie.pojo.Invoice;
import com.shhotie.service.PdfGenerationService;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/invoice")
public class InvoiceController {

    private final PdfGenerationService pdfService;

    public InvoiceController(PdfGenerationService pdfService) {
        this.pdfService = pdfService;
    }

    @PostMapping("/generate")
    public ResponseEntity<FileSystemResource> generateInvoice(@RequestBody Invoice invoice) {
        // Define the directory and file path
        String directoryPath = "invoices";
        String pdfFilePath = directoryPath + "/" + invoice.getBuyer() + "_invoice.pdf";

        // Create the directory if it does not exist
        File directory = new File(directoryPath);
        if (!directory.exists()) {
            directory.mkdirs();  // This will create the directory and any necessary parent directories
        }

        Map<String, Object> data = new HashMap<>();
        data.put("seller", invoice.getSeller());
        data.put("sellerGstin", invoice.getSellerGstin());
        data.put("sellerAddress", invoice.getSellerAddress());
        data.put("buyer", invoice.getBuyer());
        data.put("buyerGstin", invoice.getBuyerGstin());
        data.put("buyerAddress", invoice.getBuyerAddress());
        data.put("items", invoice.getItems());

        try {
            // Check if PDF exists, if not generate it
            File pdfFile = new File(pdfFilePath);
            if (!pdfFile.exists()) {
                pdfService.generatePdf(data, pdfFilePath);
            }
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + pdfFile.getName())
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(new FileSystemResource(pdfFile));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }
}
