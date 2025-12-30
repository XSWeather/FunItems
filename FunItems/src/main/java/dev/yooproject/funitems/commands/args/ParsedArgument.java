package dev.yooproject.funitems.commands.args;

public class ParsedArgument<T> {
    private final String raw;
    private final T value;

    public ParsedArgument(String raw, T value) {
        this.raw = raw;
        this.value = value;
    }

    public String getRaw() {
        return raw;
    }

    public T getValue() {
        return value;
    }
}
