package dev.yooproject.nms;

public final class NMSProvider {

    private static NMS instance;

    public static void init(String serverVersion) {
        NMSVersion version = NMSVersion.fromServerVersion(serverVersion);

        if (version == null) {
            throw new RuntimeException("Unsupported server version: " + serverVersion);
        }

        try {
            Class<?> clazz = Class.forName(version.getClassName());
            instance = (NMS) clazz.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Failed to load NMS for " + serverVersion, e);
        }
    }

    public static NMS get() {
        if (instance == null) {
            throw new IllegalStateException("NMSProvider not initialized");
        }
        return instance;
    }
}
