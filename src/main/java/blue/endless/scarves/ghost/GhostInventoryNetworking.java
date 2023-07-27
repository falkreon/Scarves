package blue.endless.scarves.ghost;

import blue.endless.scarves.ScarvesMod;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;

public class GhostInventoryNetworking {
	public static void init() {
		ServerPlayNetworking.registerGlobalReceiver(ScarvesMod.GHOST_SLOT_MESSAGE, (server, player, handler, buf, sender) -> {
			// Get data from the packet
			final int itemSlot = buf.readVarInt();
			final NbtCompound stackData = buf.readNbt();
			
			//Swap to server thread
			server.execute(() -> {
				
				//Unpack data with server-specific values and pour it in
				ItemStack stack = ItemStack.fromNbt(stackData);
				if (player.currentScreenHandler instanceof GhostInventoryHolder gui) {
					gui.getGhostInventory().setGhostItem(itemSlot, stack);
					gui.getGhostInventory().markDirty();
				}
			});
		});
	}
	
	@Environment(EnvType.CLIENT)
	public static void initClient() {
		ClientPlayNetworking.registerGlobalReceiver(ScarvesMod.GHOST_SLOT_MESSAGE, (client, handler, buf, sender) -> {
			// Get data from the packet
			final int itemSlot = buf.readVarInt();
			final NbtCompound stackData = buf.readNbt();
			
			//Swap to client thread
			client.execute(() -> {
				
				//Unpack data with client-specific values and pour it in
				ItemStack stack = ItemStack.fromNbt(stackData);
				if (client.player.currentScreenHandler instanceof GhostInventoryHolder gui) {
					gui.getGhostInventory().setGhostItem(itemSlot, stack);
					gui.getGhostInventory().markDirty();
				}
			});
		});
	}
	
	public static void sendGhostItemToClient(ServerPlayerEntity player, int slot, ItemStack stack) {
		PacketByteBuf buf = PacketByteBufs.create();
		buf.writeVarInt(slot);
		buf.writeNbt(stack.writeNbt(new NbtCompound()));
		ServerPlayNetworking.send(player, ScarvesMod.GHOST_SLOT_MESSAGE, buf);
	}

	@Environment(EnvType.CLIENT)
	public static void sendGhostItemToServer(int slot, ItemStack stack) {
		PacketByteBuf buf = PacketByteBufs.create();
		buf.writeVarInt(slot);
		buf.writeNbt(stack.writeNbt(new NbtCompound()));
		
		ClientPlayNetworking.send(ScarvesMod.GHOST_SLOT_MESSAGE, buf);
	}
}
