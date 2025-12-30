package dev.yooproject.funitems.commands.args;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class CommandArguments {

    private final List<String> args;

    public CommandArguments(String[] args) {
        this.args = Collections.unmodifiableList(Arrays.asList(args));
    }

    public String get(int index) {
        if (!has(index)) return null;
        return args.get(index);
    }

    public int size() {
        return args.size();
    }

    public List<String> getAll() {
        return args;
    }

    public boolean has(int index) {
        return index >= 0 && index < args.size();
    }

    public String getOrDefault(int index, String defaultValue) {
        return has(index) ? get(index) : defaultValue;
    }

    public Optional<Integer> getIntSafe(int index) {
        try {
            return Optional.of(getInt(index));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public int getInt(int index) throws NumberFormatException {
        return Integer.parseInt(get(index));
    }

    public int getIntOrDefault(int index, int defaultValue) {
        return getIntSafe(index).orElse(defaultValue);
    }

    public Optional<Double> getDoubleSafe(int index) {
        try {
            return Optional.of(getDouble(index));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public double getDouble(int index) throws NumberFormatException {
        return Double.parseDouble(get(index));
    }

    public double getDoubleOrDefault(int index, double defaultValue) {
        return getDoubleSafe(index).orElse(defaultValue);
    }

    public Optional<Boolean> getBooleanSafe(int index) {
        if (!has(index)) return Optional.empty();
        return Optional.of(getBoolean(index));
    }

    public boolean getBoolean(int index) {
        String value = get(index);
        return value != null && (value.equalsIgnoreCase("true") || value.equals("1"));
    }

    public boolean getBooleanOrDefault(int index, boolean defaultValue) {
        return getBooleanSafe(index).orElse(defaultValue);
    }

    /**
     * Получить все оставшиеся аргументы начиная с index
     */
    public String getRemaining(int index) {
        if (index >= size()) return "";
        return String.join(" ", args.subList(index, size()));
    }

    @Override
    public String toString() {
        return args.toString();
    }
}