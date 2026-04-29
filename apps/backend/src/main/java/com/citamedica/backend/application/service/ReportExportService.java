package com.citamedica.backend.application.service;

import com.citamedica.backend.domain.model.analytics.ReportExportFormat;
import com.citamedica.backend.exception.domain.AnalyticsException;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
public class ReportExportService {

    public byte[] export(String title, String[] headers, List<String[]> dataRows, ReportExportFormat format) {
        return switch (format) {
            case PDF -> toPdf(title, headers, dataRows);
            case CSV -> toCsv(headers, dataRows);
            case XLSX -> toXlsx(title, headers, dataRows);
        };
    }

    private byte[] toPdf(String title, String[] headers, List<String[]> dataRows) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Document document = new Document();
            PdfWriter.getInstance(document, out);
            document.open();
            document.add(new Paragraph(title));
            PdfPTable table = new PdfPTable(headers.length);
            for (String h : headers) {
                table.addCell(h != null ? h : "");
            }
            for (String[] row : dataRows) {
                for (int i = 0; i < headers.length; i++) {
                    String cell = i < row.length && row[i] != null ? row[i] : "";
                    table.addCell(cell);
                }
            }
            document.add(table);
            document.close();
            return out.toByteArray();
        } catch (DocumentException e) {
            throw new AnalyticsException("Failed to build PDF report: " + e.getMessage());
        }
    }

    private byte[] toCsv(String[] headers, List<String[]> dataRows) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            try (OutputStreamWriter w = new OutputStreamWriter(out, StandardCharsets.UTF_8)) {
                w.append(String.join(",", escapeRow(headers))).append('\n');
                for (String[] row : dataRows) {
                    String[] padded = padRow(row, headers.length);
                    w.append(String.join(",", escapeRow(padded))).append('\n');
                }
            }
            return out.toByteArray();
        } catch (IOException e) {
            throw new AnalyticsException("Failed to build CSV report: " + e.getMessage());
        }
    }

    private String[] padRow(String[] row, int len) {
        String[] padded = new String[len];
        for (int i = 0; i < len; i++) {
            padded[i] = i < row.length ? row[i] : "";
        }
        return padded;
    }

    private String[] escapeRow(String[] row) {
        String[] escaped = new String[row.length];
        for (int i = 0; i < row.length; i++) {
            escaped[i] = escapeCsv(row[i] != null ? row[i] : "");
        }
        return escaped;
    }

    private String escapeCsv(String value) {
        if (value.contains(",") || value.contains("\"") || value.contains("\n") || value.contains("\r")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }

    private byte[] toXlsx(String title, String[] headers, List<String[]> dataRows) {
        try (XSSFWorkbook wb = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            String safeTitle = title.length() > 31 ? title.substring(0, 31) : title;
            Sheet sheet = wb.createSheet(safeTitle);
            Row hdr = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                hdr.createCell(i).setCellValue(headers[i] != null ? headers[i] : "");
            }
            int r = 1;
            for (String[] row : dataRows) {
                Row excelRow = sheet.createRow(r++);
                for (int i = 0; i < headers.length; i++) {
                    String cell = i < row.length && row[i] != null ? row[i] : "";
                    excelRow.createCell(i).setCellValue(cell);
                }
            }
            wb.write(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new AnalyticsException("Failed to build Excel report: " + e.getMessage());
        }
    }
}
