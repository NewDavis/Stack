package me.newdavis.stack;

import me.newdavis.command.StackCmd;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class Stack extends JavaPlugin {

    public static final String PREFIX = "§8» §b§lStack §8┃§7 ";

    private static Stack instance;

    @Override
    public void onEnable() {
        instance = this;
        Bukkit.getConsoleSender().sendMessage(PREFIX + "§aaktiviert");

        new StackCmd();
    }

    public static Stack getInstance() {
        return instance;
    }

}
