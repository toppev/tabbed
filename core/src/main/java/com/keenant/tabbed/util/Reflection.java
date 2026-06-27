package com.keenant.tabbed.util;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Method;

public class Reflection {

    public static final String VERSION_EXACT;
    public static final String NMS;

    public static final boolean IS_PAPER;
    public static final boolean IS_FOLIA;

    public static final int MAJOR;
    public static final int MINOR;
    public static final int PATCH;

    private static final Method GET_HANDLE;

    static {
        VERSION_EXACT = Bukkit.getBukkitVersion()
                .split("-")[0]
                .replaceAll("\\.build\\.\\d+", "");

        IS_PAPER = findClass(
                "com.destroystokyo.paper.PaperConfig",
                "io.papermc.paper.configuration.Configuration"
        );

        IS_FOLIA = findClass("io.papermc.paper.threadedregions.RegionizedServer");

        String[] versions = VERSION_EXACT.split("\\.");
        MAJOR = Integer.parseInt(versions[0]);
        MINOR = Integer.parseInt(versions[1]);
        PATCH = versions.length > 2 ? Integer.parseInt(versions[2]) : 0;

        NMS = findVersion();

        try {
            final Class<?> craftPlayer;
            if (IS_PAPER && isOrOver(1, 20, 5)) {
                craftPlayer = Class.forName("org.bukkit.craftbukkit.entity.CraftPlayer");
            } else {
                craftPlayer = Class.forName("org.bukkit.craftbukkit.v" + NMS + ".entity.CraftPlayer");
            }
            GET_HANDLE = craftPlayer.getMethod("getHandle");
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean is(int major, int minor, int patch) {
        return MAJOR == major && MINOR == minor && PATCH == patch;
    }

    public static boolean isOver(int major, int minor, int patch) {
        if (MAJOR > major) return true;
        if (MAJOR == major && MINOR > minor) return true;
        if (MAJOR == major && MINOR == minor) return PATCH > patch;
        return false;
    }

    public static boolean isOrOver(int major, int minor, int patch) {
        return isOver(major, minor, patch) || is(major, minor, patch);
    }

    public static boolean isBelow(int major, int minor, int patch) {
        if (MAJOR < major) return true;
        if (MAJOR == major && MINOR < minor) return true;
        if (MAJOR == major && MINOR == minor) return PATCH < patch;
        return false;
    }

    private static String findVersion() {
        // In 26.1+ Spigot uses Mojang mappings and does not relocate
        // org.craftbukkit package anymore. needs to be handled same as
        // older paper versions when they did that out of nowhere
        if (MAJOR >= 26 || (IS_PAPER && (MAJOR > 1 || MINOR >= 20))) {
            switch (VERSION_EXACT) {
                case "1.20":
                case "1.20.1": return "1_20_R1";
                case "1.20.2":
                case "1.20.3": return "1_20_R2";
                case "1.20.4": return "1_20_R3";
                case "1.20.5":
                case "1.20.6": return "1_20_R4";
                case "1.21":
                case "1.21.1": return "1_21_R1";
                default: return "UNKNOWN";
            }
        }
        return Bukkit.getServer().getClass().getPackage().getName().substring(24);
    }

    /**
     * Finds any {@link Class} of the provided paths
     *
     * @param paths all possible class paths
     * @return false if the {@link Class} was NOT found
     */
    public static boolean findClass(final String... paths) {
        for (final String path : paths) {
            if (getClass(path) != null) return true;
        }
        return false;
    }

    /**
     * A nullable {@link Class#forName(String)} instead of throwing exceptions
     *
     * @return null if the {@link Class} was NOT found
     */
    public static Class<?> getClass(final String path) {
        try {
            return Class.forName(path);
        } catch (final Exception ignored) {
            return null;
        }
    }

    public static Object getHandle(final Player player) {
        try {
            return GET_HANDLE.invoke(player);
        } catch (final Exception e) {
            throw new RuntimeException("Couldn't get EntityPlayer", e);
        }
    }
}
