package fr.hyriode.runner;

import fr.hyriode.hyrame.plugin.IPluginProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class HyriRunnerProvider implements IPluginProvider {

    private static final String PACKAGE = "fr.hyriode.runner";

    private final JavaPlugin plugin;

    public HyriRunnerProvider(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public JavaPlugin getPlugin() {
        return plugin;
    }

    @Override
    public String getId() {
        return "therunner";
    }

    @Override
    public String[] getCommandsPackages() {
        return new String[] {PACKAGE};
    }

    @Override
    public String[] getListenersPackages() {
        return new String[] {PACKAGE};
    }

    @Override
    public String[] getItemsPackages() {
        return new String[] {PACKAGE};
    }

    @Override
    public String getLanguagesPath() {
        return "/lang/";
    }

}
