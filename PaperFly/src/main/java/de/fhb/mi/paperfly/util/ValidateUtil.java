package de.fhb.mi.paperfly.util;

import android.util.Patterns;

/**
 * @author Christoph Ott
 */
public class ValidateUtil {

    public static boolean isValidEmailAddress(String email) {
        return email != null && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}
