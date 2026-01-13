package net.p3pp3rf1y.sophisticateditemactions.compat.recipeviewers.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.resources.Identifier;
import net.p3pp3rf1y.sophisticateditemactions.SophisticatedItemActions;

@SuppressWarnings("unused")
@JeiPlugin
public class ItemActionsPlugin implements IModPlugin {
	private static final Identifier ID = SophisticatedItemActions.getIdentifier("default");

	@Override
	public Identifier getPluginUid() {
		return ID;
	}

	@Override
	public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
		JeiClientCompat.setRuntime(jeiRuntime);
	}
}
