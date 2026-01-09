package net.p3pp3rf1y.sophisticateditemactions.common;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.p3pp3rf1y.sophisticatedcore.inventory.ItemStackKey;

public interface IDepositHandler {
	Vec3 getPosition();
	ItemMatchResult getItemMatch(ItemStackKey stackKey);
	ItemStack insertItem(ItemStack stack);
}
