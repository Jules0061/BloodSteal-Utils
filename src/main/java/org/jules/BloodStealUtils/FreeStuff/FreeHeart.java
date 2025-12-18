package org.jules.BloodStealUtils.FreeStuff;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;
import org.jules.BloodStealUtils.Main;

import java.util.HashMap;
import java.util.UUID;

public class FreeHeart implements CommandExecutor {

    private final Main plugin;
    private final MiniMessage miniMessage = MiniMessage.miniMessage();
    private final HashMap<UUID, Long> cooldowns = new HashMap<>();

    public FreeHeart(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NonNull CommandSender sender, @NonNull Command command, @NonNull String label, String @NonNull [] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        UUID uuid = player.getUniqueId();
        long now = System.currentTimeMillis();

        String cooldownConfig = plugin.getConfig().getString("FreeHeart.cooldown-time", "7d");
        long cooldownTime = plugin.parseTime(cooldownConfig);

        if (cooldownTime == 0) {
            cooldownTime = 7L * 24 * 60 * 60 * 1000; // Default 7 days
        }

        if (cooldowns.containsKey(uuid) && now - cooldowns.get(uuid) < cooldownTime) {
            long remaining = cooldownTime - (now - cooldowns.get(uuid));
            long days = remaining / (1000 * 60 * 60 * 24);
            long hours = (remaining / (1000 * 60 * 60)) % 24;
            long minutes = (remaining / (1000 * 60)) % 60;

            String cooldownMsg = plugin.getConfig().getString("FreeHeart.cooldown");
            if (cooldownMsg == null) {
                cooldownMsg = "<red>You must wait <days> days, <hours> hours and <minutes> minutes before using this command again.";
            }

            player.sendMessage(miniMessage.deserialize(
                    cooldownMsg
                            .replace("<days>", String.valueOf(days))
                            .replace("<hours>", String.valueOf(hours))
                            .replace("<minutes>", String.valueOf(minutes))
            ));
            return true;
        }

        String consoleCommand = "lsz giveItem " + player.getName() + " defaultheart 1";
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), consoleCommand);

        cooldowns.put(uuid, now);

        String actionBarMsg = plugin.getConfig().getString("FreeHeart.actionbar");
        if (actionBarMsg == null) {
            actionBarMsg = "<gray>You received 1x Heart!";
        }
        player.sendActionBar(miniMessage.deserialize(actionBarMsg));

        return true;
    }
}