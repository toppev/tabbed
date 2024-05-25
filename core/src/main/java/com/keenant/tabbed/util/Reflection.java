package com.keenant.tabbed.util;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Method;

/**
 * Reflection util.
 */
public final class Reflection {


    public static final String VERSION_EXACT = Bukkit.getBukkitVersion().split("-")[0]; // e.g. 1.20.6
    public static final int INT_VER = Integer.parseInt(VERSION_EXACT.split("\\.")[1]); // e.g. 20
    public static final boolean IS_PAPER = findClass("com.destroystokyo.paper.PaperConfig", "io.papermc.paper.configuration.Configuration");

    public static final String VERSION = findVersion(); // e.g. 1_20_R4
    public static final boolean IS_19_R2_PLUS = INT_VER > 18 && !VERSION.equals("1_19_R1");
    public static final boolean IS_20_R4_PLUS = INT_VER > 19 && !"1_20_R1".equals(VERSION) && !"1_20_R2".equals(VERSION) && !"1_20_R3".equals(VERSION);
    private static final Method GET_HANDLE;

    static {
        try {
            final Class<?> craftPlayer;
            if (IS_PAPER && IS_20_R4_PLUS) {
                craftPlayer = Class.forName("org.bukkit.craftbukkit.entity.CraftPlayer");
            } else {
                craftPlayer = Class.forName("org.bukkit.craftbukkit.v" + VERSION + ".entity.CraftPlayer");
            }
            GET_HANDLE = craftPlayer.getMethod("getHandle");
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static String findVersion() {
        if (IS_PAPER && INT_VER >= 20) {
            switch (VERSION_EXACT) {
                case "1.20":
                case "1.20.1":
                    return "1_20_R1";
                case "1.20.2":
                case "1.20.3":
                    return "1_20_R2";
                case "1.20.4":
                    return "1_20_R3";
                case "1.20.5":
                case "1.20.6":
                    return "1_20_R4";
                case "1.21":
                    return "1_21_R1";
                default:
                    Bukkit.getLogger().warning(
                            "[TABBED] Couldn't find NMS version for paper " + VERSION_EXACT + ", you can ignore this if everything works fine."
                    );
                    return "UNKNOWN";
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
    private static boolean findClass(final String... paths) {
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
