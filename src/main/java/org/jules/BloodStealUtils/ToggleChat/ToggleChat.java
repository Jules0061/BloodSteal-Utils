package org.jules.BloodStealUtils.ToggleChat;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;
import org.jules.BloodStealUtils.Main;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class ToggleChat implements CommandExecutor, Listener {

    private final Main plugin;
    private final MiniMessage mm = MiniMessage.miniMessage();
    private final Set<UUID> chatDisabledPlayers = new HashSet<>();

    public ToggleChat(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cOnly players can use this command.");
            return true;
        }

        UUID playerId = player.getUniqueId();

        if (chatDisabledPlayers.contains(playerId)) {
            chatDisabledPlayers.remove(playerId);

            String enabledMessage = plugin.getConfig().getString("ToggleChat.Enabled", "<gray>Chat Enabled!");
            String actionBarEnabled = plugin.getConfig().getString("ToggleChat.ActionBar-Enabled", "<gray>Chat Enabled!");

            Component messageComponent = mm.deserialize(enabledMessage);
            Component actionBarComponent = mm.deserialize(actionBarEnabled);

            player.sendMessage(messageComponent);
            player.sendActionBar(actionBarComponent);
        } else {
            chatDisabledPlayers.add(playerId);

            String disabledMessage = plugin.getConfig().getString("ToggleChat.Disabled", "<red>Chat Disabled!");
            String actionBarDisabled = plugin.getConfig().getString("ToggleChat.ActionBar-Disabled", "<red>Chat Disabled!");

            Component messageComponent = mm.deserialize(disabledMessage);
            Component actionBarComponent = mm.deserialize(actionBarDisabled);

            player.sendMessage(messageComponent);
            player.sendActionBar(actionBarComponent);
        }

        return true;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerChat(AsyncChatEvent event) {
        Player sender = event.getPlayer();
        Set<Audience> viewers = event.viewers();

        viewers.removeIf(viewer -> {
            if (viewer instanceof Player recipient) {
                // Don't filter out the message sender so they can see their own messages
                if (recipient.getUniqueId().equals(sender.getUniqueId())) {
                    return false;
                }
                return chatDisabledPlayers.contains(recipient.getUniqueId());
            }
            return false;
        });
    }
}