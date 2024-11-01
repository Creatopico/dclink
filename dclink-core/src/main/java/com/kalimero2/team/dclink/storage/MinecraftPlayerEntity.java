package com.kalimero2.team.dclink.storage;

import java.util.UUID;

public record MinecraftPlayerEntity(UUID id, String lastKnownName, String discordID) {
}
