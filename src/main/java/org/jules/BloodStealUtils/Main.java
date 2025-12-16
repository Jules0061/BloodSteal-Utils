package org.jules.BloodStealUtils;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;
import org.jules.BloodStealUtils.NightVision.NightVision;
import org.jules.BloodStealUtils.PlayerPing.PlayerPing;
import org.jules.BloodStealUtils.ToggleChat.ToggleChat;

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

        // Register NightVision
        NightVision nightVision = new NightVision(this);
        getServer().getPluginManager().registerEvents(nightVision, this);
        Objects.requireNonNull(getCommand("nightvision")).setExecutor(nightVision);

        // Register ToggleChat
        ToggleChat toggleChat = new ToggleChat(this);
        getServer().getPluginManager().registerEvents(toggleChat, this);
        Objects.requireNonNull(getCommand("chattoggle")).setExecutor(toggleChat);

        // Register PlayerPing (handles both /ping and /ping <player>)
        PlayerPing playerPing = new PlayerPing(this);
        Objects.requireNonNull(getCommand("ping")).setExecutor(playerPing);
        Objects.requireNonNull(getCommand("ping")).setTabCompleter(playerPing);

        getLogger().info("BloodSteal-Utils enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("BloodSteal-Utils disabled!");
    }

    @Override
    public boolean onCommand(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String label,
            @NotNull String @NonNull [] args
    ) {

        if (!command.getName().equalsIgnoreCase("bloodsteal-utils")) {
            return false;
        }

        if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {

            if (!sender.hasPermission("bloodsteal.reload")) {
                sender.sendMessage("§cYou do not have permission to do this.");
                return true;
            }

            reloadConfig();
            sender.sendMessage("§aBloodSteal-Utils config reloaded.");
            return true;
        }

        sender.sendMessage("§cUsage: /bloodsteal-utils reload");
        return true;
    }
}