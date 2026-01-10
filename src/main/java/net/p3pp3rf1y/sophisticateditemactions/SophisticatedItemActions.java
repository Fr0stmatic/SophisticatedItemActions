package net.p3pp3rf1y.sophisticateditemactions;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.p3pp3rf1y.sophisticateditemactions.client.ClientEventHandler;
import net.p3pp3rf1y.sophisticateditemactions.init.ModCompat;
import net.p3pp3rf1y.sophisticateditemactions.network.ItemActionsPacketHandler;

@Mod(SophisticatedItemActions.MOD_ID)
public class SophisticatedItemActions {
	public static final String MOD_ID = "sophisticateditemactions";

	public SophisticatedItemActions() {
		IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
		ModCompat.register();
		ModCompat.initCompats();
		if (FMLEnvironment.dist == Dist.CLIENT) {
			ClientEventHandler.registerHandlers(modBus);
		}
		modBus.addListener(SophisticatedItemActions::setup);
	}

	private static void setup(FMLCommonSetupEvent event) {
		ItemActionsPacketHandler.INSTANCE.init();
		ModCompat.compatsSetup();
	}

	public static ResourceLocation getRL(String regName) {
		return new ResourceLocation(getRegistryName(regName));
	}

	public static String getRegistryName(String regName) {
		return MOD_ID + ":" + regName;
	}
}
