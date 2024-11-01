package com.kalimero2.team.dclink;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationOptions;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

import java.io.File;

public class DCLinkConfig {

    private final HoconConfigurationLoader loader;
    private final CommentedConfigurationNode node;
    private final DatabaseConfiguration databaseConfiguration;
    private final DiscordConfiguration discordConfiguration;
    private final LinkingConfiguration linkingConfiguration;
    private final MapConfiguration mapConfiguration;


    public DCLinkConfig(String configPath) throws ConfigurateException {
        File config = new File(configPath);
        loader = HoconConfigurationLoader.builder()
                .defaultOptions(ConfigurationOptions.defaults().shouldCopyDefaults(true))
                .file(config)
                .build();
        if (!config.exists()) {
            node = CommentedConfigurationNode.root();
        } else {
            node = loader.load();
        }

        databaseConfiguration = node.node("database").get(DatabaseConfiguration.class);
        discordConfiguration = node.node("discord").get(DiscordConfiguration.class);
        linkingConfiguration = node.node("linking").get(LinkingConfiguration.class);
        mapConfiguration = node.node("map").get(MapConfiguration.class);

        if (!config.exists()) {
            save();
        }
    }

    public void save() throws ConfigurateException {
        node.node("database").set(DatabaseConfiguration.class, databaseConfiguration);
        node.node("discord").set(DiscordConfiguration.class, discordConfiguration);
        node.node("linking").set(LinkingConfiguration.class, linkingConfiguration);
        node.node("map").set(MapConfiguration.class, mapConfiguration);
        loader.save(node);
    }

    public DatabaseConfiguration getDatabaseConfiguration() {
        return databaseConfiguration;
    }

    public DiscordConfiguration getDiscordConfiguration() {
        return discordConfiguration;
    }

    public LinkingConfiguration getLinkingConfiguration() {
        return linkingConfiguration;
    }

    public MapConfiguration getMapConfiguration() {
        return mapConfiguration;
    }

    @ConfigSerializable
    public static class DatabaseConfiguration {

        @Comment("The Postgres database address")
        private String address = "";

        @Comment("The Postgres database address")
        private String port = "";

        @Comment("The Postgres database address")
        private String database = "";

        @Comment("The Postgres database address")
        private String username = "";

        @Comment("The Postgres database address")
        private String password = "";

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getDatabase() {
            return database;
        }

        public void setDatabase(String database) {
            this.database = database;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getPort() {
            return port;
        }

        public void setPort(String port) {
            this.port = port;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }
    }

    @ConfigSerializable
    public static class MapConfiguration {
        private String fileName = "fabric.nbt";

        private int mapX = 0;
        private int mapY = 0;
        private int mapZ = 0;

        @Comment("0=survival 1=creative 2=adventure 3=spectator")
        private int gamemode = 1;

        public int getGamemode() {
            return gamemode;
        }

        public void setGamemode(int gamemode) {
            this.gamemode = gamemode;
        }

        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        public int getMapX() {
            return mapX;
        }

        public void setMapX(int mapX) {
            this.mapX = mapX;
        }

        public int getMapY() {
            return mapY;
        }

        public void setMapY(int mapY) {
            this.mapY = mapY;
        }

        public int getMapZ() {
            return mapZ;
        }

        public void setMapZ(int mapZ) {
            this.mapZ = mapZ;
        }
    }

    @ConfigSerializable
    public static class DiscordConfiguration {
        @Comment("Bot Token (see https://discord.com/developers/applications)")
        private String token = "";
        @Comment("Guild ID of the Guild where the bot will run")
        private String guild = "";
        @Comment("Channel ID of the channel where the bot will send the message with the button to link their account")
        private String linkChannel = "";
        @Comment("Role ID of the role that the bot will give to the linked players (If left blank, the bot will not give any roles)")
        private @Nullable String linkRole = "";

        @Comment("Role ID of the role that the bot will take away from user")
        private @Nullable String delinkRole = "";
        @Comment("Message to show on the bot's status")
        private String statusMessage = "Minecraft";

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public String getGuild() {
            return guild;
        }

        public void setGuild(String guild) {
            this.guild = guild;
        }

        public String getLinkChannel() {
            return linkChannel;
        }

        public void setLinkChannel(String linkChannel) {
            this.linkChannel = linkChannel;
        }

        @Nullable
        public String getLinkRole() {
            return linkRole;
        }

        public void setLinkRole(String linkRole) {
            this.linkRole = linkRole;
        }

        @Nullable
        public String getDeLinkRole() {
            return delinkRole;
        }

        public void setDeLinkRole(String linkRole) {
            this.delinkRole = linkRole;
        }

        public String getStatusMessage() {
            return statusMessage;
        }

        public void setStatusMessage(String statusMessage) {
            this.statusMessage = statusMessage;
        }
    }

    @ConfigSerializable
    public static class LinkingConfiguration {
        @Comment("If true, the player needs to be linked before they can join the server")
        private boolean linkRequired = true;
        @Comment("Limit of Java Edition accounts that can be linked to one Discord account")
        private int javaLimit = 1;
        @Comment("Limit of Bedrock Edition accounts that can be linked to one Discord account. Requires Floodgate to be installed")
        private int bedrockLimit = 1;

        public boolean isLinkRequired() {
            return linkRequired;
        }

        public void setLinkRequired(boolean linkRequired) {
            this.linkRequired = linkRequired;
        }

        public int getJavaLimit() {
            return javaLimit;
        }

        public void setJavaLimit(int javaLimit) {
            this.javaLimit = javaLimit;
        }

        public int getBedrockLimit() {
            return bedrockLimit;
        }

        public void setBedrockLimit(int bedrockLimit) {
            this.bedrockLimit = bedrockLimit;
        }
    }
}