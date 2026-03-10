package com.payment.bankingui.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;

import org.springframework.stereotype.Service;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.payment.bankingui.model.Transaction;

@Service
public class PdfService {

    public ByteArrayInputStream generateStatement(List<Transaction> transactions) {

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {

            PdfWriter writer = new PdfWriter(out);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            // Title
            document.add(new Paragraph("Bank Transaction Statement")
                    .setBold()
                    .setFontSize(18));

            document.add(new Paragraph(" "));

            // Table with 5 columns
            Table table = new Table(5);

            table.addHeaderCell(new Cell().add(new Paragraph("ID")));
            table.addHeaderCell(new Cell().add(new Paragraph("From Account")));
            table.addHeaderCell(new Cell().add(new Paragraph("To Account")));
            table.addHeaderCell(new Cell().add(new Paragraph("Amount")));
            table.addHeaderCell(new Cell().add(new Paragraph("Date")));

            // Add rows
            for (Transaction t : transactions) {

                table.addCell(String.valueOf(t.getId()));

                table.addCell(
                        t.getFromAccount() != null ?
                        String.valueOf(t.getFromAccount()) : "-"
                );

                table.addCell(
                        t.getToAccount() != null ?
                        String.valueOf(t.getToAccount()) : "-"
                );

                table.addCell("₹ " + t.getAmount());

                table.addCell(
                        t.getTimestamp() != null ?
                        t.getTimestamp().toString() : "-"
                );
            }

            document.add(table);

            document.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ByteArrayInputStream(out.toByteArray());
    }

}