package dev.yooproject.funitems.commands.exception;

public class UnknownSubCommandException extends CommandException {
    public UnknownSubCommandException(String subCommand) {
        super("Unknown sub-command: " + subCommand);
    }
}
