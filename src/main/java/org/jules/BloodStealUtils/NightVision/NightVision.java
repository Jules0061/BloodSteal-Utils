package org.jules.BloodStealUtils.NightVision;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityResurrectEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;
import org.jspecify.annotations.NonNull;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class NightVision implements Listener, CommandExecutor {

    private final JavaPlugin plugin;
    private final MiniMessage mm = MiniMessage.miniMessage();

    private final Set<UUID> enabled = ConcurrentHashMap.newKeySet();

    private final Map<UUID, BukkitTask> tasks = new ConcurrentHashMap<>();

    private Component enabledMsg;
    private Component disabledMsg;
    private Component actionBarEnabled;
    private Component actionBarDisabled;

    private int duration;
    private int amplifier;
    private int reapplyTicks;

    public NightVision(JavaPlugin plugin) {
        this.plugin = plugin;

        plugin.saveDefaultConfig();
        loadConfig();
    }

    private void loadConfig() {
        FileConfiguration config = plugin.getConfig();

        enabledMsg = mm.deserialize(config.getString(
                "messages.enabled",
                "<gray>Night Vision enabled."
        ));

        disabledMsg = mm.deserialize(config.getString(
                "messages.disabled",
                "<gray>Night Vision disabled!!"
        ));

        actionBarEnabled = mm.deserialize(config.getString(
                "messages.action-bar-enabled",
                "<gray>Night Vision enabled."
        ));

        actionBarDisabled = mm.deserialize(config.getString(
                "messages.action-bar-disabled",
                "<gray>Night Vision disabled!!"
        ));

        duration = config.getInt("effect.duration-seconds", 16) * 20;
        amplifier = config.getInt("effect.amplifier", 0);
        reapplyTicks = config.getInt("effect.reapply-ticks", 200);
    }


    @Override
    public boolean onCommand(@NonNull CommandSender sender, @NonNull Command command, @NonNull String label, String @NonNull [] args) {

        if (!(sender instanceof Player p)) {
            sender.sendMessage(Component.text("Only players can use this command."));
            return true;
        }

        UUID uuid = p.getUniqueId();

        if (enabled.contains(uuid)) {
            disable(p);
        } else {
            enable(p);
        }

        return true;
    }


    private void enable(Player p) {
        UUID uuid = p.getUniqueId();

        enabled.add(uuid);
        applyEffect(p);

        p.sendMessage(enabledMsg);
        p.sendActionBar(actionBarEnabled);

        startTask(p);
    }

    private void disable(Player p) {
        UUID uuid = p.getUniqueId();

        enabled.remove(uuid);
        stopTask(uuid);

        p.removePotionEffect(PotionEffectType.NIGHT_VISION);
        p.sendMessage(disabledMsg);
        p.sendActionBar(actionBarDisabled);
    }

    private void applyEffect(Player p) {
        p.addPotionEffect(new PotionEffect(
                PotionEffectType.NIGHT_VISION,
                duration,
                amplifier,
                false,
                false,
                false
        ));
    }

    private void startTask(Player p) {
        UUID uuid = p.getUniqueId();

        stopTask(uuid);

        BukkitTask task = Bukkit.getScheduler().runTaskTimer(
                plugin,
                () -> {
                    if (!p.isOnline() || !enabled.contains(uuid)) {
                        stopTask(uuid);
                        return;
                    }
                    applyEffect(p);
                },
                reapplyTicks,
                reapplyTicks
        );

        tasks.put(uuid, task);
    }

    private void stopTask(UUID uuid) {
        BukkitTask task = tasks.remove(uuid);
        if (task != null) task.cancel();
    }


    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        UUID uuid = p.getUniqueId();

        if (enabled.contains(uuid)) {
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                if (p.isOnline()) {
                    enable(p);
                }
            }, 20L);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        stopTask(e.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent e) {
        Player p = e.getPlayer();
        UUID uuid = p.getUniqueId();

        if (enabled.contains(uuid)) {
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                if (p.isOnline()) {
                    applyEffect(p);
                    startTask(p);
                }
            }, 2L);
        }
    }

    @EventHandler
    public void onTotem(EntityResurrectEvent e) {
        if (!(e.getEntity() instanceof Player p)) return;

        UUID uuid = p.getUniqueId();

        if (enabled.contains(uuid)) {
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                if (p.isOnline()) {
                    applyEffect(p);
                }
            }, 2L);
        }
    }
}