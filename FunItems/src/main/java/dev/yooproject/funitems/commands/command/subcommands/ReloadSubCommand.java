package dev.yooproject.funitems.commands.command.subcommands;

import dev.yooproject.funitems.FunItems;
import dev.yooproject.funitems.commands.annotations.SubCommandInfo;
import dev.yooproject.funitems.commands.args.CommandArguments;
import dev.yooproject.funitems.commands.base.BaseSubCommand;
import dev.yooproject.funitems.commands.exception.CommandException;
import dev.yooproject.funitems.providers.impl.LanguageProvider;
import dev.yooproject.funitems.services.ProviderService;
import dev.yooproject.funitems.util.ColorUtil;
import dev.yooproject.funitems.util.DebugUtil;
import org.bukkit.command.CommandSender;

@SubCommandInfo(name = "reload", description = "Reloads all providers", permission = "funitems.reload")
public class ReloadSubCommand extends BaseSubCommand {

    @Override
    public String getName() {
        return "reload";
    }

    @Override
    public String getDescription() {
        return "Reloads all providers";
    }

    @Override
    public String getPermission() {
        return "funitems.reload";
    }

    @Override
    public boolean isDebug() {
        return DebugUtil.isEnabled();
    }

    @Override
    protected void executeSub(CommandSender sender, CommandArguments args) throws CommandException {
        ProviderService providerService = FunItems.getInstance()
                .getInitializationService()
                .getProviderService();
        FunItems.getInstance().getConfigurationService().reloadAllConfigs();

        LanguageProvider languageProvider = providerService.get(LanguageProvider.class);

        sender.sendMessage(ColorUtil.translate(languageProvider.get("commands.reloaded")));
        DebugUtil.log("ReloadSubCommand", "All providers reloaded by " + sender.getName());
    }
}
