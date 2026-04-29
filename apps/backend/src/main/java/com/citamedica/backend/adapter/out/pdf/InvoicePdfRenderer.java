package com.citamedica.backend.adapter.out.pdf;

import com.citamedica.backend.domain.model.Clinic;
import com.citamedica.backend.domain.model.Invoice;
import com.citamedica.backend.domain.model.InvoiceLineItem;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;

@Component
public class InvoicePdfRenderer {

    public byte[] render(Invoice invoice, Clinic clinic) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Document document = new Document();
            PdfWriter.getInstance(document, out);
            document.open();
            document.add(new Paragraph(clinic != null ? clinic.getName() : "Clinic"));
            document.add(new Paragraph("Invoice: " + invoice.getInvoiceNumber()));
            document.add(new Paragraph("Patient: " + invoice.getPatient().getFullName()));
            document.add(new Paragraph("Amount: " + invoice.getAmount()));
            document.add(new Paragraph("Status: " + invoice.getStatus()));
            for (InvoiceLineItem line : invoice.getLineItems()) {
                document.add(new Paragraph(String.format(
                        "- %s x%d @ %s = %s",
                        line.getDescription(),
                        line.getQuantity(),
                        line.getUnitPrice(),
                        line.getAmount())));
            }
            document.close();
            return out.toByteArray();
        } catch (DocumentException e) {
            throw new IllegalStateException("Failed to render invoice PDF", e);
        }
    }
}
