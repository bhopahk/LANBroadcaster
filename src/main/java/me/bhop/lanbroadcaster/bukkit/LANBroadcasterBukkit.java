package me.bhop.lanbroadcaster.bukkit;

import me.bhop.lanbroadcaster.LANBroadcaster;
import org.bukkit.Server;
import org.bukkit.plugin.java.JavaPlugin;

public class LANBroadcasterBukkit extends JavaPlugin {
    private LANBroadcaster broadcaster;

    @Override
    public void onEnable() {
        Server server = getServer();
        this.broadcaster = new LANBroadcaster(
                LANBroadcaster.createSocket(),
                server.getPort(),
                server.getMotd(),
                server.getIp(),
                getLogger());
        server.getScheduler().runTaskAsynchronously(this, broadcaster);
    }

    @Override
    public void onDisable() {
        broadcaster.shutdown();
        broadcaster = null;
    }
}
