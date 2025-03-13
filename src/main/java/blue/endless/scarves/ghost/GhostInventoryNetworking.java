package blue.endless.scarves.ghost;

import blue.endless.scarves.ScarvesMod;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class GhostInventoryNetworking {
	
	public static void init() {
		PayloadTypeRegistry.playC2S().register(Payload.ID, Payload.CODEC);
		PayloadTypeRegistry.playS2C().register(Payload.ID, Payload.CODEC);
		
		ServerPlayNetworking.registerGlobalReceiver(Payload.ID, (payload, context) -> {
			if (payload.slot() < 0) return;
			
			context.server().execute(() -> {
				if (context.player().currentScreenHandler instanceof GhostInventoryHolder gui) {
					gui.getGhostInventory().setGhostItem(payload.slot(), payload.stack());
					gui.getGhostInventory().markDirty();
				}
			});
		});
	}
	
	@Environment(EnvType.CLIENT)
	public static void initClient() {
		
		
		ClientPlayNetworking.registerGlobalReceiver(Payload.ID, (payload, context) -> {
			if (payload.slot() < 0) return;
			
			context.client().execute(() -> {
				if (context.player().currentScreenHandler instanceof GhostInventoryHolder gui) {
					gui.getGhostInventory().setGhostItem(payload.slot(), payload.stack());
					gui.getGhostInventory().markDirty();
				}
			});
		});
	}
	
	public static void sendGhostItemToClient(ServerPlayerEntity player, int slot, ItemStack stack) {
		ServerPlayNetworking.send(player, new Payload(slot, stack));
	}

	@Environment(EnvType.CLIENT)
	public static void sendGhostItemToServer(int slot, ItemStack stack) {
		ClientPlayNetworking.send(new Payload(slot, stack));
	}
	
	public static record Payload(int slot, ItemStack stack) implements CustomPayload {
		public static final CustomPayload.Id<Payload> ID = new CustomPayload.Id<>(Identifier.of(ScarvesMod.MODID, "ghost_slot"));
		public static final PacketCodec<RegistryByteBuf, Payload> CODEC = PacketCodec.of(Payload::write, Payload::new);
		
		@Override
		public Id<? extends CustomPayload> getId() {
			return ID;
		}
		
		public Payload(RegistryByteBuf buf) {
			this(PacketCodecs.VAR_INT.decode(buf), ItemStack.OPTIONAL_PACKET_CODEC.decode(buf));
		}
		
		public void write(RegistryByteBuf buf) {
			PacketCodecs.VAR_INT.encode(buf, slot);
			ItemStack.OPTIONAL_PACKET_CODEC.encode(buf, stack);
		}
	}
}
