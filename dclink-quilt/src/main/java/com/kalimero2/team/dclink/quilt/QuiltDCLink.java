package com.kalimero2.team.dclink.quilt;

import com.kalimero2.team.dclink.DCLink;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import net.minecraft.server.MinecraftServer;
import org.quiltmc.loader.api.QuiltLoader;

import java.io.File;
import java.util.Optional;
import java.util.UUID;

public class QuiltDCLink extends DCLink {

    private final QuiltMod mod;
    private final MinecraftServer server;

    public QuiltDCLink(QuiltMod mod, MinecraftServer server){
        this.mod = mod;
        this.server = server;
    }

    @Override
    public void init() {
        super.init();
    }

    @Override
    public void load() {
        super.load();
    }

    @Override
    public void shutdown() {
        super.shutdown();
    }

    @Override
    public String getUsername(UUID uuid) {
        if(isLoaded()){
            // TODO: Check if this works with Floodgate
            Optional<GameProfile> optionalGameProfile = server.getUserCache().getByUuid(uuid);
            if(optionalGameProfile.isPresent()){
                return optionalGameProfile.get().getName();
            }else {
                MinecraftSessionService sessionService = server.getSessionService();
                GameProfile gameProfile = sessionService.fillProfileProperties(new GameProfile(uuid, null), true);
                return gameProfile.getName();
            }
        }
        return null;
    }

    @Override
    public String getConfigPath() {
        return new File(QuiltLoader.getConfigDir().toFile(),"dclink.conf").getAbsolutePath();
    }
}
