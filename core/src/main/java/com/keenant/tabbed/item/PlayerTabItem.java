package com.keenant.tabbed.item;

import com.keenant.tabbed.util.Reflection;
import com.keenant.tabbed.util.Skin;
import com.keenant.tabbed.util.Skins;
import lombok.Getter;
import lombok.ToString;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

/**
 * A tab item that represents a player.
 */
@ToString
public final class PlayerTabItem implements TabItem {
    private static final PlayerProvider<String> NAME_PROVIDER = HumanEntity::getName;
    private static final PlayerProvider<String> DISPLAY_NAME_PROVIDER = Player::getDisplayName;
    private static final PlayerProvider<String> LIST_NAME_PROVIDER = Player::getPlayerListName;
    private static final PlayerProvider<Skin> SKIN_PROVIDER = Skins::getPlayer;
    @Getter
    private final Player player;
    @Getter
    private final PlayerProvider<String> textProvider;
    @Getter
    private final PlayerProvider<Skin> skinProvider;
    @Getter
    private String text;
    @Getter
    private int ping;
    @Getter
    private Skin skin;

    public PlayerTabItem(Player player, PlayerProvider<String> textProvider, PlayerProvider<Skin> skinProvider) {
        this.player = player;
        this.textProvider = textProvider;
        this.skinProvider = skinProvider;
        this.text = textProvider.get(player);
        this.ping = getNewPing();
        this.skin = skinProvider.get(player);
        updateText();
        updatePing();
        updateSkin();
    }

    public PlayerTabItem(Player player, PlayerProvider<String> textProvider) {
        this(player, textProvider, SKIN_PROVIDER);
    }

    public PlayerTabItem(Player player) {
        this(player, LIST_NAME_PROVIDER);
    }

    @Override
    public boolean updateText() {
        if (!this.player.isOnline() || !this.player.isValid())
            return false;

        String newText = this.textProvider.get(this.player);
        boolean update = !newText.equals(this.text);
        this.text = newText;
        return update;
    }

    @Override
    public boolean updatePing() {
        if (!this.player.isOnline() || !this.player.isValid())
            return false;

        int newPing = getNewPing();
        boolean update = newPing != ping;
        this.ping = newPing;
        return update;
    }

    @Override
    public boolean updateSkin() {
        if (!this.player.isOnline() || !this.player.isValid())
            return false;

        Skin newSkin = this.skinProvider.get(this.player);
        boolean update = !newSkin.equals(this.skin);
        this.skin = newSkin;
        return update;
    }

    private int getNewPing() {
        try {
            final Object entityPlayer = Reflection.getHandle(this.player);
            return entityPlayer.getClass().getDeclaredField("ping").getInt(entityPlayer);
        } catch (final Exception e) {
            throw new RuntimeException("couldn't get player ping", e);
        }
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof PlayerTabItem)) {
            return false;
        }
        PlayerTabItem other = (PlayerTabItem) object;
        return this.text.equals(other.getText()) && this.skin.equals(other.getSkin()) && this.ping == other.getPing();
    }

    public interface PlayerProvider<T> {
        T get(final Player player);
    }
}
