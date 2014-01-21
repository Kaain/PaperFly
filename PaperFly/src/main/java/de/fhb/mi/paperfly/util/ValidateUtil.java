package de.fhb.mi.paperfly.util;

import android.util.Patterns;

/**
 * @author Christoph Ott
 */
public class ValidateUtil {

    public static final String PATTERN_ONLY_CHARACTERS_AND_NUMBERS = "^[a-zA-Z0-9]+$";

    public static boolean isValidEmailAddress(String email) {
        return email != null && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static boolean onlyCharactersAndNumbers(String string){
        return string != null && string.matches(PATTERN_ONLY_CHARACTERS_AND_NUMBERS);
    }

}
