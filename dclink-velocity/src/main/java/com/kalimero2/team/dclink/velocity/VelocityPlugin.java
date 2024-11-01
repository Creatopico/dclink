package com.kalimero2.team.dclink.velocity;

import com.google.common.io.Resources;
import com.google.inject.Inject;
import com.kalimero2.team.dclink.velocity.limbo.PlayerSessionHandler;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Dependency;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.PluginContainer;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import net.elytrium.limboapi.api.Limbo;
import net.elytrium.limboapi.api.LimboFactory;
import net.elytrium.limboapi.api.chunk.Dimension;
import net.elytrium.limboapi.api.chunk.VirtualWorld;
import net.elytrium.limboapi.api.file.BuiltInWorldFileType;
import net.elytrium.limboapi.api.file.WorldFile;
import net.elytrium.limboapi.api.material.Block;
import net.elytrium.limboapi.api.player.GameMode;
import net.elytrium.limboapi.api.player.LimboPlayer;
import net.elytrium.limboapi.api.protocol.PreparedPacket;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.title.Title;
import org.slf4j.Logger;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.util.stream.Collectors;


@Plugin(
        id = "dclink-velocity",
        name = "DCLink",
        version = "${version}",
        dependencies = {
                @Dependency(id = "limboapi")
        }
)
public class VelocityPlugin {

    private final VelocityDCLink dclink;
    private final ProxyServer server;

    private final LimboFactory limboFactory;
    private VirtualWorld filterWorld;
    private Limbo limbo;

    public Logger logger;
    public static PreparedPacket successfulBotFilterDisconnect;

    private final Path dataDirectory;

    @Inject
    public VelocityPlugin(ProxyServer server, Logger logger, @DataDirectory Path dataDirectory) {
        this.logger = logger;
        this.dataDirectory = dataDirectory;
        this.dclink = new VelocityDCLink(this, server, dataDirectory);
        this.server = server;
        dclink.init();

        this.limboFactory = (LimboFactory) this.server.getPluginManager().getPlugin("limboapi").flatMap(PluginContainer::getInstance).orElseThrow();
    }

    @Subscribe
    public void onProxyInitialize(ProxyInitializeEvent event) {
        dclink.load();
        this.server.getEventManager().register(this, new VelocityDCLinkListener(dclink, this));

        this.filterWorld = this.limboFactory.createVirtualWorld(
                Dimension.THE_END,
                this.dclink.getConfig().getMapConfiguration().getMapX(),
                this.dclink.getConfig().getMapConfiguration().getMapY(),
                this.dclink.getConfig().getMapConfiguration().getMapZ(),
                90f, 38f
        );

        try {
            Path path = dataDirectory.resolve(this.dclink.getConfig().getMapConfiguration().getFileName());
            WorldFile worldFile = limboFactory.openWorldFile(BuiltInWorldFileType.STRUCTURE, path);
            worldFile.toWorld(limboFactory, filterWorld, 0, 0, 0, 15);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        this.limbo = this.limboFactory.createLimbo(this.filterWorld)
                .setName("LimboFilter")
                .setWorldTime(1000L)
                .setGameMode(GameMode.getByID(this.dclink.getConfig().getMapConfiguration().getGamemode()));
    }

    public void sendToFilterServer(Player player, PlayerSessionHandler sessionHandler) {
        try {
            this.limbo.spawnPlayer(player, sessionHandler);

            TextComponent check_text = Component
                    .text("Check your ")
                    .append(Component.text("discord").color(TextColor.color(0x5793266)))
                    .append(Component.text("!"));

            TextComponent click_text = Component.text("Click ")
                    .append(Component.text("it's me", NamedTextColor.GREEN))
                    .append(Component.text(" to enter Creatopico server."));

            player.sendMessage(check_text);
            player.sendMessage(click_text);

            player.showTitle(Title.title(check_text, click_text));
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

//    private PreparedPacket createDisconnectPacket(LimboFactory factory, String message) {
//        return factory.createPreparedPacket().prepare(version -> Disconnect.create(SERIALIZER.deserialize(message), version)).build();
//    }
}
