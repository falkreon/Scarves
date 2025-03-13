package blue.endless.scarves.api;

import java.util.Optional;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.component.ComponentType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;

//import org.spongepowered.include.com.google.common.base.Preconditions;

import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.Codecs;

/**
 * @param id        The identifier of the TexturedAtlasSprite to use for in-world rendering, such as
 *                  "minecraft:block/white_wool"
 * @param xofs      The x coordinate offset of the 8x8 square within the 16x16 sprite
 * @param yofs      The y coordinate offset of the 8x8 square within the 16x16 sprite
 * @param color     A packed int ARGB to tint the square when rendering in-world
 * @param colorHint A packed int ARGB to tint part of a scarf item icon to represent this square
 * @param emissive  If true, the square is emissive. Emissive squares always render fully bright, and in certain
 *                  shaders, bloom or cast light on nearby surfaces.
 */
public record FabricSquare(Identifier id, int xofs, int yofs, int color, int colorHint, boolean emissive) {
	public static final Codec<FabricSquare> CODEC = RecordCodecBuilder.create((instance) ->
		instance.group(
			Identifier.CODEC.fieldOf("Id").forGetter(FabricSquare::id),
			Codecs.NONNEGATIVE_INT.optionalFieldOf("X", 4).forGetter(FabricSquare::xofs),
			Codecs.NONNEGATIVE_INT.optionalFieldOf("Y", 4).forGetter(FabricSquare::yofs),
			Codecs.rangedInt(Integer.MIN_VALUE, Integer.MAX_VALUE).optionalFieldOf("Color", 0xFF_FFFFFF).forGetter(FabricSquare::color),
			Codecs.rangedInt(Integer.MIN_VALUE, Integer.MAX_VALUE).optionalFieldOf("ColorHint", 0xFF_FFFFFF).forGetter(FabricSquare::colorHint),
			Codec.BOOL.optionalFieldOf("Emissive", false).forGetter(FabricSquare::emissive)
		).apply(instance, FabricSquare::new));
	
	public static final PacketCodec<PacketByteBuf, FabricSquare> PACKET_CODEC = PacketCodec.of(FabricSquare::write, FabricSquare::new);
	
	public static final ComponentType<FabricSquare> COMPONENT = ComponentType.<FabricSquare>builder().codec(CODEC).packetCodec(PACKET_CODEC).build();
	
	public FabricSquare(PacketByteBuf buf) {
		this(
				buf.readIdentifier(),	
				buf.readVarInt(),
				buf.readVarInt(),
				buf.readInt(),
				buf.readInt(),
				buf.readBoolean()
			);
	}
	
	public FabricSquare(String id) {
		this(Identifier.of(id), 4, 4, 0xFF_FFFFFF, 0xFF_FFFFFF, false);
	}
	
	/**
	 * Creates a fabric square of the specified block/item on the block atlas texture, and uses the middle 8x8
	 * @param id a texture id, such as "block/white_wool"
	 * @return a fabric square representing the middle 8x8 with no tint.
	 */
	public FabricSquare(Identifier id) {
		this(id, 4, 4, 0xFF_FFFFFF, 0xFF_FFFFFF, false);
	}
	
	public FabricSquare(Identifier id, int color) {
		this(id, 4, 4, color, color, false);
	}
	
	public FabricSquare fullbright() {
		return new FabricSquare(id, xofs, yofs, color, color, true);
	}
	
	public FabricSquare withColor(int color) {
		return new FabricSquare(id, xofs, yofs, color, color, emissive);
	}
	
	public FabricSquare withOffset(int xofs, int yofs) {
		//Preconditions.checkArgument(xofs>=0 && yofs>=0, "Negative offsets are not allowed, because they will spill over onto other textures.");
		//Preconditions.checkArgument(xofs<8 && yofs<8, "Offsets of more than 7 are not allowed, because they will spill over onto other textures.");
		return new FabricSquare(id, xofs, yofs, color, color, emissive);
	}
	
	@Override
	public String toString() {
		StringBuilder result = new StringBuilder("FabricSquare[");
		result.append("id=" + id);
		result.append(", xofs=" + xofs);
		result.append(", yofs=" + yofs);
		result.append(", color=0x" + Integer.toHexString(color));
		result.append(", colorHint=0x" + Integer.toHexString(colorHint));
		result.append(", emissive=" + emissive);
		result.append("]");
		return result.toString();
	}
	
	public NbtCompound toCompound() {
		NbtCompound result = new NbtCompound();
		result.putString("Id", id.toString());
		result.putInt("X", xofs);
		result.putInt("Y", yofs);
		result.putInt("Color", color);
		result.putInt("ColorHint", colorHint);
		result.putBoolean("Emissive", emissive);
		
		return result;
	}
	
	public static FabricSquare fromCompound(NbtCompound tag) {
		String id = tag.getString("Id");
		int xofs = (tag.contains("X", NbtElement.INT_TYPE)) ? tag.getInt("X") : 4;
		int yofs = (tag.contains("Y", NbtElement.INT_TYPE)) ? tag.getInt("Y") : 4;
		int color = (tag.contains("Color", NbtElement.INT_TYPE)) ? tag.getInt("Color") : 0xFF_FFFFFF;
		int colorHint = (tag.contains("ColorHint", NbtElement.INT_TYPE)) ? tag.getInt("ColorHint") : color;
		boolean emissive = tag.getBoolean("Emissive");
		
		return new FabricSquare(Identifier.of(id), xofs, yofs, color, colorHint, emissive);
	}
	
	public void write(PacketByteBuf buf) {
		buf.writeIdentifier(id);
		buf.writeVarInt(xofs);
		buf.writeVarInt(yofs);
		buf.writeInt(color);
		buf.writeInt(colorHint);
		buf.writeBoolean(emissive);
	}
}