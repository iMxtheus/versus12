package me.robomonkey.versus.duel.command;

import me.robomonkey.versus.Versus;
import me.robomonkey.versus.command.AbstractCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class OneVOneCancelCommand extends AbstractCommand {

    public OneVOneCancelCommand() {
        super("1v1cancel", "versus.duel");

        if (Versus.getInstance().getCommand("1v1cancel") != null) {
            Versus.getInstance().getCommand("1v1cancel").setExecutor((sender, command, label, args) -> {
                callCommand(sender, args);
                return true;
            });
        }

        setPlayersOnly(true);
        setPermissionRequired(false);
        setArgumentRequired(false);
        setUsage("/1v1cancel");
        setDescription("Leave the 1v1 matchmaking queue.");
    }

    @Override
    public void callCommand(CommandSender sender, String[] args) {

        if (!(sender instanceof Player player)) return;

        if (OneVOneCommand.cancelQueue(player)) {
            player.sendMessage("§cYou left the queue. Come back anytime to duel!");
        } else {
            player.sendMessage("§cYou are not currently in the 1v1 queue");
        }

    }

    @Override
    public List<String> callCompletionsUpdate(CommandSender sender, String[] args) {
        return null;
    }
}