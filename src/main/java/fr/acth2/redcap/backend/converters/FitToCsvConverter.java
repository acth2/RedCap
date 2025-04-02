package fr.acth2.redcap.backend.converters;

import com.garmin.fit.*;
import org.apache.commons.csv.*;

import java.io.*;
import java.util.*;

import static fr.acth2.redcap.utils.References.*;

public class FitToCsvConverter {

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: java FitToCsvConverter <input.fit> [output.csv]");
            return;
        }

        String inputFile = args[0];
        String outputFile = args.length > 1 ? args[1] : inputFile.replace(".fit", ".csv");

        try {
            convertFitToCsv(inputFile, outputFile);
            System.out.println("Conversion successful: " + outputFile);
        } catch (Exception e) {
            System.err.println("Error during conversion: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void convertFitToCsv(String fitFilePath, String csvFilePath) throws IOException, FitRuntimeException {
        Decode decode = new Decode();
        MesgBroadcaster mesgBroadcaster = new MesgBroadcaster(decode);
        FitToCsvListener listener = new FitToCsvListener();

        mesgBroadcaster.addListener(listener);
        mesgBroadcaster.run(new FileInputStream(fitFilePath));

        writeCsv(csvFilePath, listener.getRecords(), listener.getHeaders());
    }

    private static void writeCsv(String filePath, List<Map<String, Object>> records, Set<String> headers)
            throws IOException {
        try (FileWriter writer = new FileWriter(filePath);
             CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader(headers.toArray(new String[0])))) {

            for (Map<String, Object> record : records) {
                List<String> values = new ArrayList<>();
                for (String header : headers) {
                    values.add(record.containsKey(header) ? record.get(header).toString() : "");
                }
                csvPrinter.printRecord(values);
            }
        }
    }
}

class FitToCsvListener implements MesgListener {
    private final List<Map<String, Object>> records = new ArrayList<>();
    private final Set<String> headers = new LinkedHashSet<>();

    @Override
    public void onMesg(Mesg mesg) {
        Map<String, Object> record = new HashMap<>();

        for (Field field : mesg.getFields()) {
            String fieldName = mesg.getName() + "_" + field.getName();
            Object value = field.getValue();

            if (value instanceof Number) {
                double scaledValue = ((Number) value).doubleValue();
                if (!Double.isNaN(getFieldOffset(field)) && getFieldScale(field) != 1.0) {
                    scaledValue /= getFieldScale(field);
                }
                if (!Double.isNaN(getFieldOffset(field)) && getFieldOffset(field) != 0.0) {
                    scaledValue -= getFieldOffset(field);
                }
                record.put(fieldName, scaledValue);
            } else {
                record.put(fieldName, value);
            }

            headers.add(fieldName);
        }

        records.add(record);
    }

    public List<Map<String, Object>> getRecords() {
        return records;
    }

    public Set<String> getHeaders() {
        return headers;
    }
}