package com.kalimero2.team.dclink.discord;

import java.util.function.Consumer;

public record EnterInfo(String ip, Consumer<Boolean> consumer){}
