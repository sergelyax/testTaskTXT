package com.sergelyax;

import com.sergelyax.data.Data;
import com.sergelyax.entity.Entity;
import com.sergelyax.entity.MultiEntity;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

public class App {

    private static Set<MultiEntity> uniqueStrings = new HashSet<>();
    private static Map<Entity, List<MultiEntity>> containers = new HashMap<>();
    private static Map<Integer, List<Data>> orderedGroup = new TreeMap<>(Collections.reverseOrder());

    private static int countGroup = 0;

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: java -Xmx1G -jar target/my-new-maven-project-1.0-SNAPSHOT.jar <input-file>");
            return;
        }

        String inputFile = args[0];
        long start = System.currentTimeMillis();

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                filterData(line.replace("\"", "").split(";", -1));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        clearGroupWithoutPair();
        finalGrouping();
        printResults();

        System.out.printf("%.2f seconds%n", (System.currentTimeMillis() - start) / 1000f);
    }

    private static void filterData(String[] values) {
        if (values.length == 3) {
            MultiEntity multiEntity = new MultiEntity(values);
            if (multiEntity.isLegit() && uniqueStrings.add(multiEntity)) {
                primaryGrouping(multiEntity);
            }
        }
    }

    private static void primaryGrouping(MultiEntity multiEntity) {
        for (Entity entity : multiEntity.getLegitEntities()) {
            containers.computeIfAbsent(entity, k -> new ArrayList<>()).add(multiEntity);
        }
    }

    private static void clearGroupWithoutPair() {
        containers.entrySet().removeIf(entry -> entry.getValue().size() <= 1);
    }

    private static void finalGrouping() {
        additionalGrouping();

        for (List<MultiEntity> group : containers.values()) {
            Data data = new Data(group);
            orderedGroup.computeIfAbsent(data.size(), k -> new ArrayList<>()).add(data);
            countGroup++;
        }
    }

    private static void additionalGrouping() {
        Map<MultiEntity, Integer> multiEntityCount = new HashMap<>();

        for (List<MultiEntity> entities : containers.values()) {
            for (MultiEntity multiEntity : entities) {
                multiEntityCount.merge(multiEntity, 1, Integer::sum);
            }
        }

        List<Set<MultiEntity>> multiEntitySubGroup = new ArrayList<>();
        for (Map.Entry<MultiEntity, Integer> entry : multiEntityCount.entrySet()) {
            if (entry.getValue() > 1) {
                Set<MultiEntity> set = new HashSet<>();
                for (Entity entity : entry.getKey().getLegitEntities()) {
                    if (containers.containsKey(entity)) {
                        set.addAll(containers.remove(entity));
                    }
                }
                if (set.size() > 1)
                    multiEntitySubGroup.add(set);
            }
        }

        for (Set<MultiEntity> subGroup : multiEntitySubGroup) {
            Data data = new Data(new ArrayList<>(subGroup));
            orderedGroup.computeIfAbsent(data.size(), k -> new ArrayList<>()).add(data);
            countGroup++;
        }
    }

    private static void printResults() {
        System.out.println("Group count: " + countGroup);
        int count = 1;
        try (PrintWriter writer = new PrintWriter("output.txt")) {
            for (List<Data> dataList : orderedGroup.values()) {
                for (Data data : dataList) {
                    writer.printf("Group %d%n", count++);
                    writer.println(data);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
