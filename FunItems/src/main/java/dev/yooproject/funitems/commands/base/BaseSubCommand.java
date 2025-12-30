package dev.yooproject.funitems.commands.base;

import dev.yooproject.funitems.commands.annotations.SubCommandInfo;
import dev.yooproject.funitems.commands.args.CommandArguments;
import dev.yooproject.funitems.commands.command.ISubCommand;
import dev.yooproject.funitems.commands.exception.CommandException;
import dev.yooproject.funitems.util.DebugUtil;
import org.bukkit.command.CommandSender;

public abstract class BaseSubCommand implements ISubCommand {

    @Override
    public void execute(CommandSender sender, CommandArguments args) throws CommandException {
        SubCommandInfo info = this.getClass().getAnnotation(SubCommandInfo.class);
        boolean debug = info != null && info.debug();
        boolean throwEx = info == null || info.throwException();

        try {
            executeSub(sender, args);
        } catch (CommandException e) {
            if (debug) DebugUtil.exception(getName(), e);
            if (throwEx) throw e;
            sender.sendMessage("Error: " + e.getMessage());
        }
    }

    protected abstract void executeSub(CommandSender sender, CommandArguments args) throws CommandException;
}
