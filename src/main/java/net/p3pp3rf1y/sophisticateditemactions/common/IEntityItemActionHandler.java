package net.p3pp3rf1y.sophisticateditemactions.common;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.p3pp3rf1y.sophisticatedcore.inventory.ItemStackKey;

import java.util.Optional;

public interface IEntityItemActionHandler {
	ResourceLocation id();

	boolean canActOn(Entity entity);

	ItemMatchResult getItemMatch(ItemStackKey stackKey, Entity entity);

	Optional<IDepositHandler> getDepositHandler(Entity entity);

	Optional<IRestockHandler> getRestockHandler(Entity entity);
}
