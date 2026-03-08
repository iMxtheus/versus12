package me.robomonkey.versus.duel.command;

import me.robomonkey.versus.command.RootCommand;
import org.bukkit.command.CommandSender;

import java.util.List;

public class Root1v1Command extends RootCommand {

    public Root1v1Command() {
        super("1v1", "versus.duel");
        setUsage("/1v1");
        setDescription("Main duel command.");
    }

    @Override
    public void callCommand(CommandSender sender, String[] args) {
        sender.sendMessage(getUsage());
    }

    @Override
    public List<String> callCompletionsUpdate(CommandSender sender, String[] args) {
        return null;
    }
}