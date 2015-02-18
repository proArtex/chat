package me.proartex.test.vitamin.chat;

import me.proartex.test.vitamin.chat.commands.Executable;
import me.proartex.test.vitamin.chat.exceptions.UnknownParseTypeException;
import me.proartex.test.vitamin.chat.server.Server;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;


public class Protocol {

    private static final String COMMAND_DELIMITER   = "<SYS_COMMAND>";
    private static final String CLASSNAME_DELIMITER = "<args>";
    private static final String ARGUMENTS_DELIMITER = ";";
    private static final String VALUE_DELIMITER     = "=";

    public static String serialize(Executable command) {
        Class cl                        = command.getClass();
        StringBuilder serializedCommand = new StringBuilder(COMMAND_DELIMITER);

        try {
            serializedCommand.append(cl.getName()).append(CLASSNAME_DELIMITER);

            for (java.lang.reflect.Field field : cl.getDeclaredFields()) {
                //do not serialize server
                if ("server".equals(field.getName()))
                    continue;

                field.setAccessible(true);
                serializedCommand
                        .append(field.getType().getName())
                        .append("=")
                        .append(field.getType().isArray() ? Arrays.toString( (byte[]) field.get(command))
                                                          : field.get(command)) //TODO: other types?
                        .append(";");
            }

//            System.out.println("command: '"+serializedCommand+"'");
            return serializedCommand.toString();
        }
        catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static ArrayList<Executable> deserialize(Server server, String serializedCommand) {
        ArrayList<Executable> executableCommands = new ArrayList<>();

        try {
            for (String command : serializedCommand.split(COMMAND_DELIMITER)) {
                command = command.trim();
                if ("".equals(command))
                    continue;

                Class[] classes, fullClasses;
                Object[] arguments, fullArguments;

                String className = command.substring(0, command.indexOf(CLASSNAME_DELIMITER));
                Class<?> cl      = Class.forName(className);

                String argsLine = command.substring(command.indexOf(CLASSNAME_DELIMITER) + CLASSNAME_DELIMITER.length());
                String[] args   = argsLine.split(ARGUMENTS_DELIMITER);
                int argsLength  = (args.length == 1 && "".equals(args[0]))
                                ? 0
                                : args.length;

                //origin and server reserved arrays
                classes       = new Class[argsLength];
                arguments     = new Object[argsLength];
                fullClasses   = new Class[argsLength+1];
                fullArguments = new Object[argsLength+1];

                for (int i = 0; i < argsLength; i++) {
                    String key   = args[i].substring(0, args[i].indexOf(VALUE_DELIMITER));
                    String value = args[i].substring(args[i].indexOf(VALUE_DELIMITER) + VALUE_DELIMITER.length());

                    classes[i]   = getClassFor(key);
                    arguments[i] = parseString(value, classes[i]);
                }

                //put server inside
                fullClasses[0]   = Server.class;
                fullArguments[0] = server;
                System.arraycopy(classes, 0, fullClasses, 1, classes.length);
                System.arraycopy(arguments, 0, fullArguments, 1, arguments.length);

                Executable executableCommand = (Executable) cl.getDeclaredConstructor(fullClasses).newInstance(fullArguments);
                executableCommands.add(executableCommand);
            }
        }
        catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
        catch (IndexOutOfBoundsException e) {
            //TODO: validate it before
            e.printStackTrace();
        }

        return executableCommands;
    }


//    private static boolean isValidCommand(String command) {
//        System.out.println(command);
//        //^[^<]+<args>(?:[a-z0-9_]+=(?:null|\[(?:\d+, )+\d+\]|\d);)+$
//        Pattern pattern = Pattern.compile("^[^<]+<args>(?:[a-z0-9_]+=null;|message=\\[[^\\]]+\\];)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
//        Matcher matcher = pattern.matcher(command);
//
//        return matcher.matches();
//    }

    private static Class getClassFor(String value) throws ClassNotFoundException {
        switch (value) {
            case "int":
                return int.class;
            case "long":
                return long.class;
            case "float":
                return float.class;
            case "double":
                return float.class;
            case "short":
                return short.class;
            case "byte":
                return byte.class;
            case "char":
                return char.class;
            case "boolean":
                return boolean.class;
            default:
                return Class.forName(value);
        }
    }

    private static Object parseString(String value, Class cl) {
        if ("null".equals(value))
            return null;

        if (byte[].class == cl) {
            String[] items = value.substring(1, value.length()-1).split(", ");
            int length     = items.length == 1 && "".equals(items[0]) ? 0 : items.length;
            byte[] result  = new byte[length];

            for (int i = 0; i < length; i++) {
                if (byte.class == cl.getComponentType()) {
                    result[i] = (byte) parseString(items[i], cl.getComponentType());
                }
            }

            return result;
        }
        else if (byte.class == cl) {
            return Byte.parseByte(value);
        }
        else {
            throw new UnknownParseTypeException("undefined parser's " + cl);
        }
    }
}
