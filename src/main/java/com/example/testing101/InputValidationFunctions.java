package com.example.testing101;

import java.util.regex .*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class InputValidationFunctions {

    // 1. Validate userID (only numbers, max 8 digits)
    public static boolean isValidUserID(Integer number) {
        if (number == null) {
            return false; // Reject null input
        }

        // Convert the integer to a string and check against regex
        String numStr = Integer.toString(number);
        return numStr.matches("^\\d{1,8}$"); // 1-8 digits, non-negative
    }

    public static boolean isValidOtherID(Integer number) { //this one is to check for auto-incremented values like petID,customerID
        if (number == null) {
            return false; // Reject null input
        }

        // Convert the integer to a string and check against regex
        String numStr = Integer.toString(number);
        return numStr.matches("^\\d{1,11}$"); // 1-11 digits, non-negative
    }


    // 2. Validate password (min 8 length, at least 2 numbers, 2 letters, 1 special character (!_))
    public static boolean isValidPassword(String password) {
        return password.matches("^(?=(.*\\d){2,})(?=(.*[a-zA-Z]){2,})(?=(.*[!_]){1,}).{8,}$");
    }

    // 3. Validate and convert date of birth (dd-mm-yyyy -> yyyy-MM-dd for SQL)
    public static String validateAndFormatDOB(String dob) {
        try {
            DateTimeFormatter inputFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            DateTimeFormatter outputFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate date = LocalDate.parse(dob, inputFormat);
            return date.format(outputFormat);
        } catch (DateTimeParseException e) {
            return null; // Invalid date format
        }
    }

    // 4. Validate gender (only "male" or "female", case insensitive)
    public static boolean isValidGender(String gender) {
        return gender.equalsIgnoreCase("male") || gender.equalsIgnoreCase("female");
    }

    // 5. Validate last name (only letters, not null)
    public static boolean isValidLastName(String lastName) {
        return lastName != null && lastName.matches("^[a-zA-Z]+$");
    }

    // 6. Validate first name (only letters, not null)
    public static boolean isValidFirstName(String firstName) {
        return firstName != null && firstName.matches("^[a-zA-Z]+$");
    }

    // 7. Validate Qatari phone number (must start with specific prefixes)
    public static boolean isValidQatariPhoneNumber(String phoneNumber) {
        return phoneNumber.matches("^(?:\\+974|974)?(3[0-9]{7}|5[0-9]{7}|6[0-9]{7}|7[0-9]{7})$");
    }  //here for some reason when im just using \ its giing mev an error not sure why?

    // 8. Regex pattern to match valid times between 9 AM and 5 PM inclusive

    public static boolean isTimeValid(String timeStr) {
        String regex =
                "^(?i)" + // Case-insensitive
                        "(" +
                        // 12-hour format (9:00 AM to 11:59 AM)
                        "(0?[9]|10|11):[0-5][0-9]\\s*([AP]M)" + "|" +
                        // 12-hour format (12:00 PM to 4:59 PM and 5:00 PM)
                        "((0?[12]|0?[1-4]):[0-5][0-9]\\s*PM|5:00\\s*PM)" + "|" +
                        // 24-hour format (09:00 to 17:00)
                        "((09|1[0-6]):[0-5][0-9]|17:00)" +
                        ")$";

        return Pattern.matches(regex, timeStr);
    }
}
