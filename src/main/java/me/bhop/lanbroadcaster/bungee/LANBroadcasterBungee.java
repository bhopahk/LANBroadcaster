package me.bhop.lanbroadcaster.bungee;

import me.bhop.lanbroadcaster.LANBroadcaster;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class LANBroadcasterBungee extends Plugin {
    private List<LANBroadcaster> broadcasters = new ArrayList<>();

    @Override
    @SuppressWarnings("unchecked")
    public void onEnable() {
        ProxyServer proxy = getProxy();
        Collection<?> listeners = proxy.getConfigurationAdapter().getList("listeners", null);
        for (Object obj : listeners) {
            Map<String, Object> map = (Map<String, Object>) obj;

            String host = (String) map.get("host");
            String[] spl = host.split(":", 2);

            String address = spl[0];
            int port = Integer.parseInt(spl[1]);

            if (address.equals("0.0.0.0") || address.equals("127.0.0.1"))
                address = "";
            LANBroadcaster broadcaster = new LANBroadcaster(
                    LANBroadcaster.createSocket(),
                    port,
                    ChatColor.translateAlternateColorCodes('&', (String) map.get("motd")),
                    address,
                    getLogger());
            broadcasters.add(broadcaster);
        }
        for (LANBroadcaster broadcaster : broadcasters)
            proxy.getScheduler().runAsync(this, broadcaster);
    }

    @Override
    public void onDisable() {
        for (LANBroadcaster broadcaster : broadcasters)
            broadcaster.shutdown();
        broadcasters.clear();
    }
}
