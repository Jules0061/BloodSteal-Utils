package org.jules.BloodStealUtils.PlayerPing;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;
import org.jules.BloodStealUtils.Main;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"unused", "RedundantSuppression"})
public class PlayerPing implements CommandExecutor, TabCompleter {

    private final Main plugin;
    private final MiniMessage mm = MiniMessage.miniMessage();

    public PlayerPing(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {

        if (args.length == 0) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage("§cConsole must specify a player: /ping <player>");
                return true;
            }

            int ping = player.getPing();

            String pingMessage = plugin.getConfig().getString("OwnPing.Message", "<gray>Your ping is <green>%player_ping%");
            String actionBarMessage = plugin.getConfig().getString("OwnPing.ActionBar", "<gray>Your ping is <green>%player_ping%");

            pingMessage = pingMessage.replace("%player_ping%", String.valueOf(ping));
            actionBarMessage = actionBarMessage.replace("%player_ping%", String.valueOf(ping));

            Component messageComponent = mm.deserialize(pingMessage);
            Component actionBarComponent = mm.deserialize(actionBarMessage);

            player.sendMessage(messageComponent);
            player.sendActionBar(actionBarComponent);

            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);

        if (target == null || !target.isOnline()) {
            String notFoundMessage = plugin.getConfig().getString("PlayerPing.PlayerNotFound", "<red>Player not found!");
            Component component = mm.deserialize(notFoundMessage);
            sender.sendMessage(component);
            return true;
        }

        int ping = target.getPing();

        String pingMessage = plugin.getConfig().getString("PlayerPing.Message", "<gray>%player_name%'s ping is <green>%player_ping%");
        pingMessage = pingMessage.replace("%player_name%", target.getName()).replace("%player_ping%", String.valueOf(ping));

        Component messageComponent = mm.deserialize(pingMessage);
        sender.sendMessage(messageComponent);

        if (sender instanceof Player senderPlayer) {
            String actionBarMessage = plugin.getConfig().getString("PlayerPing.ActionBar", "<gray>%player_name%'s ping is <green>%player_ping%");
            actionBarMessage = actionBarMessage.replace("%player_name%", target.getName()).replace("%player_ping%", String.valueOf(ping));

            Component actionBarComponent = mm.deserialize(actionBarMessage);
            senderPlayer.sendActionBar(actionBarComponent);
        }

        return true;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String @NonNull [] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            String input = args[0].toLowerCase();
            for (Player player : Bukkit.getOnlinePlayers()) {
                String playerName = player.getName();
                if (playerName.toLowerCase().startsWith(input)) {
                    completions.add(playerName);
                }
            }
        }

        return completions;
    }
}