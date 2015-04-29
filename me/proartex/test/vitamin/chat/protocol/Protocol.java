package me.proartex.test.vitamin.chat.protocol;

import me.proartex.test.vitamin.chat.*;
import me.proartex.test.vitamin.chat.client.commands.*;
import me.proartex.test.vitamin.chat.protocol.exceptions.SerializeException;
import me.proartex.test.vitamin.chat.server.commands.*;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.*;

//TODO: instance of interface + versionUID (?)
public class Protocol {

    public  static final String RESPONSE_DELIMITER  = "<@rd>";
    private static final String COMMAND_DELIMITER   = "<@SYS_COMMAND>";
    private static final String ARGUMENTS_DELIMITER = "<@ad>";
    private static final String VALUE_DELIMITER     = "@=";
    private static final List<String> FORBIDDEN_FIELDS;

    static {
        FORBIDDEN_FIELDS = new ArrayList<>();
        FORBIDDEN_FIELDS.add("handler");
        FORBIDDEN_FIELDS.add("key");
        FORBIDDEN_FIELDS.add("client");
    }

    //TODO: cut exception?
    public static String serialize(Serializable command) {
        try {
            return tryToSerialize(command);
        }
        catch (IllegalAccessException | ClassNotFoundException e) {
            e.printStackTrace();
            throw new SerializeException();
        }
    }

    public static List<Executable> deserialize(String serializedCommands, CommandPlacement placement) {
        List<Executable> executableCommands = new ArrayList<>();

        for (String stringCommand : Utils.explodeString(serializedCommands, COMMAND_DELIMITER)) {
            stringCommand = stringCommand.trim();

            if (stringCommand.isEmpty())
                continue;

            HashMap<String, String> params = getParamsOf(stringCommand);
            int id = shiftIdFrom(params);
            Executable command = getCommandFor(placement, id);
            setParamsToCommand(params, command);

            executableCommands.add(command);
        }

        return executableCommands;
    }

    private static String tryToSerialize(Serializable command) throws IllegalAccessException, ClassNotFoundException {
        StringBuilder serializedCommand = new StringBuilder(COMMAND_DELIMITER);
        Class cl = command.getClass();

        for (Field field : cl.getDeclaredFields()) {
            String name = field.getName();
            if (isForbidden(name))
                continue;

            field.setAccessible(true);
            serializedCommand
                    .append(field.getName())
                    .append(VALUE_DELIMITER)
                    .append(field.getType().isArray() ? arrayFieldOfCommandToString(field, command)
                            : field.get(command))
                    .append(ARGUMENTS_DELIMITER);
        }

        return serializedCommand.toString();
    }

    private static boolean isForbidden(String name) {
        return FORBIDDEN_FIELDS.contains(name);
    }

    private static String arrayFieldOfCommandToString(Field field, Serializable command) throws ClassNotFoundException, IllegalAccessException {
        Class arrayClass = Class.forName(field.getType().getName());
        Object[] array = castObjectToArray(arrayClass.cast(field.get(command)));

        return Arrays.asList(array).toString();
    }

    //TODO: utils
    private static Object[] castObjectToArray(Object array) {
        Object[] castedArray = new Object[Array.getLength(array)];

        for(int i = 0; i < castedArray.length; i++) {
            castedArray[i] = Array.get(array, i);
        }

        return castedArray;
    }

    private static HashMap<String, String> getParamsOf(String command) {
        HashMap<String, String> params = new HashMap<>();
        String[] pairs = Utils.explodeString(command, ARGUMENTS_DELIMITER);

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

    private static Executable getCommandFor(CommandPlacement placement, int id) {
        return placement == CommandPlacement.SERVER
               ? getServerCommandFor(id)
               : getClientCommandFor(id);
    }

    private static Executable getServerCommandFor(int id) {
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

    private static Executable getClientCommandFor(int id) {
        switch (id) {
            case Command.ACCEPT:
                return new AcceptCommand();

            case Command.INVALID_NAME:
                return new InvalidUsernameCommand();

            case Command.TAKEN_NAME:
                return new TakenUsernameCommand();

            case Command.SYSTEM_MESSAGE:
                return new SystemMessageCommand();

            case Command.USER_MESSAGE:
                return new UserMessageCommand();

            default:
                return new InvalidCommand();
        }
    }

    private static void setParamsToCommand(HashMap<String, String> params, Executable command) {
        Class<?> commandClass = command.getClass();

        for (Map.Entry<String, String> pair : params.entrySet()) {
            String name = pair.getKey();
            String value = pair.getValue();

            if (isForbidden(name))
                continue;

            try {
                Field field = commandClass.getDeclaredField(name);
                Object castedValue = Parser.parseString(value, field.getType());
                field.setAccessible(true);
                field.set(command, castedValue);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }
}
