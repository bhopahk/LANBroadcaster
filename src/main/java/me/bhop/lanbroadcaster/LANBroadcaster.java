package me.bhop.lanbroadcaster;

import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.logging.Logger;

public class LANBroadcaster implements Runnable {
    private static final String BROADCAST_HOST = "224.0.2.60:4445";
    private int failcount = 0;
    private final DatagramSocket socket;
    private final int port;
    private final String motd;
    private final String configuredIP;
    private final Logger logger;
    private boolean running = true;

    public LANBroadcaster(DatagramSocket socket, int port, String motd, String ip, Logger logger) {
        this.socket = socket;
        this.port = port;
        this.motd = motd;
        this.configuredIP = ip;
        this.logger = logger;
    }

    public static DatagramSocket createSocket() {
        DatagramSocket socket = null;
        try {
            socket = new DatagramSocket();
            socket.setSoTimeout(3000);
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return socket;
    }

    @Override
    public void run() {
        try {
            byte[] ad = getAd();
            String[] host = BROADCAST_HOST.split(":");
            DatagramPacket packet = new DatagramPacket(ad, ad.length, InetAddress.getByName(host[0]), Integer.parseInt(host[1]));
            broadcast(socket, packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
        socket.close();
    }

    private void broadcast(DatagramSocket socket, DatagramPacket packet) {
        try {
            while (running) {
                try {
                    socket.send(packet);
                    failcount = 0;
                } catch (IOException e) {
                    fail(e);
                }
                Thread.sleep(1500);
            }
        } catch (InterruptedException ignored) { }
    }

    private void fail(Exception e) throws InterruptedException {
        if (failcount++ == 0)
            e.printStackTrace();
        if (failcount < 5)
            logger.warning("Failed to broadcast. Trying again in 10 seconds...");
        else if (failcount == 5)
            logger.severe("Broadcasting will not work until the network is fixed. Warnings disabled.");
        Thread.sleep(8500);
    }

    private byte[] getAd() {
        String ip = getLanIp(), ad = ip + ":" + port;
        if (isBukkit1_6() || isBungee() || isSponge() || isVelocity()) {
            ad = String.valueOf(port);
            logger.info("Broadcasting server with port " + ad + " over LAN.");
        } else
            logger.info("Broadcasting " + ip + " over LAN.");

        String str = "[MOTD]" + motd + "[/MOTD][AD]" + ad + "[/AD]";
        return str.getBytes(StandardCharsets.UTF_8);
    }

    private String getLanIp() {
        if (!configuredIP.equals(""))
            return configuredIP;
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface iface = interfaces.nextElement();
                Enumeration<InetAddress> addresses = iface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress address = addresses.nextElement();
                    if (address instanceof Inet4Address && !address.isLoopbackAddress())
                        return address.getHostAddress();
                }
            }
            throw new Exception("No usable IPv4 non-loopback address found");
        } catch (Exception e) {
            e.printStackTrace();
            logger.severe("Could not automatically detect LAN IP, please set server-ip in server.properties.");
            try {
                return InetAddress.getLocalHost().getHostAddress();
            } catch (UnknownHostException ex) {
                ex.printStackTrace();
                logger.severe("No network interfaces found!");
                return "This string does not matter. If we reach here the plugin will not work anyway.";
            }
        }
    }

    private boolean isBukkit1_6() {
        try {
            Class.forName("org.bukkit.entity.Horse");
            return true;
        } catch (ClassNotFoundException ignored) {
            return false;
        }
    }

    private boolean isBungee() {
        try {
            Class.forName("net.md_5.bungee.api.plugin.Plugin");
            return true;
        } catch (ClassNotFoundException ignored) {
            return false;
        }
    }

    private boolean isSponge() {
        try {
            Class.forName("org.spongepowered.api.Sponge");
            return true;
        } catch (ClassNotFoundException ignored) {
            return false;
        }
    }

    private boolean isVelocity() {
        try {
            Class.forName("com.velocitypowered.api.proxy.ProxyServer");
            return true;
        } catch (ClassNotFoundException ignored) {
            return false;
        }
    }

    public void shutdown() {
        this.running = false;
    }
}
