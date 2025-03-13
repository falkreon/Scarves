package blue.endless.scarves.api;

import java.util.ArrayList;
import java.util.List;

import com.mojang.serialization.Codec;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

/**
 * 
 */
public record ScarfSlotConfiguration(List<AnchoredSlot> slots) {
	
	public static final Codec<ScarfSlotConfiguration> CODEC = Codec.list(AnchoredSlot.CODEC).xmap(ScarfSlotConfiguration::new, ScarfSlotConfiguration::slots);
	public static final PacketCodec<PacketByteBuf, ScarfSlotConfiguration> PACKET_CODEC =
			PacketCodecs.<PacketByteBuf, AnchoredSlot, List<AnchoredSlot>>collection(ArrayList::new, AnchoredSlot.PACKET_CODEC)
			.xmap(
					(list) -> new ScarfSlotConfiguration(List.copyOf(list)),
					(it) -> it.slots
			);
}
