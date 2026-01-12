package net.p3pp3rf1y.sophisticateditemactions.common;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

public interface IRestockHandler {
	Vec3 getPosition();
	int extractItem(ItemStack stack);
}
