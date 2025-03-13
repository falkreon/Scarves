package blue.endless.scarves.api;

import java.util.Optional;

import org.joml.Vector3f;

import com.mojang.brigadier.StringReader;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import dev.emi.trinkets.api.TrinketComponent;
import dev.emi.trinkets.api.TrinketsApi;
import net.minecraft.command.argument.ItemSlotArgumentType;
import net.minecraft.component.ComponentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.dynamic.Codecs;

public record AnchoredSlot(String anchorPoint, Vector3f offset, Identifier slot) {
	public static final String MINECRAFT_SOURCE = "minecraft";
	public static final String TRINKETS_SOURCE = "trinkets";
	
	public static final Codec<AnchoredSlot> CODEC = RecordCodecBuilder.create((instance) -> {
		return instance.group(
				Codec.STRING.fieldOf("anchor_point").forGetter(AnchoredSlot::anchorPoint),
				Codecs.VECTOR_3F.fieldOf("offset").forGetter(AnchoredSlot::offset),
				Identifier.CODEC.fieldOf("slot").forGetter(AnchoredSlot::slot)
				).apply(instance, AnchoredSlot::new);
	});
	
	public static final PacketCodec<PacketByteBuf, AnchoredSlot> PACKET_CODEC = PacketCodec.of(AnchoredSlot::write, AnchoredSlot::new);
	public static final ComponentType<AnchoredSlot> COMPONENT = ComponentType.<AnchoredSlot>builder()
			.codec(CODEC)
			.packetCodec(PACKET_CODEC)
			.build();
	
	
	public AnchoredSlot(PacketByteBuf buf) {
		this(buf.readString(), buf.readVector3f(), buf.readIdentifier());
	}
	
	public void write(PacketByteBuf buf) {
		buf.writeString(anchorPoint);
		buf.writeVector3f(offset);
		buf.writeIdentifier(slot);
	}
	
	private ItemStack getStack(Entity subject) {
		if (slot.getNamespace().equals(MINECRAFT_SOURCE)) {
			try {
				int slotId = ItemSlotArgumentType.itemSlot().parse(new StringReader(slot.getPath()));
				StackReference ref = subject.getStackReference(slotId);
				return ref.get();
			} catch (Throwable t) {
				return ItemStack.EMPTY;
			}
		} else if (slot.getNamespace().equals(TRINKETS_SOURCE) && subject instanceof LivingEntity living) {
			Optional<TrinketComponent> optionalComponent = TrinketsApi.getTrinketComponent(living);
			if (optionalComponent.isEmpty()) return ItemStack.EMPTY;
			
			for(Pair<dev.emi.trinkets.api.SlotReference, ItemStack> pair : optionalComponent.get().getAllEquipped()) {
				if (pair.getLeft().getId().equals(slot.getPath())) return pair.getRight();
			}
		} else {
			return ItemStack.EMPTY;
		}
		
		return ItemStack.EMPTY;
	}
	
	/**
	 * Gets the ScarfDesign in this slot, or empty if there is no item, or no design in this slot.
	 * @param subject
	 * @return
	 */
	public Optional<ScarfDesign> getScarfDesign(Entity subject) {
		ItemStack stack = getStack(subject);
		if (stack.isEmpty()) return Optional.empty();
		return Optional.ofNullable(stack.get(ScarfDesign.COMPONENT));
	}
}
