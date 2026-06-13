package mc.mkay.lives;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class LivesListener implements Listener {

    private final LivesPlugin plugin;

    public LivesListener(LivesPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        plugin.getManager().initPlayer(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getPlayer();
        LivesManager manager = plugin.getManager();

        manager.removeLife(player);
        int remaining = manager.getLives(player);

        // Tell only the player their remaining lives — silent to everyone else
        if (remaining > 0) {
            // Delayed so it shows after respawn screen
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (player.isOnline()) {
                        player.sendMessage(
                            Component.text("You have ", NamedTextColor.GRAY)
                                .append(Component.text(remaining + " ", NamedTextColor.RED))
                                .append(Component.text(remaining == 1 ? "life" : "lives", NamedTextColor.GRAY))
                                .append(Component.text(" remaining.", NamedTextColor.GRAY))
                        );
                    }
                }
            }.runTaskLater(plugin, 20L);
        }
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        LivesManager manager = plugin.getManager();

        // Check if they're out of lives — kick after respawn
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!player.isOnline()) return;
                if (manager.isDead(player)) {
                    player.kick(
                        Component.text("\n")
                            .append(Component.text("Your story ends here.", NamedTextColor.DARK_PURPLE))
                            .append(Component.text("\n\n"))
                            .append(Component.text("You have no lives remaining.", NamedTextColor.GRAY))
                    );
                }
            }
        }.runTaskLater(plugin, 5L);
    }
}
