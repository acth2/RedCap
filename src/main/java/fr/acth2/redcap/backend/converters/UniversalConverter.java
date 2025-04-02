package fr.acth2.redcap.backend.converters;

import fr.acth2.redcap.backend.converters.fit.FitConverter;
import fr.acth2.redcap.backend.converters.gpx.GpxConverter;
import fr.acth2.redcap.backend.converters.tcx.TcxConverter;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class UniversalConverter {
    private static final Map<String, FileConverter> converters;
    static {
        Map<String, FileConverter> tempMap = new HashMap<>();
        tempMap.put(".fit", new FitConverter());
        tempMap.put(".tcx", new TcxConverter());
        tempMap.put(".gpx", new GpxConverter());
        converters = Collections.unmodifiableMap(tempMap);
    }

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: java UniversalConverter <inputfile> [output.csv]");
            return;
        }

        File inputFile = new File(args[0]);
        String outputFile = args.length > 1 ? args[1] :
                inputFile.getName().replaceFirst("[.][^.]+$", "") + ".csv";

        String extension = inputFile.getName().substring(inputFile.getName().lastIndexOf('.'));
        FileConverter converter = converters.get(extension.toLowerCase());

        if (converter == null) {
            System.err.println("Unsupported file format: " + extension);
            return;
        }

        try {
            List<Map<String, Object>> records = converter.convertToRecords(inputFile);
            Map<String, Object> stats = converter.calculateStatistics(records);

            writeCsv(outputFile, records);

            System.out.println("\nStatistics:");
            stats.forEach((k, v) -> System.out.printf("%s: %s%n", k, v));

        } catch (Exception e) {
            System.err.println("Error during conversion: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void writeCsv(String filePath, List<Map<String, Object>> records) throws IOException {
        if (records.isEmpty()) {
            return;
        }

        Set<String> headers = new LinkedHashSet<>();
        for (Map<String, Object> record : records) {
            headers.addAll(record.keySet());
        }

        try (FileWriter writer = new FileWriter(filePath);
             CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader(headers.toArray(new String[0])))) {

            for (Map<String, Object> record : records) {
                List<String> values = new ArrayList<>();
                for (String header : headers) {
                    Object value = record.get(header);
                    values.add(value != null ? value.toString() : "");
                }
                csvPrinter.printRecord(values);
            }
        }
    }
}