package ch.heig.dai.lab.smtp;

import java.util.*;
import java.io.*;

public class PrankGenerator {
    private static final String VICTIMS_FILE = "config/victims.txt";
    private static final String MESSAGES_FILE = "config/messages.txt";
    private static final String MESSAGE_SEPARATOR = "===\n";
    private static final String SUBJECT_PREFIX = "Subject:";
    private static final String BODY_PREFIX = "Body:";
    private static final int MIN_GROUP_SIZE = 2;
    private static final int MAX_GROUP_SIZE = 5;
    private static final Random random = new Random();

    public static List<Email> generatePranks(int numberOfGroups) {
        try (BufferedReader victimsReader = new BufferedReader(new InputStreamReader(
                new FileInputStream(VICTIMS_FILE)));
             BufferedReader messagesReader = new BufferedReader(new InputStreamReader(
                     new FileInputStream(MESSAGES_FILE)))) {

            String victims = victimsReader.readLine();
            List<String> victimsList = Arrays.asList(victims.split(", "));
            Collections.shuffle(victimsList);

            int[] groupSizes = setGroupSizes(victimsList.size(), numberOfGroups);

            StringBuilder sb = new StringBuilder();
            String line = messagesReader.readLine();
            while (null != line) {
                sb.append(line).append("\n");
                line = messagesReader.readLine();
            }
            List<String> messagesList = Arrays.asList(sb.toString().split(MESSAGE_SEPARATOR));

            List<Email> emails = new ArrayList<>();
            for (int i = 0; i < numberOfGroups; i++) {
                String[] message = parseMessage(messagesList.get(random.nextInt(messagesList.size())));
                emails.add(new Email(victimsList.getFirst(), victimsList.subList(1, groupSizes[i]),
                        message[0], message[1]));
            }

            return emails;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static int[] setGroupSizes(int totalVictims, int numberOfGroups) {
        if (totalVictims < MIN_GROUP_SIZE * numberOfGroups || totalVictims > MAX_GROUP_SIZE * numberOfGroups) {
            throw new IllegalArgumentException("The number of victims is not compatible with the number of groups");
        }

        int[] groupSizes = new int[numberOfGroups];

        Arrays.fill(groupSizes, MIN_GROUP_SIZE);
        int remaining = totalVictims - numberOfGroups * MIN_GROUP_SIZE;

        while (remaining > 0) {
            for (int i = 0; i < numberOfGroups && remaining > 0; i++) {
                int maxAddable = Math.min(remaining, MAX_GROUP_SIZE - groupSizes[i]);
                if (maxAddable > 0) {
                    int toAdd = random.nextInt(maxAddable + 1);
                    groupSizes[i] += toAdd;
                    remaining -= toAdd;
                }
            }
        }
        return groupSizes;
    }

    public static String[] parseMessage(String input) {
        if (input == null || !input.contains(SUBJECT_PREFIX) || !input.contains(BODY_PREFIX)) {
            throw new IllegalArgumentException("The String format is invalid.");
        }
        String[] result = new String[2];

        int subjectStart = input.indexOf(SUBJECT_PREFIX) + SUBJECT_PREFIX.length();
        int subjectEnd = input.indexOf(BODY_PREFIX);
        int bodyStart = subjectEnd + BODY_PREFIX.length();
        result[0] = input.substring(subjectStart, subjectEnd).trim();
        result[1] = input.substring(bodyStart).trim();

        return result;
    }
}