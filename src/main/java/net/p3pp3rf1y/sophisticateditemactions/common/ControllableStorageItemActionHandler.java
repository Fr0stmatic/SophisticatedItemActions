package net.p3pp3rf1y.sophisticateditemactions.common;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.p3pp3rf1y.sophisticatedcore.SophisticatedCore;
import net.p3pp3rf1y.sophisticatedcore.controller.IControllableStorage;
import net.p3pp3rf1y.sophisticatedcore.inventory.ISlotTracker;
import net.p3pp3rf1y.sophisticatedcore.inventory.ItemStackKey;
import net.p3pp3rf1y.sophisticatedcore.util.InventoryHelper;

public class ControllableStorageItemActionHandler implements IBlockEntityItemActionHandler<IControllableStorage> {
	public static final ControllableStorageItemActionHandler INSTANCE = new ControllableStorageItemActionHandler();
	public static final Identifier ID = SophisticatedCore.getIdentifier("controllable_storage");

	@Override
	public ItemMatchResult getItemMatch(ItemStackKey stackKey, IControllableStorage storage) {
		ISlotTracker slotTracker = storage.getStorageWrapper().getInventoryHandler().getSlotTracker();
		return getItemMatchResult(stackKey, slotTracker, false);
	}

	private static ItemMatchResult getItemMatchResult(ItemStackKey stackKey, ISlotTracker slotTracker, boolean includeMemorizedAndFiltered) {
		if (slotTracker.getPartialStacks().contains(stackKey) || slotTracker.getFullStacks().contains(stackKey) || (includeMemorizedAndFiltered && slotTracker.hasExactStackMemorized(stackKey))) {
			return ItemMatchResult.MATCHING_STACK;
		} else if (slotTracker.getItems().contains(stackKey.stack().getItem()) || (includeMemorizedAndFiltered && slotTracker.hasItemMemorizedOrFiltered(stackKey.stack().getItem()))) {
			return ItemMatchResult.MATCHING_ITEM;
		}
		return ItemMatchResult.NO_MATCH;
	}

	@Override
	public Class<IControllableStorage> getObjectClass() {
		return IControllableStorage.class;
	}

	@Override
	public Identifier id() {
		return ID;
	}

	@Override
	public IRestockHandler getRestockHandler(IControllableStorage storage) {
		return new IRestockHandler() {
			@Override
			public Vec3 getPosition() {
				return Vec3.atCenterOf(storage.getStorageBlockPos());
			}

			@Override
			public int extractItem(ItemStack stack) {
				return InventoryHelper.extract(storage.getStorageWrapper().getInventoryForInputOutput(), stack);
			}
		};
	}

	@Override
	public BlockPos getDepositPosToActOn(BlockPos pos, IControllableStorage controllableStorage) {
		return controllableStorage.getControllerPos().orElse(pos);
	}

	@Override
	public IDepositHandler getDepositHandler(IControllableStorage storage) {
		Vec3 center = Vec3.atCenterOf(storage.getStorageBlockPos());
		return new IDepositHandler() {
			@Override
			public Vec3 getPosition() {
				return center;
			}

			@Override
			public ItemMatchResult getItemMatch(ItemStackKey stackKey) {
				ISlotTracker slotTracker = storage.getStorageWrapper().getInventoryHandler().getSlotTracker();
				return getItemMatchResult(stackKey, slotTracker, true);
			}

			@Override
			public int insertItem(ItemStack stack) {
				return InventoryHelper.insert(storage.getStorageWrapper().getInventoryForInputOutput(), stack);
			}
		};
	}
}
