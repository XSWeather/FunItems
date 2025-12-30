package dev.yooproject.funitems.commands.args;

public class ArgumentParser {

    @SuppressWarnings("unchecked")
    public static <T> ParsedArgument<T> parse(ArgumentType type, String arg) throws IllegalArgumentException {
        switch (type) {
            case STRING:
                return new ParsedArgument<>(arg, (T) arg);
            case INTEGER:
                try {
                    return new ParsedArgument<>(arg, (T) (Integer) Integer.parseInt(arg));
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Invalid integer: " + arg, e);
                }
            case DOUBLE:
                try {
                    return new ParsedArgument<>(arg, (T) (Double) Double.parseDouble(arg));
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Invalid double: " + arg, e);
                }
            case BOOLEAN:
                return new ParsedArgument<>(arg, (T) (Boolean) (arg.equalsIgnoreCase("true") || arg.equals("1")));
            default:
                throw new IllegalArgumentException("Unsupported argument type: " + type);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> ParsedArgument<T> parse(String arg, Class<T> clazz) throws IllegalArgumentException {
        if (clazz == String.class) return (ParsedArgument<T>) parse(ArgumentType.STRING, arg);
        if (clazz == Integer.class || clazz == int.class) return (ParsedArgument<T>) parse(ArgumentType.INTEGER, arg);
        if (clazz == Double.class || clazz == double.class) return (ParsedArgument<T>) parse(ArgumentType.DOUBLE, arg);
        if (clazz == Boolean.class || clazz == boolean.class) return (ParsedArgument<T>) parse(ArgumentType.BOOLEAN, arg);
        throw new IllegalArgumentException("Unsupported argument class: " + clazz.getSimpleName());
    }
}
