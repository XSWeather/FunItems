package dev.yooproject.funitems.configuration.folder;

import java.io.File;

/**
 * Интерфейс для работы с папками
 */
public interface Folder {

    /**
     * Получить объект папки.
     * @return папка
     */
    File getFolder();

    /**
     * Создать папку, если её нет.
     */
    void create();
}
