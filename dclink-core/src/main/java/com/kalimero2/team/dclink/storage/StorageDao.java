package com.kalimero2.team.dclink.storage;


import org.jdbi.v3.sqlobject.config.RegisterConstructorMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.util.UUID;

public interface StorageDao {

    @SqlUpdate("CREATE TABLE IF NOT EXISTS discord_account (DISCORD_ID TEXT PRIMARY KEY);")
    void createDiscordTable();

    @SqlUpdate("CREATE TABLE IF NOT EXISTS minecraft_account (id UUID PRIMARY KEY, last_known_name text, discord_id text, FOREIGN KEY (discord_id) REFERENCES discord_account);")
    void createMinecraftTableIfNotExists();

    @SqlUpdate("INSERT INTO discord_account VALUES (:discordID) ON CONFLICT DO NOTHING;")
    void saveDiscordAccount(@Bind("discordID") String uuid);

    @SqlUpdate("INSERT INTO minecraft_account (id, last_known_name) VALUES (:id, :lastKnownName) ON CONFLICT DO NOTHING;")
    void createMinecraftPlayer(@Bind("id") UUID minecraftId, @Bind("lastKnownName") String playerName);

    @SqlUpdate("UPDATE minecraft_account SET last_known_name = :name WHERE id = :id;")
    void setLastKnownName(@Bind("id") UUID minecraftId, @Bind("name") String playerName);

    @SqlQuery("SELECT * FROM minecraft_account WHERE discord_id = :discordID;")
    @RegisterConstructorMapper(MinecraftPlayerEntity.class)
    MinecraftPlayerEntity getPlayerByDiscord(@Bind("discordID") String discordID);

    @SqlQuery("SELECT * FROM minecraft_account WHERE id = :id;")
    @RegisterConstructorMapper(MinecraftPlayerEntity.class)
    MinecraftPlayerEntity getPlayerById(@Bind("id") UUID id);

    @SqlQuery("SELECT * FROM minecraft_account WHERE last_known_name = :name;")
    @RegisterConstructorMapper(MinecraftPlayerEntity.class)
    MinecraftPlayerEntity getPlayerByName(@Bind("name") String name);

    @SqlUpdate("UPDATE minecraft_account SET discord_id = :discordID WHERE id = :id;")
    void linkAccounts(@Bind("id") UUID minecraftId, @Bind("discordID") String discordID);

    @SqlUpdate("UPDATE minecraft_account SET discord_id = null WHERE id = :id;")
    void unlinkAccountsById(@Bind("id") UUID minecraftId);

    @SqlUpdate("UPDATE minecraft_account SET discord_id = null WHERE discord_id = :discordID;")
    void unlinkAccountsByDiscord(@Bind("discordID") String discordID);
}
