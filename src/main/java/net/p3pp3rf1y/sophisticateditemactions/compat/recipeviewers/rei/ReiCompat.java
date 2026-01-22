package net.p3pp3rf1y.sophisticateditemactions.compat.recipeviewers.rei;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.p3pp3rf1y.sophisticatedcore.compat.ICompat;

public class ReiCompat implements ICompat {
	@Override
	public void setup() {
		if (FMLEnvironment.dist == Dist.CLIENT) {
			ReiClientCompat.init();
		}
	}
}
