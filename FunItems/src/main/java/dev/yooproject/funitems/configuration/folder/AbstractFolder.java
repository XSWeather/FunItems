package dev.yooproject.funitems.configuration.folder;

import dev.yooproject.funitems.FunItems;

import java.io.File;

/**
 * Базовая реализация папки
 */
public abstract class AbstractFolder implements Folder {

    private final File folder;

    public AbstractFolder(String folderName) {
        this.folder = new File(FunItems.getInstance().getDataFolder(), folderName);
        create();
    }

    @Override
    public File getFolder() {
        return folder;
    }

    @Override
    public void create() {
        if (!folder.exists() && !folder.mkdirs()) {
            FunItems.getInstance().getLogger().warning("Failed to create folder: " + folder.getAbsolutePath());
        }
    }
}
