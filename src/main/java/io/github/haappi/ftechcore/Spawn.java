package io.github.haappi.ftechcore;

import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class Spawn extends Command {
    protected Spawn() {
        super("spawn");
    }

    @Override
    public boolean execute(@NotNull CommandSender commandSender, @NotNull String s, @NotNull String[] strings) {
        if (!(commandSender instanceof Player)) {
            return false;
        }
        ((Player) commandSender).teleport(new Location(FTechCore.getInstance().getServer().getWorld("world"), 8, 76, 20));
        return true;
    }
}
