package mc.mkay.lives;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class LivesPlugin extends JavaPlugin {

    private static LivesPlugin instance;
    private LivesManager manager;

    @Override
    public void onEnable() {
        instance = this;
        manager = new LivesManager(this);
        getServer().getPluginManager().registerEvents(new LivesListener(this), this);
        getCommand("lives").setExecutor(new LivesCommand(this));
        getCommand("setlives").setExecutor(new LivesCommand(this));
        getCommand("resetlives").setExecutor(new LivesCommand(this));

        // Register PlaceholderAPI expansion if available
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new LivesExpansion(this).register();
            getLogger().info("[Lives] PlaceholderAPI expansion registered. Use %lives_remaining% on NPCs.");
        }

        getLogger().info("[Lives] Plugin enabled.");
    }

    @Override
    public void onDisable() {
        if (manager != null) manager.saveAll();
        getLogger().info("[Lives] Plugin disabled.");
    }

    public static LivesPlugin getInstance() { return instance; }
    public LivesManager getManager() { return manager; }
}
