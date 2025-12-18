package org.jules.BloodStealUtils.FreeStuff;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NonNull;
import org.jules.BloodStealUtils.Main;

import java.util.HashMap;
import java.util.UUID;

public class FreeFood implements CommandExecutor {

    private final Main plugin;
    private final MiniMessage miniMessage = MiniMessage.miniMessage();
    private final HashMap<UUID, Long> cooldowns = new HashMap<>();

    public FreeFood(Main plugin) {
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

        String cooldownConfig = plugin.getConfig().getString("FreeFood.cooldown-time", "1h");
        long cooldownTime = plugin.parseTime(cooldownConfig);

        if (cooldownTime == 0) {
            cooldownTime = 60L * 60 * 1000; // Default 1 hour
        }

        if (cooldowns.containsKey(uuid) && now - cooldowns.get(uuid) < cooldownTime) {
            long remaining = cooldownTime - (now - cooldowns.get(uuid));
            long minutes = (remaining / (1000 * 60)) % 60;
            long seconds = (remaining / 1000) % 60;

            String cooldownMsg = plugin.getConfig().getString("FreeFood.cooldown");
            if (cooldownMsg == null) {
                cooldownMsg = "<red>You must wait <minutes> minutes and <seconds> seconds before using this command again.";
            }

            player.sendMessage(miniMessage.deserialize(
                    cooldownMsg
                            .replace("<minutes>", String.valueOf(minutes))
                            .replace("<seconds>", String.valueOf(seconds))
            ));
            return true;
        }

        ItemStack steak = new ItemStack(Material.COOKED_BEEF, 64);
        player.getInventory().addItem(steak);

        cooldowns.put(uuid, now);

        String chatMsg = plugin.getConfig().getString("FreeFood.message");
        if (chatMsg == null) {
            chatMsg = "<gray>+You received 64x Cooked Steak";
        }
        player.sendMessage(miniMessage.deserialize(chatMsg));

        String actionBarMsg = plugin.getConfig().getString("FreeFood.actionbar");
        if (actionBarMsg == null) {
            actionBarMsg = "<gold>You received 64x Cooked Steak";
        }
        player.sendActionBar(miniMessage.deserialize(actionBarMsg));

        return true;
    }
}