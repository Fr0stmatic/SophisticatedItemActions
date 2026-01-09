package net.p3pp3rf1y.sophisticateditemactions.client.gui;

import net.p3pp3rf1y.sophisticatedcore.client.gui.utils.TranslationHelper;
import net.p3pp3rf1y.sophisticateditemactions.SophisticatedItemActions;

public class ItemActionsTranslationHelper extends TranslationHelper {
	public static final ItemActionsTranslationHelper INSTANCE = new ItemActionsTranslationHelper();

	public ItemActionsTranslationHelper() {
		super(SophisticatedItemActions.MOD_ID);
	}
}
