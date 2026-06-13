package mc.mkay.lives;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LivesCommand implements CommandExecutor {

    private final LivesPlugin plugin;

    public LivesCommand(LivesPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        LivesManager manager = plugin.getManager();
        String cmd = command.getName().toLowerCase();

        switch (cmd) {
            case "lives" -> {
                // Any player can check their own lives
                if (!(sender instanceof Player player)) {
                    sender.sendMessage("Only players can use this.");
                    return true;
                }
                int lives = manager.getLives(player);
                player.sendMessage(
                    Component.text("You have ", NamedTextColor.GRAY)
                        .append(Component.text(lives + " ", NamedTextColor.RED))
                        .append(Component.text(lives == 1 ? "life" : "lives", NamedTextColor.GRAY))
                        .append(Component.text(" remaining.", NamedTextColor.GRAY))
                );
            }

            case "setlives" -> {
                // Op only
                if (!sender.isOp()) {
                    sender.sendMessage(Component.text("No permission.", NamedTextColor.RED));
                    return true;
                }
                if (args.length < 2) {
                    sender.sendMessage(Component.text("Usage: /setlives <player> <amount>", NamedTextColor.RED));
                    return true;
                }
                Player target = Bukkit.getPlayer(args[0]);
                if (target == null) {
                    sender.sendMessage(Component.text("Player not found.", NamedTextColor.RED));
                    return true;
                }
                try {
                    int amount = Integer.parseInt(args[1]);
                    manager.setLives(target, amount);
                    sender.sendMessage(Component.text("Set " + target.getName() + "'s lives to " + amount, NamedTextColor.GREEN));
                } catch (NumberFormatException e) {
                    sender.sendMessage(Component.text("Invalid number.", NamedTextColor.RED));
                }
            }

            case "resetlives" -> {
                // Op only
                if (!sender.isOp()) {
                    sender.sendMessage(Component.text("No permission.", NamedTextColor.RED));
                    return true;
                }
                if (args.length < 1) {
                    sender.sendMessage(Component.text("Usage: /resetlives <player|all>", NamedTextColor.RED));
                    return true;
                }
                if (args[0].equalsIgnoreCase("all")) {
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        manager.setLives(p, manager.getDefaultLives(p));
                    }
                    sender.sendMessage(Component.text("Reset all online players' lives.", NamedTextColor.GREEN));
                } else {
                    Player target = Bukkit.getPlayer(args[0]);
                    if (target == null) {
                        sender.sendMessage(Component.text("Player not found.", NamedTextColor.RED));
                        return true;
                    }
                    manager.setLives(target, manager.getDefaultLives(target));
                    sender.sendMessage(Component.text("Reset " + target.getName() + "'s lives.", NamedTextColor.GREEN));
                }
            }
        }
        return true;
    }
}
