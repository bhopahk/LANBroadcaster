package me.bhop.lanbroadcaster.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import me.bhop.lanbroadcaster.LANBroadcaster;
import net.kyori.text.serializer.ComponentSerializers;
import org.slf4j.Logger;

import java.nio.file.Path;

@Plugin(id = "lanbroadcaster", name = "LANBroadcaster", version = "1.0", description = "Broadcasts a Minecraft server over LAN.", authors = {"Ruan", "bhop_"})
public class LANBroadcasterVelocity {
    private final ProxyServer proxyServer;
    private LANBroadcaster broadcaster;

    @Inject
    public LANBroadcasterVelocity(ProxyServer proxyServer, Logger logger, @DataDirectory Path dataDirectory) {
        this.proxyServer = proxyServer;
    }

    @Subscribe
    public void onProxyInit(ProxyInitializeEvent event) {
        this.broadcaster = new LANBroadcaster(
                LANBroadcaster.createSocket(),
                proxyServer.getBoundAddress().getPort(),
                ComponentSerializers.LEGACY.serialize(proxyServer.getConfiguration().getMotdComponent()),
                proxyServer.getBoundAddress().getAddress().getHostAddress(),
                java.util.logging.Logger.getLogger("LANBroadcaster"));
        proxyServer.getScheduler().buildTask(this, this.broadcaster).schedule();
    }

    @Subscribe
    public void onProxyShutdown(ProxyShutdownEvent event) {
        this.broadcaster.shutdown();
        this.broadcaster = null;
    }
}
