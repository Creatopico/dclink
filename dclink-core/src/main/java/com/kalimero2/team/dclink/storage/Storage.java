package com.kalimero2.team.dclink.storage;

import com.kalimero2.team.dclink.DCLink;
import com.kalimero2.team.dclink.api.discord.DiscordAccount;
import com.kalimero2.team.dclink.api.minecraft.MinecraftPlayer;
import com.kalimero2.team.dclink.impl.discord.DiscordAccountImpl;
import com.kalimero2.team.dclink.impl.minecraft.MinecraftPlayerImpl;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.postgres.PostgresPlugin;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class Storage {
    private final DCLink dcLink;
    public static Jdbi JDBI;

    public Storage(DCLink dcLink, Properties connection) {
        this.dcLink = dcLink;

        String url = String.format(
                "jdbc:postgresql://%s:%s/%s",
                connection.get("address"),
                connection.get("port"),
                connection.get("database")
        );

        try {
            DriverManager.registerDriver(new org.postgresql.Driver());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        JDBI = Jdbi.create(url, connection)
                .installPlugin(new SqlObjectPlugin())
                .installPlugin(new PostgresPlugin());

        createTables();
        dcLink.getLogger().info("Database initialized");
    }

    private void createTables() {
        try (Handle handle = JDBI.open()){
            StorageDao storageDao = handle.attach(StorageDao.class);
            storageDao.createDiscordTable();
            storageDao.createMinecraftTableIfNotExists();
        }
    }

    private void saveDiscordAccount(DiscordAccount discordAccount) throws SQLException {
        try (Handle handle = JDBI.open()){
            StorageDao storageDao = handle.attach(StorageDao.class);
            storageDao.saveDiscordAccount(discordAccount.getId());
        }
    }

    public void createMinecraftPlayer(UUID uuid, String name) throws SQLException {
        try (Handle handle = JDBI.open()){
            StorageDao storageDao = handle.attach(StorageDao.class);
            storageDao.createMinecraftPlayer(uuid, name);
        }
    }

    public String getLastKnownName(UUID uuid) throws SQLException {
        try (Handle handle = JDBI.open()){
            StorageDao storageDao = handle.attach(StorageDao.class);
            return storageDao.getPlayerById(uuid).lastKnownName();
        }
    }

    public void setLastKnownName(UUID uuid, String name) throws SQLException {
        try (Handle handle = JDBI.open()){
            StorageDao storageDao = handle.attach(StorageDao.class);
            storageDao.setLastKnownName(uuid, name);
        }
    }

    public DiscordAccount getDiscordAccount(String discordID) throws SQLException {
        if (discordID == null || discordID.isEmpty() || discordID.equals("null")) {
            return null;
        }

        List<MinecraftPlayer> linkedPlayers = new ArrayList<>();

        DiscordAccountImpl discordAccount = new DiscordAccountImpl(dcLink, discordID) {
            @Override
            public Collection<MinecraftPlayer> getLinkedPlayers() {
                return linkedPlayers.stream().toList();
            }
        };

        try (Handle handle = JDBI.open()){
            StorageDao storageDao = handle.attach(StorageDao.class);
            MinecraftPlayerEntity playerEntity = storageDao.getPlayerByDiscord(discordID);

            if (playerEntity != null) {
                linkedPlayers.add(new MinecraftPlayerImpl(playerEntity.id(), playerEntity.lastKnownName()) {
                    @Override
                    public DiscordAccount getDiscordAccount() {
                        return discordAccount;
                    }
                });
            }
        }

        saveDiscordAccount(discordAccount);
        return discordAccount;
    }

    public UUID getUUIDByLastKnownName(String username) throws SQLException {
        try (Handle handle = JDBI.open()){
            StorageDao storageDao = handle.attach(StorageDao.class);
            return storageDao.getPlayerByName(username).id();
        }
    }


    public MinecraftPlayer getMinecraftPlayer(UUID uuid) throws SQLException {
        try (Handle handle = JDBI.open()){
            StorageDao storageDao = handle.attach(StorageDao.class);
            MinecraftPlayerEntity playerEntity = storageDao.getPlayerById(uuid);

            if (playerEntity != null && playerEntity.lastKnownName() != null) {
                final DiscordAccount discordAccount;
                if(playerEntity.discordID() != null){
                    discordAccount = getDiscordAccount(playerEntity.discordID());
                }else {
                    discordAccount = null;
                }
                return new MinecraftPlayerImpl(uuid, playerEntity.lastKnownName()) {
                    @Override
                    public DiscordAccount getDiscordAccount() {
                        return discordAccount;
                    }
                };
            }
        }
        return null;
        //throw new IllegalArgumentException("There is NO player with such id in database! DCLINK BY LITTLELIGR");
    }

    public boolean linkAccounts(MinecraftPlayer minecraftPlayer, DiscordAccount discordAccount) {
        try (Handle handle = JDBI.open()){
            StorageDao storageDao = handle.attach(StorageDao.class);
            storageDao.linkAccounts(minecraftPlayer.getUuid(), discordAccount.getId());
            return true;
        }
    }

    public void unLinkAccounts(DiscordAccount discordAccount) {
        try (Handle handle = JDBI.open()){
            StorageDao storageDao = handle.attach(StorageDao.class);
            storageDao.unlinkAccountsByDiscord(discordAccount.getId());
        }
    }

    public void unLinkAccount(MinecraftPlayer minecraftPlayer) {
        try (Handle handle = JDBI.open()){
            StorageDao storageDao = handle.attach(StorageDao.class);
            storageDao.unlinkAccountsById(minecraftPlayer.getUuid());
        }
    }

}
