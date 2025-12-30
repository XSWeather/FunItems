package dev.yooproject.funitems.commands.command.subcommands;

import dev.yooproject.funitems.FunItems;
import dev.yooproject.funitems.commands.annotations.SubCommandInfo;
import dev.yooproject.funitems.commands.args.CommandArguments;
import dev.yooproject.funitems.commands.base.BaseSubCommand;
import dev.yooproject.funitems.commands.exception.CommandException;
import dev.yooproject.funitems.configuration.impl.ItemsConfiguration;
import dev.yooproject.funitems.configuration.impl.LanguageConfiguration;
import dev.yooproject.funitems.services.ItemService;
import dev.yooproject.funitems.util.ColorUtil;
import dev.yooproject.funitems.util.DebugUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@SubCommandInfo(name = "give", description = "Gives player item", permission = "funitems.give")
public class GiveItemSubCommand extends BaseSubCommand {

    @Override
    public String getName() {
        return "give";
    }

    @Override
    public String getDescription() {
        return "Gives player item";
    }

    @Override
    public String getPermission() {
        return "funitems.give";
    }

    @Override
    public boolean isDebug() {
        return DebugUtil.isEnabled();
    }

    @Override
    protected void executeSub(CommandSender sender, CommandArguments args) throws CommandException {

        if (!args.has(2)) {
            throw new CommandException("Usage: /funitems give <player> <item> <amount>");
        }
        String playerName = args.get(0);
        Player target = Bukkit.getPlayerExact(playerName);
        if (target == null) {
            throw new CommandException("Player not found: " + playerName);
        }

        String itemId = args.get(1);
        int amount = args.getIntOrDefault(2, 1);
        if (amount <= 0) amount = 1;

        ItemsConfiguration cfg = FunItems.getInstance().getConfigurationService().getConfig(ItemsConfiguration.class);

        ItemService service = new ItemService(cfg);

        ItemStack item = service.getItem(itemId, amount);
        if (item == null) {
            throw new CommandException("Item not found: " + itemId);
        }

        target.getInventory().addItem(item);

        LanguageConfiguration langCfg = FunItems.getInstance().getConfigurationService().getConfig(LanguageConfiguration.class);
        String msg = langCfg.getConfig().getString("commands.give-command", "Item {item} given to player {player} amount {amount}");
        msg = msg.replace("{player}", target.getName()).replace("{item}", itemId).replace("{amount}", String.valueOf(amount));
        sender.sendMessage(ColorUtil.translate(msg));
    }
}
