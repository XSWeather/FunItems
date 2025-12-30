package dev.yooproject.funitems.util;

public class DebugUtil {

    // Глобальный флаг включения/выключения debug
    private static boolean enabled = true;

    /**
     * Включить или отключить вывод debug
     * @param flag true = включить, false = отключить
     */
    public static void setEnabled(boolean flag) {
        enabled = flag;
    }

    /**
     * Проверка включен ли debug
     * @return true если включен
     */
    public static boolean isEnabled() {
        return enabled;
    }

    /**
     * Логирование обычного debug-сообщения
     * @param message текст сообщения
     */
    public static void log(String message) {
        if (!enabled) return;
        System.out.println("[DEBUG] " + message);
    }

    /**
     * Логирование warning/ошибки
     * @param message текст сообщения
     */
    public static void error(String message) {
        if (!enabled) return;
        System.err.println("[DEBUG][ERROR] " + message);
    }

    /**
     * Вывод стека исключения
     * @param e исключение
     */
    public static void exception(Exception e) {
        if (!enabled) return;
        System.err.println("[DEBUG][EXCEPTION] " + e.getClass().getSimpleName() + ": " + e.getMessage());
        e.printStackTrace(System.err);
    }

    /**
     * Логирование с указанием источника (класс/метод)
     * @param source название источника
     * @param message сообщение
     */
    public static void log(String source, String message) {
        if (!enabled) return;
        System.out.println("[DEBUG][" + source + "] " + message);
    }

    /**
     * Логирование ошибки с указанием источника
     * @param source название источника
     * @param message сообщение
     */
    public static void error(String source, String message) {
        if (!enabled) return;
        System.err.println("[DEBUG][ERROR][" + source + "] " + message);
    }

    /**
     * Вывод исключения с указанием источника
     * @param source название источника
     * @param e исключение
     */
    public static void exception(String source, Exception e) {
        if (!enabled) return;
        System.err.println("[DEBUG][EXCEPTION][" + source + "] " + e.getClass().getSimpleName() + ": " + e.getMessage());
        e.printStackTrace(System.err);
    }
}
