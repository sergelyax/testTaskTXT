package com.sergelyax;

import com.sergelyax.data.DataGroup;
import com.sergelyax.entity.ColumnEntity;
import com.sergelyax.entity.MultiColumnEntity;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

public class App {

    private static final Set<MultiColumnEntity> uniqueEntities = new HashSet<>();
    private static final Map<ColumnEntity, List<MultiColumnEntity>> entityContainers = new HashMap<>();
    private static final Map<Integer, List<DataGroup>> orderedGroups = new TreeMap<>(Collections.reverseOrder());

    private static int groupCount = 0;

    @SuppressWarnings("CallToPrintStackTrace")
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: java -Xmx1G -jar target/my-new-maven-project-1.0-SNAPSHOT.jar <input-file>");
            return;
        }

        String inputFile = args[0];
        long startTime = System.currentTimeMillis();

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                processLine(line.replace("\"", "").split(";", -1));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        removeGroupsWithoutPair();
        performFinalGrouping();
        outputResults();

        System.out.printf("%.2f seconds%n", (System.currentTimeMillis() - startTime) / 1000f);
    }

    private static void processLine(String[] values) {
        if (values.length == 3) {
            MultiColumnEntity multiColumnEntity = new MultiColumnEntity(values);
            if (multiColumnEntity.isLegit() && uniqueEntities.add(multiColumnEntity)) {
                groupEntities(multiColumnEntity);
            }
        }
    }

    private static void groupEntities(MultiColumnEntity multiColumnEntity) {
        for (ColumnEntity columnEntity : multiColumnEntity.getLegitColumnEntities()) {
            entityContainers.computeIfAbsent(columnEntity, k -> new ArrayList<>()).add(multiColumnEntity);
        }
    }

    private static void removeGroupsWithoutPair() {
        entityContainers.entrySet().removeIf(entry -> entry.getValue().size() <= 1);
    }

    private static void performFinalGrouping() {
        groupAdditionalEntities();

        for (List<MultiColumnEntity> entities : entityContainers.values()) {
            DataGroup dataGroup = new DataGroup(entities);
            orderedGroups.computeIfAbsent(dataGroup.getSize(), k -> new ArrayList<>()).add(dataGroup);
            groupCount++;
        }
    }

    private static void groupAdditionalEntities() {
        Map<MultiColumnEntity, Integer> multiEntityCount = new HashMap<>();

        for (List<MultiColumnEntity> entities : entityContainers.values()) {
            for (MultiColumnEntity multiColumnEntity : entities) {
                multiEntityCount.merge(multiColumnEntity, 1, Integer::sum);
            }
        }

        List<Set<MultiColumnEntity>> subGroups = new ArrayList<>();
        for (Map.Entry<MultiColumnEntity, Integer> entry : multiEntityCount.entrySet()) {
            if (entry.getValue() > 1) {
                Set<MultiColumnEntity> set = new HashSet<>();
                for (ColumnEntity columnEntity : entry.getKey().getLegitColumnEntities()) {
                    if (entityContainers.containsKey(columnEntity)) {
                        set.addAll(entityContainers.remove(columnEntity));
                    }
                }
                if (set.size() > 1)
                    subGroups.add(set);
            }
        }

        for (Set<MultiColumnEntity> subGroup : subGroups) {
            DataGroup dataGroup = new DataGroup(new ArrayList<>(subGroup));
            orderedGroups.computeIfAbsent(dataGroup.getSize(), k -> new ArrayList<>()).add(dataGroup);
            groupCount++;
        }
    }

    @SuppressWarnings("CallToPrintStackTrace")
    private static void outputResults() {
        System.out.println("Group count: " + groupCount);
        int count = 1;
        try (PrintWriter writer = new PrintWriter("output.txt")) {
            for (List<DataGroup> dataGroups : orderedGroups.values()) {
                for (DataGroup dataGroup : dataGroups) {
                    writer.printf("Group %d%n", count++);
                    writer.println(dataGroup);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
