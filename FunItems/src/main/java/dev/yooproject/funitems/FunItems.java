    package dev.yooproject.funitems;

    import dev.yooproject.funitems.async.AsyncExecutor;
    import dev.yooproject.funitems.commands.command.commands.FunItemsCommand;
    import dev.yooproject.funitems.services.*;
    import org.bukkit.plugin.java.JavaPlugin;

    public final class FunItems extends JavaPlugin {
        private static FunItems instance;
        private ConfigurationService configurationService;
        private CommandService commandService;
        private InitializationService initializationService;
        private static WorldGuardFlagService worldGuardFlagService;

        @Override
        public void onLoad() {
            instance = this;

            worldGuardFlagService = new WorldGuardFlagService();
            worldGuardFlagService.registerFlags();
        }

        @Override
        public void onEnable() {
            saveDefaultConfig();
            commandService = new CommandService();
            commandService.registerCommand(new FunItemsCommand());
            configurationService = new ConfigurationService();
            configurationService.registerAll();
            initializationService = new InitializationService();
            initializationService.init();
        }

        @Override
        public void onDisable() {
            if (initializationService != null && initializationService.getAsyncExecutor() != null) {
                initializationService.getAsyncExecutor().shutdown();
            }
        }

        public static FunItems getInstance() { return instance; }

        public InitializationService getInitializationService() { return initializationService; }

        public ConfigurationService getConfigurationService() { return configurationService; }

        public CommandService getCommandService() { return commandService; }

        public AsyncExecutor getAsyncExecutor() { return initializationService.getAsyncExecutor(); }

        public IParticleService getIParticleService() { return initializationService.getIParticleService(); }

        public ItemCooldownService getItemCooldownService(){ return initializationService.getItemCooldownService(); }

        public static WorldGuardFlagService getWorldGuardFlagService() { return worldGuardFlagService; }
    }
