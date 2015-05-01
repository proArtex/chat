package me.proartex.test.vitamin.chat;

import java.lang.reflect.Array;
import java.util.regex.Pattern;

public class Utils {

    public static final String LINE_SEPARATOR = System.getProperty("line.separator");

    public static String addLineSeparator(String message) {
        return message + LINE_SEPARATOR;
    }

    public static String implodeStringArray(String[] pieces, String glue) {
        String result = "";

        for (int i = 0; i < pieces.length; i++) {
            result += pieces[i];
            if (i < pieces.length - 1)
                result += glue;
        }

        return result;
    }

    public static String[] explodeString(String context, String delimiter) {
        return context.split(Pattern.quote(delimiter));
    }

    public static Object[] castObjectToArray(Object array) {
        Object[] castedArray = new Object[Array.getLength(array)];

        for(int i = 0; i < castedArray.length; i++) {
            castedArray[i] = Array.get(array, i);
        }

        return castedArray;
    }
}
