package com.keenant.tabbed.util;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Method;

/**
 * Reflection util.
 */
public final class Reflection {

    public static final String VERSION = Bukkit.getServer().getClass().getPackage().getName().substring(24);
    public static final int INT_VER = Integer.parseInt(VERSION.split("_")[1]);
    public static final boolean IS_19_R2_PLUS = INT_VER > 18 && !VERSION.equals("1_19_R1");
    private static final Method GET_HANDLE;

    static {
        try {
            final Class<?> craftPlayer = Class.forName(Bukkit.getServer().getClass().getPackage().getName() + ".entity.CraftPlayer");
            GET_HANDLE = craftPlayer.getMethod("getHandle");
        } catch (final Exception e) {
            throw new RuntimeException(e);
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
