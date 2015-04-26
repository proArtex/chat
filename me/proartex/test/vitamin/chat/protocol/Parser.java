package me.proartex.test.vitamin.chat.protocol;

import me.proartex.test.vitamin.chat.exceptions.UnknownParseTypeException;

//TODO: different classes
public class Parser {

    public static final int INT_ERROR_VALUE = 0;

    public static Object parseString(String value, Class cl) {
        if ("null".equals(value))
            return null;

        if (String.class == cl)
            return value;

        if (int.class == cl) {
            try {
                return Integer.parseInt(value);
            }
            catch (NumberFormatException e) {
                return INT_ERROR_VALUE;
            }
        }

        throw new UnknownParseTypeException("undefined parser's " + cl);
    }
}
