package dev.yooproject.nms;

import java.util.Arrays;

public enum NMSVersion {
    V1_16_R3("v1_16_R3", "dev.yooproject.V1_16_5"),
    V1_17_R1("v1_17_R1", "dev.yooproject.V1_17_1");

    private final String serverVersion;
    private final String className;

    NMSVersion(String serverVersion, String className) {
        this.serverVersion = serverVersion;
        this.className = className;
    }

    public String getClassName() {
        return className;
    }

    public static NMSVersion fromServerVersion(String version) {
        return Arrays.stream(values())
                .filter(v -> v.serverVersion.equals(version))
                .findFirst()
                .orElse(null);
    }
}