package com.kalimero2.team.dclink.velocity.limbo;

import net.elytrium.limboapi.api.Limbo;
import net.elytrium.limboapi.api.LimboSessionHandler;
import net.elytrium.limboapi.api.player.LimboPlayer;

public class PlayerSessionHandler implements LimboSessionHandler {

    LimboPlayer player;

    @Override
    public void onSpawn(Limbo server, LimboPlayer player) {
        this.player = player;
        player.disableFalling();
    }

    public LimboPlayer getLimboPlayer() {
        return player;
    }


}
