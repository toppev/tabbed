package com.keenant.tabbed.util;

import com.comphenix.protocol.PacketType.Play.Server;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers.PlayerInfoAction;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Some generic-ish packet utils.
 */
public final class Packets {

    /**
     * Creates a PLAYER_INFO packet from the params.
     */
    public static PacketContainer getPacket(PlayerInfoAction action, PlayerInfoData data) {
        return getPacket(action, Collections.singletonList(data));
    }

    /**
     * Creates a PLAYER_INFO packet from the params.
     */
    public static PacketContainer getPacket(PlayerInfoAction action, List<PlayerInfoData> data) {
        PacketContainer packet = ProtocolLibrary.getProtocolManager().createPacket(Server.PLAYER_INFO);
        // not sure if only 1.19.2+ or not, whatever... too lazy to check
        if (Reflection.IS_19_R2_PLUS) {
            Set<PlayerInfoAction> set = new HashSet<>(1);
            set.add(action);
            packet.getPlayerInfoActions().write(0, set);
        } else {
            packet.getPlayerInfoAction().write(0, action);
        }
        packet.getPlayerInfoDataLists().write(1, data);
        return packet;
    }

    /**
     * Creates a PLAYER_INFO_REMOVE packet.
     */
    public static PacketContainer getRemovePacket(List<PlayerInfoData> removedPlayers) {
        PacketContainer packet;
        if (Reflection.IS_19_R2_PLUS) {
            packet = ProtocolLibrary.getProtocolManager().createPacket(Server.PLAYER_INFO_REMOVE);
            packet.getUUIDLists().write(
                    0,
                    removedPlayers.stream().map(PlayerInfoData::getProfileId).collect(Collectors.toList())
            );
        } else {
            packet = ProtocolLibrary.getProtocolManager().createPacket(Server.PLAYER_INFO);
            //noinspection deprecation
            packet.getPlayerInfoAction().write(0, PlayerInfoAction.REMOVE_PLAYER);
            packet.getPlayerInfoDataLists().write(0, removedPlayers);
        }
        return packet;
    }

    /**
     * Sends a list of ProtocolLib packets to a player.
     */
    public static void send(Player player, List<PacketContainer> packets) {
        for (PacketContainer packet : packets)
            ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet, false);
    }

}
