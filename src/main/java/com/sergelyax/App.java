package com.sergelyax;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

public class App {

    private static int groupCount = 0;
    private static final String regex = "^(\"\\d*\")(;\"\\d*\")*$";
    private static final Pattern pattern = Pattern.compile(regex);

    private static volatile boolean check = true;
    private static Set<Long[]> mainFilteredSet = new LinkedHashSet<>();
    private static Set<Map<Long, Set<Long[]>>> result = new LinkedHashSet<>();
    private static int maxAmountElementsOfLine = 0;
    private static String pathToFile = "";

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: java -Xmx1G -jar target/my-new-maven-project-1.0-SNAPSHOT.jar <input-file>");
            return;
        }

        pathToFile = args[0];
        LocalTime startTime = LocalTime.now();

        Runnable task = new Runnable() {
            int countTime = 0;

            public void run() {
                while (check) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        System.out.println(e.getMessage());
                    }
                    System.out.println(countTime++);
                }
            }
        };

        Thread myThread = new Thread(task);
        myThread.start();

        System.out.println("Starting read file");
        Set<Long[]> copyOfMainSet = readFile(pathToFile, pattern);
        if (!copyOfMainSet.isEmpty()) {
            findMaxAmount();
            Set<Map<Long, Set<Long[]>>> forTest = findMatches(copyOfMainSet, maxAmountElementsOfLine);
            print(forTest);
        } else {
            System.out.println("File empty or incorrect format");
        }
        check = false;
        long secondsOfWork = ChronoUnit.SECONDS.between(startTime, LocalTime.now());
        System.out.printf("Complete, work time: %d sec \n", secondsOfWork);
    }

    public static Set<Long[]> readFile(String pathToFile, Pattern pattern) {
        Set<Long[]> set = null;
        try (BufferedReader reader = new BufferedReader(new FileReader(pathToFile))) {
            set = reader.lines().filter(s -> {
                        Matcher matcher = pattern.matcher(s);
                        return matcher.matches();
                    })
                    .filter(s -> (s.length() > 2))
                    .distinct()
                    .map(s -> s.replace("\"\"", "0"))
                    .map(s -> s.replace("\"", ""))
                    .map(s -> Arrays.stream(s.split(";"))
                            .map(Long::valueOf)
                            .toArray(Long[]::new))
                    .collect(Collectors.toSet());
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Read fail or wrong path");
        }

        if (set != null && !set.isEmpty()) {
            mainFilteredSet.addAll(set);
        }

        return new LinkedHashSet<>(mainFilteredSet);
    }

    private static void findMaxAmount() {
        for (Long[] x : mainFilteredSet) {
            if (maxAmountElementsOfLine <= x.length)
                maxAmountElementsOfLine = x.length;
        }
    }

    public static Set<Map<Long, Set<Long[]>>> findMatches(Set<Long[]> copyOfMainSet, int maxElements) {
        int size = 1;
        Set<Map<Long, Set<Long[]>>> setOfMatchesPerColumn = new LinkedHashSet<>();
        while (maxElements >= size) {

            int finalSize = size;
            long[] thumb = copyOfMainSet.stream()
                    .flatMapToLong(s -> LongStream.of(Arrays.stream(s)
                            .skip(finalSize - 1)
                            .findFirst()
                            .orElse(0L)))
                    .toArray();

            Set<Long> clear = new HashSet<>();
            Set<Long> copies = new HashSet<>();
            for (long l : thumb) {
                if (l != 0 && !clear.add(l)) {
                    copies.add(l);
                }
            }

            Map<Long, Set<Long[]>> mapWithMatches = copyOfMainSet.stream()
                    .filter(s -> Arrays.stream(s)
                            .skip(finalSize - 1)
                            .limit(1)
                            .anyMatch(copies::contains))
                    .collect(Collectors.groupingBy(
                            s -> Arrays.stream(s)
                                    .skip(finalSize - 1)
                                    .limit(1)
                                    .findFirst().orElse(0L),
                            Collectors.toSet()
                    ));

            if (!mapWithMatches.isEmpty()) {
                result.add(mapWithMatches);
                setOfMatchesPerColumn.add(mapWithMatches);
            }
            size++;
        }
        return setOfMatchesPerColumn;
    }

    public static void print(Set<Map<Long, Set<Long[]>>> setOfDuplicatesInMaps) {
        String newFileName = pathToFile.replace(".txt", "-output.txt");
        Path outputPath = Path.of(newFileName);
        if (!Files.exists(outputPath)) {
            try {
                Files.createFile(outputPath);
            } catch (IOException e) {
                System.out.println(e.getMessage());
                System.out.println("Error creating file");
            }
        }
        try (PrintWriter out = new PrintWriter(Files.newBufferedWriter(outputPath))) {
            out.println(takeQuantityOfGroups());
            List<Map<Long, Set<Long[]>>> sortedMaps = new ArrayList<>(setOfDuplicatesInMaps);
            sortedMaps.sort((map1, map2) -> Integer.compare(map2.values().iterator().next().size(), map1.values().iterator().next().size()));

            int groupNumber = 1;
            for (Map<Long, Set<Long[]>> map : sortedMaps) {
                for (Map.Entry<Long, Set<Long[]>> entry : map.entrySet()) {
                    out.printf("Group %d\n", groupNumber++);
                    out.printf("Key %d\n", entry.getKey());
                    out.printf("Matches %d\n", entry.getValue().size());
                    entry.getValue().forEach(s -> out.println(Arrays.toString(s)));
                }
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
            System.out.println("Error writing file");
        }
    }

    private static int takeQuantityOfGroups() {
        int quantityOfGroups = 0;
        for (Map<Long, Set<Long[]>> x : result) {
            quantityOfGroups += x.keySet().size();
        }
        return quantityOfGroups;
    }
}
