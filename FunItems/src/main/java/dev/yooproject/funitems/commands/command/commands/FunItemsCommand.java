package dev.yooproject.funitems.commands.command.commands;

import dev.yooproject.funitems.commands.annotations.CommandInfo;
import dev.yooproject.funitems.commands.base.BaseCommand;
import dev.yooproject.funitems.commands.command.subcommands.GiveItemSubCommand;
import dev.yooproject.funitems.commands.command.subcommands.ReloadSubCommand;
import dev.yooproject.funitems.util.DebugUtil;
import org.bukkit.command.CommandSender;

@CommandInfo(name = "funitems", description = "Main plugin command", permission = "funitems.use")
public class FunItemsCommand extends BaseCommand {

    public FunItemsCommand() {
        registerSubCommand(new GiveItemSubCommand());
        registerSubCommand(new ReloadSubCommand());
    }

    @Override
    protected void executeDefault(CommandSender sender) {
    }

    @Override
    public String getName() {
        return "funitems";
    }

    @Override
    public String getDescription() {
        return "Main plugin command";
    }

    @Override
    public String getPermission() {
        return "funitems.use";
    }

    @Override
    public boolean isDebug() {
        return DebugUtil.isEnabled();
    }
}
