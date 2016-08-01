package org.bs.jnonogram.tui;

import java.io.File;
import java.util.Scanner;

public class TerminalUtils {
    public static void repeatString(String s, int times) {
        for (int i = 0; i < times; i++) {
            System.out.print(s);
        }
    }

    public static int expectInteger(String message) {
        Scanner scanner = new Scanner(System.in);

        for (; ; ) {
            System.out.print(message);
            System.out.print(": ");
            try {
                return scanner.nextInt();
            } catch (java.util.InputMismatchException ex) {
                System.out.println("Please enter a number");
                scanner.nextLine();
            }
        }
    }

    public static int expectInteger(String message, int min, int max) {
        for (; ; ) {
            int res = expectInteger(message);
            if (res < min || res > max) {
                System.out.println("Number out of range");
            } else {
                return res;
            }
        }
    }

    public static boolean AskYesNoQuestion(String question) {
        Scanner scanner = new Scanner(System.in);

        for (; ; ) {
            System.out.print(question);
            System.out.print(" ");
            String line = scanner.nextLine();
            if (line.trim().equalsIgnoreCase("yes")) {
                return true;
            } else if (line.trim().equalsIgnoreCase("no")) {
                return false;
            } else {
                System.out.println("Please enter either 'yes' or 'no'");
            }
        }
    }

    public static File expectPath(String message, boolean checkReadable, boolean checkWritable) {
        Scanner scanner = new Scanner(System.in);
        File file;

        for (; ; ) {
            System.out.print(message);
            System.out.print(": ");
            String line = scanner.nextLine();
            file = new File(line);
            if (!file.exists()) {
                System.out.println("File does not exist");
            } else if (checkReadable && !file.canRead()) {
                System.out.println("File is not readable");
            } else if (checkWritable && !file.canWrite()) {
                System.out.println("File is not writable");
            } else {
                return file;
            }
        }
    }

    public static <T extends Enum<T>> T expectEnum(String message, Enum<T> e) {
        Scanner scanner = new Scanner(System.in);
        for (;;) {
            System.out.print(message);
            System.out.println(":");
            Enum<T>[] constants = e.getClass().getEnumConstants();
            int i = 1;
            for (Enum<T> value : constants) {
                System.out.print(String.valueOf(i));
                System.out.print(") ");
                System.out.println(value);
                i++;
            }

            System.out.println("Please select an option: ");
            int selection;
            try {
                selection = scanner.nextInt() - 1;
            } catch (Exception ex) {
                System.out.println("Please enter a number: ");
                scanner.nextLine();
                continue;
            }

            if (selection < 0 | selection >= constants.length) {
                System.out.println("Selection out of range");
                continue;
            }

            return (T) constants[selection];
        }
    }

    public static String ExpectString(String message, boolean allowEmpty) {
        Scanner scanner = new Scanner(System.in);
        for (; ; ) {
            System.out.print(message);
            System.out.println(":");
            String result = scanner.nextLine().trim();
            if (!result.isEmpty() || allowEmpty) {
                return result;
            }

            System.out.println("Empty response is not allowed");
        }
    }
}
