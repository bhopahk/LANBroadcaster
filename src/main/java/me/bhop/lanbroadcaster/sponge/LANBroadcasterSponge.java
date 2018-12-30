package me.bhop.lanbroadcaster.sponge;

import me.bhop.lanbroadcaster.LANBroadcaster;
import org.spongepowered.api.Server;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.game.state.GameStoppingServerEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.scheduler.Task;

import java.util.logging.Logger;

@Plugin(id = "lanbroadcaster", name = "LANBroadcaster", version = "1.0", description = "Broadcasts a Minecraft server over LAN.", authors = {"Ruan", "bhop_"})
public class LANBroadcasterSponge {
    private LANBroadcaster broadcaster;

    @Listener
    public void onPreInit(GameStartedServerEvent event) {
        Server server = Sponge.getServer();
        if (!server.getBoundAddress().isPresent())
            return;

        this.broadcaster = new LANBroadcaster(
                LANBroadcaster.createSocket(),
                server.getBoundAddress().get().getPort(),
                server.getMotd().toString(),
                server.getBoundAddress().get().getHostName(),
                Logger.getLogger("LANBroadcaster"));
        Task.builder().execute(this.broadcaster).async().name("LANBroadcaster").submit(this);
    }

    @Listener
    public void onStop(GameStoppingServerEvent event) {
        this.broadcaster.shutdown();
        this.broadcaster = null;
    }
}
