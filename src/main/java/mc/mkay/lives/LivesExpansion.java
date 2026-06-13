package mc.mkay.lives;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class LivesExpansion extends PlaceholderExpansion {

    private final LivesPlugin plugin;

    public LivesExpansion(LivesPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getIdentifier() { return "lives"; }

    @Override
    public @NotNull String getAuthor() { return "mkaymc"; }

    @Override
    public @NotNull String getVersion() { return plugin.getDescription().getVersion(); }

    @Override
    public boolean persist() { return true; }

    @Override
    public String onRequest(OfflinePlayer offlinePlayer, @NotNull String params) {
        LivesManager manager = plugin.getManager();

        // %lives_remaining% — lives of the viewer/player
        if (params.equals("remaining")) {
            if (offlinePlayer instanceof Player player) {
                return String.valueOf(manager.getLives(player));
            }
            return "0";
        }

        // %lives_remaining_<playername>% — lives of a specific named player
        if (params.startsWith("remaining_")) {
            String targetName = params.substring("remaining_".length());
            Player target = Bukkit.getPlayerExact(targetName);
            if (target != null) {
                return String.valueOf(manager.getLives(target));
            }
            return "?";
        }

        // %lives_hearts% — heart symbols based on lives (for NPC display)
        if (params.equals("hearts")) {
            if (offlinePlayer instanceof Player player) {
                int lives = manager.getLives(player);
                return "❤".repeat(Math.max(0, lives));
            }
            return "";
        }

        // %lives_color% — color based on lives for styling NPC text
        if (params.equals("color")) {
            if (offlinePlayer instanceof Player player) {
                int lives = manager.getLives(player);
                if (lives >= 3) return "<green>";
                if (lives == 2) return "<yellow>";
                if (lives == 1) return "<red>";
                return "<dark_red>";
            }
            return "<gray>";
        }

        return null;
    }
}
