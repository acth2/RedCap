package fr.acth2.redcap.backend.math;

import java.util.*;

public class StatisticsCalculator {
    public static Map<String, Object> calculateStatistics(List<Map<String, Object>> records) {
        Map<String, Object> stats = new HashMap<>();

        if (records.isEmpty()) return stats;

        Set<String> numericFields = identifyNumericFields(records.get(0));

        for (String field : numericFields) {
            List<Double> values = extractNumericValues(records, field);
            if (!values.isEmpty()) {
                stats.put(field + "_avg", calculateAverage(values));
                stats.put(field + "_max", Collections.max(values));
                stats.put(field + "_min", Collections.min(values));
                stats.put(field + "_sum", values.stream().mapToDouble(Double::doubleValue).sum());
            }
        }

        return stats;
    }

    private static Set<String> identifyNumericFields(Map<String, Object> sampleRecord) {
        Set<String> numericFields = new HashSet<>();
        for (Map.Entry<String, Object> entry : sampleRecord.entrySet()) {
            if (entry.getValue() instanceof Number) {
                numericFields.add(entry.getKey());
            }
        }
        return numericFields;
    }

    private static List<Double> extractNumericValues(List<Map<String, Object>> records, String field) {
        List<Double> values = new ArrayList<>();
        for (Map<String, Object> record : records) {
            if (record.containsKey(field) && record.get(field) instanceof Number) {
                values.add(((Number) record.get(field)).doubleValue());
            }
        }
        return values;
    }

    private static double calculateAverage(List<Double> values) {
        return values.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
    }
}