package net.p3pp3rf1y.sophisticateditemactions.client.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.p3pp3rf1y.sophisticatedcore.util.RandHelper;

import java.util.List;
import java.util.Map;

public class ItemTransferClientHandler {
	private ItemTransferClientHandler() {}

	public static void handleItemTransfers(Map<Vec3, List<ItemStack>> itemsTransferred, Vec3 playerPos, boolean fromPlayer) {
		LocalPlayer player = Minecraft.getInstance().player;
		Level level = player.level();
		itemsTransferred.forEach((pos, stacks) -> {
			Vec3 from = fromPlayer ? playerPos : pos;
			Vec3 to = fromPlayer ? pos : playerPos;
			for (ItemStack stack : stacks) {
				ItemFlightAnimator.startFlight(stack, from, to, level.getGameTime(), fromPlayer ? 10 : 5, level.getRandom());
			}
			float pitch = fromPlayer ? RandHelper.getRandomMinusOneToOne(level.random) * 0.1F + 0.2F : RandHelper.getRandomMinusOneToOne(level.random) * 1.4F + 2.0F;
			level.playSound(player, to.x(), to.y(), to.z(), SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.3F, pitch);
		});
	}
}
