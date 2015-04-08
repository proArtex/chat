package me.proartex.test.vitamin.chat.protocol;

import me.proartex.test.vitamin.chat.Command;
import me.proartex.test.vitamin.chat.client.commands.Serializable;
import me.proartex.test.vitamin.chat.server.commands.Executable;
import me.proartex.test.vitamin.chat.server.commands.*;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Protocol {

    private static final String COMMAND_DELIMITER   = "<SYS_COMMAND>";
    private static final String ARGUMENTS_DELIMITER = ";";
    private static final String VALUE_DELIMITER     = "=";

    public static String serialize(Serializable command) {
        try {
            return tryToSerialize(command);
        }
        catch (IllegalAccessException | ClassNotFoundException e) {
            e.printStackTrace();
            throw new SerializeException();
        }
    }

    private static String tryToSerialize(Serializable command) throws IllegalAccessException, ClassNotFoundException {
        StringBuilder serializedCommand = new StringBuilder(COMMAND_DELIMITER);
        Class cl = command.getClass();

        for (Field field : cl.getDeclaredFields()) {
            field.setAccessible(true);
            serializedCommand
                    .append(field.getName())
                    .append("=")
                    .append(field.getType().isArray() ? arrayFieldOfCommandToString(field, command)
                                                      : field.get(command))
                    .append(";");
        }

        System.out.println("command: '"+serializedCommand+"'");
        return serializedCommand.toString();
    }

    public static ArrayList<Executable> deserialize(String serializedCommands) {
        ArrayList<Executable> executableCommands = new ArrayList<>();

        for (String stringCommand : serializedCommands.split(COMMAND_DELIMITER)) {
            stringCommand = stringCommand.trim();
            if ("".equals(stringCommand))
                continue;

            HashMap<String, String> params = getParamsOf(stringCommand);
            int id = shiftIdFrom(params);
            Executable command = getCommandBy(id);
            setParamsToCommand(params, command);

            executableCommands.add(command);
        }

        return executableCommands;
    }

    private static String arrayFieldOfCommandToString(Field field, Serializable command) throws ClassNotFoundException, IllegalAccessException {
        Class arrayClass = Class.forName(field.getType().getName());
        Object[] array = castObjectToArray(arrayClass.cast(field.get(command)));

        return Arrays.asList(array).toString();
    }

    private static Object[] castObjectToArray(Object array) {
        Object[] castedArray = new Object[Array.getLength(array)];

        for(int i = 0; i < castedArray.length; i++) {
            castedArray[i] = Array.get(array, i);
        }

        return castedArray;
    }

    private static HashMap<String, String> getParamsOf(String command) {
        HashMap<String, String> params = new HashMap<>();
        String[] pairs = command.split(ARGUMENTS_DELIMITER);

        for (String pair: pairs) {
            if (pair.contains(VALUE_DELIMITER)) {
                String key = pair.substring(0, pair.indexOf(VALUE_DELIMITER));
                String value = pair.substring(pair.indexOf(VALUE_DELIMITER) + VALUE_DELIMITER.length());
                params.put(key, value);
            }
        }

        return params;
    }

    private static int shiftIdFrom(HashMap<String, String> params) {
        int id;

        try {
            id = Integer.parseInt(params.get("id"));
        }
        catch (NumberFormatException e) {
            id = Command.INVALID;
        }

        params.remove("id");
        return id;
    }

    //TODO: server factory
    private static Executable getCommandBy(int id) {
        switch (id) {
            case Command.REGISTER:
                return new RegisterUserCommand();

            case Command.MESSAGE:
                return new SendMessageCommand();

            case Command.HISTORY:
                return new ShowMessageHistoryCommand();

            case Command.COMMANDS:
                return new ShowCommandListCommand();

            case Command.TOTAL:
                return new ShowClientsNumCommand();

            case Command.EXIT:
                return new ExitCommand();

            default:
                return new UnknownCommand();
        }
    }

    private static void setParamsToCommand(HashMap<String, String> params, Executable command) {
        Class<?> commandClass = command.getClass();

        for (Map.Entry<String, String> pair : params.entrySet()) {
            String name = pair.getKey();
            String value = pair.getValue();

            try {
                Field field = commandClass.getDeclaredField(name);
                Object castedValue = Parser.parseString(value, field.getType());
                field.setAccessible(true);
                field.set(command, castedValue);
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }
}
