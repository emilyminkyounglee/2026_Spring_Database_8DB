package util;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

public class InputUtil {
    // [REQ15] Shared Scanner for the text-based console interface.
    private static final Scanner scanner = new Scanner(System.in);

    private InputUtil() {
    }

    // [REQ15] Reads a non-empty text value from the console.
    public static String readString(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();

            if (!input.isBlank()) {
                return input;
            }

            System.out.println("Input cannot be empty.");
        }
    }

    // [REQ15] Reads optional text input, used when a menu allows Enter to skip.
    public static String readStringOrEmpty(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    // [REQ15] Reads integer menu choices and ids from the console.
    public static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();

            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid integer.");
            }
        }
    }

    // [REQ15] Reads decimal values such as prices from the console.
    public static BigDecimal readBigDecimal(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();

            try {
                return new BigDecimal(input);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid decimal number.");
            }
        }
    }

    // [REQ15] Reads date values using yyyy-MM-dd format.
    public static Date readDate(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();

            try {
                return Date.valueOf(LocalDate.parse(input));
            } catch (DateTimeParseException e) {
                System.out.println("Please enter a date in yyyy-MM-dd format.");
            }
        }
    }
}
