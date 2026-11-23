package org.uaemex.cleanerservice.domain.reader;


import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.uaemex.cleanerservice.models.Matrix;
import org.uaemex.cleanerservice.models.dto.MatrixReport;
import org.uaemex.cleanerservice.models.typedata.BinaryData;
import org.uaemex.cleanerservice.models.typedata.QualitativeData;
import org.uaemex.cleanerservice.models.typedata.QuantitativeData;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class FileReader implements IFileReader {

    private int column;
    private List<String> fileData;
    private int rows;
    private String delimiter;
    private Set<Double> repetitiveValues;
    private List<Double> repetitiveValuesResponse;

    @Override
    public MatrixReport getMatrix(MultipartFile file) {
        String filename = file.getOriginalFilename();
        if (filename != null && (filename.toLowerCase().endsWith(".xlsx") || filename.toLowerCase().endsWith(".xls")))readExcel(file);
        else getRowsAndColumns(file);
        return getMatrixData();
    }

    private void getRowsAndColumns(MultipartFile file) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            fileData = new ArrayList<>();
            String line = reader.readLine();

            if (line == null) throw new RuntimeException("El archivo está vacío");

            this.delimiter = determineDelimiter(line);
            int columnAux = line.trim().split(this.delimiter, -1).length;
            fileData.add(line);
            rows = 1;

            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                fileData.add(line);
                rows++;
            }
            this.column = columnAux;
        } catch (IOException e) {
            throw new RuntimeException("Error al leer el archivo");
        }
    }

    private String determineDelimiter(String line){
        if (line.contains(",")) return ",";
        if (line.contains(";")) return ";";
        if (line.contains("\t")) return "\t";
        if (line.contains("|")) return "\\|";
        return "\\s+";
    }

    private void readExcel(MultipartFile file) {
        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            DataFormatter formatter = new DataFormatter();

            fileData = new ArrayList<>();
            this.delimiter = ",";
            int maxCols = 0;

            for (Row row : sheet) {
                if (row == null) continue;
                int lastCol = row.getLastCellNum();
                if (lastCol > maxCols) maxCols = lastCol;

                List<String> cells = new ArrayList<>();
                boolean hasData = false;

                for (int c = 0; c < maxCols; c++) {
                    Cell cell = row.getCell(c, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                    String val = (cell == null) ? "" : formatter.formatCellValue(cell).trim();
                    if (!val.isEmpty()) hasData = true;
                    cells.add(val);
                }

                if (hasData) {
                    fileData.add(String.join(",", cells));
                }
            }

            if (fileData.isEmpty()) throw new RuntimeException("El archivo Excel está vacío");
            this.rows = fileData.size();
            this.column = maxCols;

        } catch (Exception e) {
            throw new RuntimeException("Error al leer el archivo Excel: " + e.getMessage());
        }
    }

    private MatrixReport getMatrixData() {
        Matrix response = new Matrix();
        repetitiveValues = new HashSet<>();
        repetitiveValuesResponse = new ArrayList<>();
        List<String> report = new ArrayList<>();

        response.setRowsNum(rows);

        response.setTotalVariables(column * rows);

        int columAux = 0;
        while (columAux < column) {
            List<String> numbers = new ArrayList<>();
            for (String line : fileData) {
                String[] parts = line.trim().split(this.delimiter, -1);
                numbers.add(columAux < parts.length ? parts[columAux] : "");
            }
            determinateRow(numbers, response, columAux,report);
            columAux++;
        }
        response.setRepetitiveValues(repetitiveValuesResponse);
        return new MatrixReport(response,new ArrayList<>(report));
    }

    private void determinateRow(List<String> numbers, Matrix m,int columnAux,List<String> report){
        RowType type = determinateType(numbers);
        determinateRepetitiveValues(numbers);
        AtomicInteger rowAux = new AtomicInteger();
        switch (type){
            case QUALITATIVE -> {
                var data = numbers.stream()
                        .map(n -> {
                            int currentRow = rowAux.incrementAndGet();
                            try { return Double.parseDouble(n.trim()); }
                            catch (Exception e) {
                                report.add("Se detecto en la columna " + (columnAux + 1) + " Fila: " + currentRow + " el caracter: " + n);
                                return null; }
                        }).toList();
                m.getQualitativeData().add(new QualitativeData(data));
            }
            case BINARY -> {
                var data = numbers.stream()
                        .map(n -> {
                            int currentRow = rowAux.incrementAndGet();
                            try { return Integer.parseInt(n.trim()); }
                            catch (Exception e) {
                                report.add("Se detecto en la columna " + (columnAux + 1) + " Fila: " + currentRow + " el caracter: " + n);
                                return null; }
                        }).toList();
                m.getBinaryData().add(new BinaryData(data));
            }
            case QUANTITATIVE -> {
                var data = numbers.stream()
                        .map(n -> {
                            int currentRow = rowAux.incrementAndGet();
                            try { return Double.parseDouble(n.trim()); }
                            catch (Exception e) {
                                report.add("Se detecto en la columna " + (columnAux + 1) + " Fila: " + currentRow  + " el caracter: " + n);
                                return null; }
                        }).toList();
                m.getQuantitativeData().add(new QuantitativeData(data));
            }
        }
    }

    private void determinateRepetitiveValues(List<String> numbers){
        var data = numbers.stream()
                .map(n -> {
                    try { return Double.parseDouble(n.trim()); }
                    catch (Exception e) { return null; }
                }).toList();

        data.forEach(d -> {
            if (d != null && !repetitiveValues.add(d) && !repetitiveValuesResponse.contains(d)) {
                repetitiveValuesResponse.add(d);
            }
        });
    }

    private RowType determinateType(List<String> numbers){
        if (isBinary(numbers)) return RowType.BINARY;
        else if (isQuantitative(numbers)) return RowType.QUANTITATIVE;
        return RowType.QUALITATIVE;
    }

    private boolean isBinary(List<String> numbers){
        for (String number : numbers) {
            String trimmed = number.trim();
            if (trimmed.isEmpty()) continue; // Ignora los huecos vacíos para no romper la clasificación
            if (!(trimmed.equals("1") || trimmed.equals("0"))) return false;
        }
        return true;
    }

    private boolean isQuantitative(List<String> numbers){
        for (String n : numbers) {
            String trimmed = n.trim();
            if (trimmed.isEmpty()) continue;
            if (trimmed.contains(".") || trimmed.contains(",")) return true;
        }
        return false;
    }

    private enum RowType{
        BINARY,
        QUALITATIVE,
        QUANTITATIVE
    }
}