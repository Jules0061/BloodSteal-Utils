package org.jules.BloodStealUtils;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.plugin.java.JavaPlugin;
import org.jspecify.annotations.NonNull;
import org.jules.BloodStealUtils.NightVision.NightVision;
import org.jules.BloodStealUtils.PlayerPing.PlayerPing;
import org.jules.BloodStealUtils.ToggleChat.ToggleChat;
import org.jules.BloodStealUtils.FreeStuff.FreeHeart;
import org.jules.BloodStealUtils.FreeStuff.FreeFood;

import java.util.Objects;

public final class Main extends JavaPlugin {

    private static Main instance;

    @SuppressWarnings("unused")
    public static Main getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;

        saveDefaultConfig();

        NightVision nightVision = new NightVision(this);
        getServer().getPluginManager().registerEvents(nightVision, this);
        Objects.requireNonNull(getCommand("nightvision")).setExecutor(nightVision);

        ToggleChat toggleChat = new ToggleChat(this);
        getServer().getPluginManager().registerEvents(toggleChat, this);
        Objects.requireNonNull(getCommand("chattoggle")).setExecutor(toggleChat);

        PlayerPing playerPing = new PlayerPing(this);
        Objects.requireNonNull(getCommand("ping")).setExecutor(playerPing);
        Objects.requireNonNull(getCommand("ping")).setTabCompleter(playerPing);

        Objects.requireNonNull(getCommand("weekly")).setExecutor(new FreeHeart(this));
        Objects.requireNonNull(getCommand("freefood")).setExecutor(new FreeFood(this));

        getLogger().info("BloodSteal-Utils enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("BloodSteal-Utils disabled!");
    }

    @Override
    public boolean onCommand(
            org.bukkit.command.@NonNull CommandSender sender,
            org.bukkit.command.Command command,
            @NonNull String label,
            String @NonNull [] args
    ) {

        if (command.getName().equalsIgnoreCase("bloodsteal-utils")) {

            MiniMessage mm = MiniMessage.miniMessage();

            if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {

                if (!sender.hasPermission("bloodsteal.reload")) {
                    sender.sendMessage(mm.deserialize("<red>You do not have permission to do this."));
                    return true;
                }

                reloadConfig();
                sender.sendMessage(mm.deserialize("<green>BloodSteal-Utils config reloaded."));
                return true;
            }

            sender.sendMessage(mm.deserialize("<red>Usage: /bloodsteal-utils reload"));
            return true;
        }

        return false;
    }

    // Parse time string like "7d", "1h", "30m", "45s" to milliseconds
    public long parseTime(String timeStr) {
        if (timeStr == null || timeStr.isEmpty()) {
            return 0;
        }

        timeStr = timeStr.toLowerCase().trim();
        long total = 0;

        // Split by common delimiters or parse each segment
        String[] parts = timeStr.split("[,\\s]+");

        for (String part : parts) {
            part = part.trim();
            if (part.isEmpty()) continue;

            try {
                String numberPart;
                long multiplier;

                if (part.endsWith("d") && part.length() > 1) {
                    numberPart = part.substring(0, part.length() - 1);
                    multiplier = 24L * 60 * 60 * 1000;
                } else if (part.endsWith("h") && part.length() > 1) {
                    numberPart = part.substring(0, part.length() - 1);
                    multiplier = 60L * 60 * 1000;
                } else if (part.endsWith("m") && part.length() > 1) {
                    numberPart = part.substring(0, part.length() - 1);
                    multiplier = 60L * 1000;
                } else if (part.endsWith("s") && part.length() > 1) {
                    numberPart = part.substring(0, part.length() - 1);
                    multiplier = 1000L;
                } else {
                    continue;
                }

                long value = Long.parseLong(numberPart);
                total += value * multiplier;
            } catch (NumberFormatException e) {
                getLogger().warning("Invalid time format: " + part);
            }
        }

        return total;
    }
}