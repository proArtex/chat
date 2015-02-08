package me.proartex.test.vitamin.chat.nio;

import java.util.ArrayList;
import java.util.Arrays;


public class Protocol {

    private static final String COMMAND_DELIMITER   = "<SYS_COMMAND>";
    private static final String CLASSNAME_DELIMITER = "<args>";
    private static final String ARGUMENTS_DELIMITER = ";";
    private static final String VALUE_DELIMITER     = "=";

    protected static String serialize(Executable command) {
        Class cl                        = command.getClass();
        StringBuilder serializedCommand = new StringBuilder(COMMAND_DELIMITER);

        try {
            serializedCommand.append(cl.getName()).append(CLASSNAME_DELIMITER);

            for (java.lang.reflect.Field field : cl.getDeclaredFields()) {
                //do not serialize name and description
//                if ("NAME".equals(field.getName()) || "DESCRIPTION".equals(field.getName()))
//                    continue;

                field.setAccessible(true);
                serializedCommand
                        .append(field.getType().getName())
                        .append("=")
                        .append(field.getType().isArray() ? Arrays.toString( (byte[]) field.get(command))
                                                          : field.get(command)) //TODO: other types?
                        .append(";");
            }

            System.out.println("command: '"+serializedCommand+"'");
            return serializedCommand.toString();
        }
        catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return null;
    }

    protected static ArrayList<Executable> deserialize(Server server, String serializedCommand) {

        ArrayList<Executable> executableCommands = new ArrayList<>();

        try {
            for (String command : serializedCommand.split(COMMAND_DELIMITER)) {
                if ("".equals(command)) continue;

                Class[] classes;
                Object[] arguments;
                System.out.println(serializedCommand);
                String className = command.substring(0, command.indexOf(CLASSNAME_DELIMITER));
                Class<?> cl      = Class.forName(className);
//                System.out.println("class: " + className);

                String argsLine = command.substring(command.indexOf(CLASSNAME_DELIMITER) + CLASSNAME_DELIMITER.length());
                String[] args   = argsLine.split(ARGUMENTS_DELIMITER);

                classes   = new Class[args.length];
                arguments = new Object[args.length];

                for (int i = 0; i < args.length; i++) {
                    String key = args[i].substring(0, args[i].indexOf(VALUE_DELIMITER));
                    String value = args[i].substring(args[i].indexOf(VALUE_DELIMITER) + VALUE_DELIMITER.length());
//                    System.out.println("key: " + key + " value: " + value);

                    classes[i]   = Class.forName(key);
                    arguments[i] = Server.class == classes[i] ? server : parseString(value, classes[i]);
                }

                Executable executableCommand = (Executable) cl.getDeclaredConstructor(classes).newInstance(arguments);
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
//            throw new NotParsedTypeException();
            System.out.println("EXCEPTION HERE");
            return null;
        }
    }
}
