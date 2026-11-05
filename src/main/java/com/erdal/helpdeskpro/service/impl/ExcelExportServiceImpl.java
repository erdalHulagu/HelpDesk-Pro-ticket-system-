
package com.erdal.helpdeskpro.service.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.erdal.helpdeskpro.service.ExcelExportService;

public class ExcelExportServiceImpl implements  ExcelExportService {

	@Override
    public byte[] exportToExcel(
            String sheetName,
            List<String> headers,
            List<List<String>> data
    ) throws IOException {

        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet(sheetName);

            // Header style
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();

            headerFont.setBold(true);
            headerStyle.setFont(headerFont);

            // Create header row
            Row headerRow = sheet.createRow(0);

            for (int i = 0; i < headers.size(); i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers.get(i));
                cell.setCellStyle(headerStyle);
            }

            // Create data rows
            for (int i = 0; i < data.size(); i++) {
                Row row = sheet.createRow(i + 1);

                List<String> rowData = data.get(i);

                for (int j = 0; j < rowData.size(); j++) {
                    row.createCell(j).setCellValue(
                            rowData.get(j) != null ? rowData.get(j) : ""
                    );
                }
            }

            // Automatically adjust column widths
            for (int i = 0; i < headers.size(); i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(outputStream);

            return outputStream.toByteArray();
        }
    }
}
