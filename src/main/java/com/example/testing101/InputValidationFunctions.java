package com.example.testing101;

import java.util.regex .*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class InputValidationFunctions {

    // 1. Validate userID (only numbers, max 8 digits)
    public static boolean isValidUserID(String userID) {
        return userID.matches("\\d{1,8}");
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
}
