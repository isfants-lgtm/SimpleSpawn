package com.exemplo.simplespawn;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class SimpleSpawn extends JavaPlugin implements CommandExecutor {

    @Override
    public void onEnable() {
        saveDefaultConfig();
        getCommand("setspawn").setExecutor(this);
        getCommand("spawn").setExecutor(this);
        getLogger().info("SimpleSpawn ativado!");
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage("Apenas jogadores podem usar este comando.");
            return true;
        }

        if (command.getName().equalsIgnoreCase("setspawn")) {
            return handleSetSpawn(player);
        }

        if (command.getName().equalsIgnoreCase("spawn")) {
            return handleSpawn(player);
        }

        return false;
    }

    private boolean handleSetSpawn(Player player) {
        Location loc = player.getLocation();

        getConfig().set("spawn.world", loc.getWorld().getName());
        getConfig().set("spawn.x", loc.getX());
        getConfig().set("spawn.y", loc.getY());
        getConfig().set("spawn.z", loc.getZ());
        getConfig().set("spawn.yaw", loc.getYaw());
        getConfig().set("spawn.pitch", loc.getPitch());
        saveConfig();

        player.sendMessage(getConfig().getString("mensagens.spawn-definido",
                "&aSpawn definido com sucesso!").replace('&', '§'));
        return true;
    }

    private boolean handleSpawn(Player player) {
        if (!getConfig().contains("spawn.world")) {
            player.sendMessage(getConfig().getString("mensagens.sem-spawn",
                    "&cO spawn ainda não foi definido.").replace('&', '§'));
            return true;
        }

        World world = Bukkit.getWorld(getConfig().getString("spawn.world"));
        if (world == null) {
            player.sendMessage(getConfig().getString("mensagens.mundo-invalido",
                    "&cO mundo do spawn não existe mais.").replace('&', '§'));
            return true;
        }

        Location spawn = new Location(
                world,
                getConfig().getDouble("spawn.x"),
                getConfig().getDouble("spawn.y"),
                getConfig().getDouble("spawn.z"),
                (float) getConfig().getDouble("spawn.yaw"),
                (float) getConfig().getDouble("spawn.pitch")
        );

        player.teleportAsync(spawn).thenAccept(success -> {
            if (success) {
                player.sendMessage(getConfig().getString("mensagens.teleportado",
                        "&aTeleportado para o spawn!").replace('&', '§'));
            }
        });
        return true;
    }
}
