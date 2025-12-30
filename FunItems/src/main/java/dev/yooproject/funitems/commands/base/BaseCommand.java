package dev.yooproject.funitems.commands.base;

import dev.yooproject.funitems.commands.annotations.CommandInfo;
import dev.yooproject.funitems.commands.args.CommandArguments;
import dev.yooproject.funitems.commands.command.ICommand;
import dev.yooproject.funitems.commands.command.ISubCommand;
import dev.yooproject.funitems.commands.exception.CommandException;
import dev.yooproject.funitems.commands.exception.NoPermissionException;
import dev.yooproject.funitems.commands.exception.UnknownSubCommandException;
import dev.yooproject.funitems.util.DebugUtil;
import org.bukkit.command.CommandSender;

import java.util.HashMap;
import java.util.Map;

public abstract class BaseCommand implements ICommand {

    protected final Map<String, ISubCommand> subCommands = new HashMap<>();

    protected void registerSubCommand(ISubCommand subCommand) {
        subCommands.put(subCommand.getName().toLowerCase(), subCommand);
    }

    @Override
    public void execute(CommandSender sender, CommandArguments args) throws CommandException {
        CommandInfo info = this.getClass().getAnnotation(CommandInfo.class);
        boolean debug = info != null && info.debug();
        boolean throwEx = info == null || info.throwException();

        if (debug) DebugUtil.log(getName(), "Executing command");

        if (!getPermission().isEmpty() && !sender.hasPermission(getPermission())) {
            if (throwEx) throw new NoPermissionException(getPermission());
            sender.sendMessage("No permission: " + getPermission());
            return;
        }

        if (args.size() == 0) {
            executeDefault(sender);
            return;
        }

        ISubCommand sub = subCommands.get(args.get(0).toLowerCase());
        if (sub == null) {
            if (throwEx) throw new UnknownSubCommandException(args.get(0));
            sender.sendMessage("Unknown sub-command: " + args.get(0));
            return;
        }

        try {
            sub.execute(sender, new CommandArguments(args.getAll().subList(1, args.size()).toArray(new String[0])));
        } catch (CommandException e) {
            if (debug) DebugUtil.exception(sub.getName(), e);
            throw e;
        }
    }

    protected abstract void executeDefault(CommandSender sender);
}
