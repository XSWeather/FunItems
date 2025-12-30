package dev.yooproject.funitems.commands.command;

import dev.yooproject.funitems.commands.args.CommandArguments;
import dev.yooproject.funitems.commands.exception.CommandException;
import org.bukkit.command.CommandSender;

import java.util.List;

public interface ICommand {
    String getName();
    String getDescription();
    String getPermission();
    boolean isDebug();

    void execute(CommandSender sender, CommandArguments args) throws CommandException;
}
