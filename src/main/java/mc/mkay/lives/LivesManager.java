package mc.mkay.lives;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class LivesManager {

    // Relic holders — hardcoded, add more UUIDs as needed
    // You can also just use the username check for simplicity
    private static final java.util.Set<String> RELIC_HOLDERS = java.util.Set.of(
        "mkaymc"
    );

    public static final int DEFAULT_LIVES = 3;
    public static final int RELIC_LIVES = 1;

    private final LivesPlugin plugin;
    private final Map<UUID, Integer> lives = new HashMap<>();
    private File dataFile;
    private FileConfiguration data;

    public LivesManager(LivesPlugin plugin) {
        this.plugin = plugin;
        dataFile = new File(plugin.getDataFolder(), "lives.yml");
        if (!plugin.getDataFolder().exists()) plugin.getDataFolder().mkdirs();
        if (!dataFile.exists()) {
            try { dataFile.createNewFile(); } catch (IOException e) { e.printStackTrace(); }
        }
        data = YamlConfiguration.loadConfiguration(dataFile);
        loadAll();
    }

    public int getLives(Player player) {
        return lives.getOrDefault(player.getUniqueId(), getDefaultLives(player));
    }

    public void setLives(Player player, int count) {
        lives.put(player.getUniqueId(), Math.max(0, count));
        save(player);
    }

    public void removeLife(Player player) {
        int current = getLives(player);
        setLives(player, current - 1);
    }

    public boolean isDead(Player player) {
        return getLives(player) <= 0;
    }

    public boolean isRelicHolder(Player player) {
        return RELIC_HOLDERS.contains(player.getName().toLowerCase());
    }

    public int getDefaultLives(Player player) {
        return isRelicHolder(player) ? RELIC_LIVES : DEFAULT_LIVES;
    }

    public void initPlayer(Player player) {
        if (!lives.containsKey(player.getUniqueId())) {
            lives.put(player.getUniqueId(), getDefaultLives(player));
            save(player);
        }
    }

    private void save(Player player) {
        data.set(player.getUniqueId().toString(), lives.get(player.getUniqueId()));
        try { data.save(dataFile); } catch (IOException e) { e.printStackTrace(); }
    }

    public void saveAll() {
        for (Map.Entry<UUID, Integer> entry : lives.entrySet()) {
            data.set(entry.getKey().toString(), entry.getValue());
        }
        try { data.save(dataFile); } catch (IOException e) { e.printStackTrace(); }
    }

    private void loadAll() {
        if (data.getKeys(false).isEmpty()) return;
        for (String key : data.getKeys(false)) {
            try {
                lives.put(UUID.fromString(key), data.getInt(key));
            } catch (Exception ignored) {}
        }
    }

    // Add relic holder by name (for ops)
    public void addRelicHolder(String name) {
        // stored in config for persistence
        plugin.getConfig().getStringList("relic-holders").add(name.toLowerCase());
        plugin.saveConfig();
    }
}
