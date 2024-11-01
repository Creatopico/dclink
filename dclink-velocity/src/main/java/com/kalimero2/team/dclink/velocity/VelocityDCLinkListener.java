package com.kalimero2.team.dclink.velocity;

import com.kalimero2.team.dclink.DCLink;
import com.kalimero2.team.dclink.discord.DiscordAccountLinker;
import com.kalimero2.team.dclink.discord.EnterInfo;
import com.kalimero2.team.dclink.velocity.limbo.PlayerSessionHandler;
import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.ResultedEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.LoginEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.proxy.Player;
import net.elytrium.limboapi.api.event.LoginLimboRegisterEvent;
import net.elytrium.limboapi.api.protocol.PreparedPacket;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;

import java.util.Optional;


public class VelocityDCLinkListener {

    private final VelocityDCLink velocityDCLink;
    private final VelocityPlugin plugin;

    public VelocityDCLinkListener(VelocityDCLink velocityDCLink, VelocityPlugin plugin) {
        this.velocityDCLink = velocityDCLink;
        this.plugin = plugin;
    }

    @Subscribe(order = PostOrder.FIRST)
    public void onJoin(LoginEvent event) {
//        Player player = event.getPlayer();
//        DCLink.JoinResult joinResult = velocityDCLink.onLogin(player.getUniqueId(), player.getUsername());
//
//        if (joinResult.success()) {
//                event.setResult(ResultedEvent.ComponentResult.allowed());
//            } else {
//                event.setResult(ResultedEvent.ComponentResult.denied(joinResult.message()));
//        }
    }

    @Subscribe
    public void onShutdown(ProxyShutdownEvent event) {
        velocityDCLink.shutdown();
    }

    @Subscribe(order = PostOrder.LAST)
    public void onLogin(LoginLimboRegisterEvent event) {
        Player player = event.getPlayer();
        plugin.logger.info(player.getUsername());

        DCLink.JoinResult joinResult = velocityDCLink.onLogin(player.getUniqueId(), player.getUsername());
        Optional<String> optionsDiscordID = velocityDCLink.getDiscordID(player.getUniqueId(), player.getUsername());

        if (!joinResult.success())
            player.disconnect(joinResult.message());
        else {
            String ip = player.getRemoteAddress().getAddress().getHostAddress();
            PlayerSessionHandler sessionHandler = new PlayerSessionHandler();

            boolean player_has_access = DiscordAccountLinker.discord_to_ip_map.containsKey(optionsDiscordID.get())
                    && DiscordAccountLinker.discord_to_ip_map.get(optionsDiscordID.get()).equals(ip);
            if (!player_has_access) {
                event.addOnJoinCallback(() -> this.plugin.sendToFilterServer(player, sessionHandler));
                velocityDCLink.sendMessageOnEnter(
                        optionsDiscordID.get(), new EnterInfo(
                                ip,
                                (isPlayer) -> {
                                    if (isPlayer) sessionHandler.getLimboPlayer().disconnect();
                                    else sessionHandler.getLimboPlayer().closeWith("");
                                })
                );
            }
        }
    }


}
