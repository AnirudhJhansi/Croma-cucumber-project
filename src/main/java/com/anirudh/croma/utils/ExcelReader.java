package com.anirudh.croma.utils;

import org.apache.logging.log4j.LogManager;

import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.*;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ExcelReader {
    private static final Logger log = LogManager.getLogger(ExcelReader.class);

    /**
     * Reads the first data row (row 1) as a key-value map using header row 0.
     */
    public Map<String, String> readFirstRow(String pathWithinResources, String sheetName) {
        Map<String, String> out = new HashMap<>();
        try (InputStream in = getClass().getClassLoader().getResourceAsStream(pathWithinResources)) {
            if (in == null) throw new RuntimeException("Excel not found in resources: " + pathWithinResources);
            Workbook wb = WorkbookFactory.create(in);
            Sheet sheet = wb.getSheet(sheetName);
            if (sheet == null) throw new RuntimeException("Sheet not found: " + sheetName);
            Row header = sheet.getRow(0);
            Row row = sheet.getRow(1);
            if (header == null || row == null) throw new RuntimeException("Insufficient rows in sheet");

            Iterator<Cell> it = header.cellIterator();
            int idx = 0;
            while (it.hasNext()) {
                Cell h = it.next();
                Cell v = row.getCell(idx);
                String key = h.getStringCellValue().trim();
                String val = (v == null) ? "" : getCellValueAsString(v);
                out.put(key, val);
                idx++;
            }
            wb.close();
        } catch (Exception e) {
            throw new RuntimeException("Error reading Excel: " + e.getMessage(), e);
        }
        log.info("Excel first row loaded: {}", out);
        return out;
    }

    // Java 11-friendly version (classic switch)
    private String getCellValueAsString(Cell cell) {
        if (cell == null) return "";
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();

            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    // cast to long to avoid decimal `.0` for integral numbers
                    return String.valueOf((long) cell.getNumericCellValue());
                }

            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());

            case FORMULA:
                // depending on your need, you may evaluate formula with a FormulaEvaluator
                return cell.getCellFormula();

            default:
                return "";
        }
    }
}
