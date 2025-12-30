package dev.yooproject.funitems.services;

import dev.yooproject.funitems.FunItems;
import dev.yooproject.funitems.async.AsyncExecutor;
import dev.yooproject.funitems.async.AsyncThreadPoolExecutor;
import dev.yooproject.funitems.async.BukkitAsyncExecutor;
import dev.yooproject.funitems.item.handler.ItemHandler;
import dev.yooproject.funitems.item.handler.PlayerHandler;
import dev.yooproject.funitems.providers.impl.ItemsProvider;
import dev.yooproject.funitems.providers.impl.LanguageProvider;
import dev.yooproject.funitems.util.DebugUtil;
import dev.yooproject.nms.NMSProvider;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

public class InitializationService {

    private AsyncExecutor asyncExecutor;
    private IParticleService iparticleService;
    private ItemCooldownService itemCooldownService;
    private ProviderService providerService;

    public void init() {
        FunItems plugin = FunItems.getInstance();
        DebugUtil.setEnabled(plugin.getConfig().getBoolean("settings.enable-debug"));
        boolean useThreadPool = plugin.getConfig().getBoolean("settings.async.use-thread-pool");
        int poolSize = plugin.getConfig().getInt("settings.async.pool-size");
        this.asyncExecutor = useThreadPool ? new AsyncThreadPoolExecutor(poolSize, "FunItems") : new BukkitAsyncExecutor(plugin);
        this.iparticleService = new IParticleService(asyncExecutor);
        this.itemCooldownService = new ItemCooldownService();
        this.providerService = new ProviderService();
        plugin.getConfigurationService().setProviderService(providerService);
        providerService.register(new LanguageProvider());
        providerService.register(new ItemsProvider());
        String nms = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
        NMSProvider.init(nms);
        registerListeners(
                new ItemHandler(),
                new PlayerHandler()
        );
    }

    public AsyncExecutor getAsyncExecutor() {
        return asyncExecutor;
    }

    public IParticleService getIParticleService() {
        return iparticleService;
    }

    public ItemCooldownService getItemCooldownService() { return itemCooldownService; }

    public ProviderService getProviderService(){
        return providerService;
    }

    private void registerListeners(Listener... listeners) {
        FunItems plugin = FunItems.getInstance();

        DebugUtil.log("InitializationService", "Starting registration of listeners...");

        for (Listener listener : listeners) {
            try {
                Bukkit.getPluginManager().registerEvents(listener, plugin);
                DebugUtil.log("InitializationService", "Registered listener: " + listener.getClass().getSimpleName());
            } catch (Exception e) {
                DebugUtil.exception("InitializationService", e);
            }
        }

        DebugUtil.log("InitializationService", "All listeners have been successfully registered!");
    }
}
