package com.kalimero2.team.dclink;

import com.kalimero2.team.dclink.api.minecraft.MinecraftPlayer;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

public class DCLinkCodes {

    private static final Map<String, MinecraftPlayer> codeIdMap = new HashMap<>();

    public static String addPlayer(MinecraftPlayer minecraftPlayer) {
        // remove old code
        Optional<Map.Entry<String, MinecraftPlayer>> optionalEntry = codeIdMap.entrySet().stream().filter(entry -> entry.getValue().equals(minecraftPlayer)).findFirst();
        optionalEntry.ifPresent(entry -> removePlayer(entry.getKey()));

        // generate new code
        String code = generateCode();
        codeIdMap.put(code, minecraftPlayer);
        return code;
    }

    @Nullable
    public static MinecraftPlayer getPlayer(String code) {
        return codeIdMap.get(code);
    }

    public static void removePlayer(String code) {
        codeIdMap.remove(code);
    }

    private static String generateCode() {
        String[] letters = "0123456789ABCDEF".split("");
        Random random = new Random();
        boolean needsCheck = true;
        StringBuilder sb = new StringBuilder();
        while (needsCheck) {
            for (int i = 0; i < 4; i++) {
                sb.append(letters[random.nextInt(letters.length)]);
            }
            if (codeIdMap.containsKey(sb.toString())) {
                sb = new StringBuilder();
            } else {
                needsCheck = false;
            }
        }
        return sb.toString();
    }

}
