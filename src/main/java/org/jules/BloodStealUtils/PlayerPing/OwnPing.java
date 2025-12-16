package org.jules.BloodStealUtils.PlayerPing;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jules.BloodStealUtils.Main;

@SuppressWarnings({"unused", "RedundantSuppression"})
public class OwnPing implements CommandExecutor {

    private final Main plugin;
    private final MiniMessage mm = MiniMessage.miniMessage();

    public OwnPing(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cOnly players can use this command.");
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
}