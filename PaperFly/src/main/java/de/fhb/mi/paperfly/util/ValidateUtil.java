package de.fhb.mi.paperfly.util;

import android.util.Patterns;

/**
 * Util for validating input values
 * @author Christoph Ott
 */
public class ValidateUtil {

    /**
     * Pattern for only char and numbers
     */
    public static final String PATTERN_ONLY_CHARACTERS_AND_NUMBERS = "^[a-zA-Z0-9]+$";

    /**
     * validate email address
     * @param email
     * @return
     */
    public static boolean isValidEmailAddress(String email) {
        return email != null && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    /**
     * check for only chars and numbers
     *
     * @param string
     * @return
     */
    public static boolean onlyCharactersAndNumbers(String string){
        return string != null && string.matches(PATTERN_ONLY_CHARACTERS_AND_NUMBERS);
    }

}
