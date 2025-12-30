package dev.yooproject.funitems.commands.exception;

public class NoPermissionException extends CommandException {
    public NoPermissionException(String permission) {
        super("You do not have permission: " + permission);
    }
}
