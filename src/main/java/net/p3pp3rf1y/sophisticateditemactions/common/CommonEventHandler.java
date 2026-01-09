package net.p3pp3rf1y.sophisticateditemactions.common;

import net.neoforged.bus.api.IEventBus;
import net.p3pp3rf1y.sophisticateditemactions.init.ModPayloads;

public class CommonEventHandler {
	public static void registerHandlers(IEventBus modBus) {
		modBus.addListener(ModPayloads::registerPayloads);
	}
}
