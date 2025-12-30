package dev.yooproject.funitems.services;

import dev.yooproject.funitems.FunItems;
import dev.yooproject.funitems.commands.command.ICommand;
import dev.yooproject.funitems.commands.exception.CommandException;
import dev.yooproject.funitems.commands.args.CommandArguments;
import org.bukkit.command.CommandSender;

import java.util.HashMap;
import java.util.Map;

public class CommandService {

    private final Map<String, ICommand> commands = new HashMap<>();

    public void registerCommand(ICommand command) {
        commands.put(command.getName().toLowerCase(), command);
        if (FunItems.getInstance().getCommand(command.getName()) != null) {
            FunItems.getInstance().getCommand(command.getName()).setExecutor((sender, bukkitCommand, label, args) -> {
                try {
                    command.execute(sender, new CommandArguments(args));
                } catch (Exception e) {
                    sender.sendMessage(e.getMessage());
                    e.printStackTrace();
                }
                return true;
            });
        } else {
            FunItems.getInstance().getLogger().warning("Command '" + command.getName() + "' not found in plugin.yml!");
        }
    }
}
