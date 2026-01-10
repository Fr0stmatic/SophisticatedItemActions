
package net.p3pp3rf1y.sophisticateditemactions.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent;
import net.p3pp3rf1y.sophisticatedcore.network.ISplittableMessage;
import net.p3pp3rf1y.sophisticateditemactions.client.render.ItemTransferClientHandler;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public record SyncItemTransfersMessage(Map<Vec3, List<ItemStack>> itemsTransferred, Vec3 playerPos,
									   boolean fromPlayer) implements ISplittableMessage {
	public static void encode(SyncItemTransfersMessage msg, FriendlyByteBuf packetBuffer) {
		packetBuffer.writeMap(msg.itemsTransferred, (buf, vec) -> {
			buf.writeDouble(vec.x());
			buf.writeDouble(vec.y());
			buf.writeDouble(vec.z());
		}, (buf, list) -> buf.writeCollection(list, FriendlyByteBuf::writeItem));
		packetBuffer.writeDouble(msg.playerPos.x());
		packetBuffer.writeDouble(msg.playerPos.y());
		packetBuffer.writeDouble(msg.playerPos.z());
		packetBuffer.writeBoolean(msg.fromPlayer);
	}

	public static SyncItemTransfersMessage decode(FriendlyByteBuf packetBuffer) {
		return new SyncItemTransfersMessage(packetBuffer.readMap(buf -> {
			double x = buf.readDouble();
			double y = buf.readDouble();
			double z = buf.readDouble();
			return new Vec3(x, y, z);
		}, buf -> buf.readList(FriendlyByteBuf::readItem)),
				new Vec3(packetBuffer.readDouble(), packetBuffer.readDouble(), packetBuffer.readDouble()),
				packetBuffer.readBoolean()
		);
	}

	static void onMessage(SyncItemTransfersMessage msg, Supplier<NetworkEvent.Context> contextSupplier) {
		NetworkEvent.Context context = contextSupplier.get();
		context.enqueueWork(() -> handleMessage(msg, context));
		context.setPacketHandled(true);
	}

	public static void handleMessage(SyncItemTransfersMessage payload, NetworkEvent.Context context) {
		ItemTransferClientHandler.handleItemTransfers(payload.itemsTransferred(), payload.playerPos(), payload.fromPlayer());
	}
}
