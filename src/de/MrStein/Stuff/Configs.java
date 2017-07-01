package de.MrStein.Stuff;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;

public class Configs {
    private FileConfiguration config = null;
    private File file = null;
    private String filename = null;
    private Plugin plugin;
    private String subfolder = "";

    public Configs(Plugin plugin, String filename, String... subfolder) {
        this.plugin = plugin;
        this.filename = filename;
        this.subfolder = subfolder.length > 0 ? "plugins/" + plugin.getName() + "/" + subfolder[0] : "plugins/" + plugin.getName();
    }

    public FileConfiguration get() {
        if (config == null) {
            reload();
        }
        return config;
    }

    public void save() {
        try {
            config.save(file);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Couldn't save File to " + file.getName(), e);
        }
    }

    public void reload() {
        file = new File(subfolder, filename);
        config = YamlConfiguration.loadConfiguration(file);
        InputStream defPlayerData = plugin.getResource(filename);
        if (defPlayerData != null) {
            config.setDefaults(YamlConfiguration.loadConfiguration(new InputStreamReader(defPlayerData)));
        }
    }

    public void saveDefault() {
        get().options().copyDefaults(true);
        save();
    }

    public File getFile() {
        return file;
    }
}
