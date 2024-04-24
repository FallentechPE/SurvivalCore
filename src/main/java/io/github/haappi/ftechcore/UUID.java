package io.github.haappi.ftechcore;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class UUID extends Command {
    protected UUID() {
        super("uuid");
    }

    @Override
    public boolean execute(@NotNull CommandSender commandSender, @NotNull String s, @NotNull String[] strings) {
        if (!(commandSender instanceof Player)) {
            return false;
        }
        commandSender.sendMessage(((Player) commandSender).getUniqueId().toString());
        return true;
    }
}
