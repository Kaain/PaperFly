package de.fhb.mi.paperfly.util;

import android.util.Patterns;

/**
 * Util for validating input values
 *
 * @author Christoph Ott
 */
public class ValidateUtil {

    /**
     * Pattern for only char and numbers
     */
    public static final String PATTERN_ONLY_CHARACTERS_AND_NUMBERS = "^[a-zA-Z0-9]+$";

    /**
     * Validate email address
     *
     * @param email the email to validate
     *
     * @return true if email is valid, false if not
     */
    public static boolean isValidEmailAddress(String email) {
        return email != null && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    /**
     * Check for only chars and numbers
     *
     * @param string the string to validate
     *
     * @return true if the string is valid, false if not
     */
    public static boolean onlyCharactersAndNumbers(String string) {
        return string != null && string.matches(PATTERN_ONLY_CHARACTERS_AND_NUMBERS);
    }

}
