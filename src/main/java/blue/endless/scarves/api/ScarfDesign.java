package blue.endless.scarves.api;

import java.util.ArrayList;
import java.util.List;
import java.util.random.RandomGenerator;
import java.util.random.RandomGeneratorFactory;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.ListCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import blue.endless.scarves.ScarvesMod;
import net.minecraft.component.ComponentType;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.dynamic.Codecs;

/**
 * Designates the object it's attached to as having a scarf dangling from it, and describes the pattern and appearance.
 * 
 * @param repeatType  How the scarf pattern will repeat when rendered in-world
 * @param repeatCount How many times the scarf pattern will repeat when rendered in-world. Affects the length of the scarf.
 * @param squares     The sequence of FabricSquares that govern both the scarf's in-world appearance and item colors.
 *                    The length of this sequence also affects the length of the scarf.
 */
public record ScarfDesign(RepeatType repeatType, int repeatCount, List<FabricSquare> squares) {
	public static final Codec<ScarfDesign> CODEC = RecordCodecBuilder.create((instance) ->
		instance.group(
				StringIdentifiable.createCodec(RepeatType::values).fieldOf("RepeatType").forGetter(ScarfDesign::repeatType),
				Codecs.POSITIVE_INT.fieldOf("RepeatCount").forGetter(ScarfDesign::repeatCount),
				new ListCodec<>(FabricSquare.CODEC, 0, Integer.MAX_VALUE).fieldOf("Pattern").forGetter(ScarfDesign::squares)
				).apply(instance, ScarfDesign::new)
	);
	
	public static final PacketCodec<PacketByteBuf, ScarfDesign> PACKET_CODEC = PacketCodec.of(ScarfDesign::write, ScarfDesign::new);
	
	public static final ComponentType<ScarfDesign> COMPONENT = ComponentType.<ScarfDesign>builder().codec(CODEC).packetCodec(PACKET_CODEC).build();
	
	private static RandomGeneratorFactory<RandomGenerator> RANDOM_FACTORY = null;
	
	static {
		try {
			RANDOM_FACTORY = RandomGeneratorFactory.of("L32X64MixRandom");
		} catch (Throwable t) {
			try {
				RANDOM_FACTORY = RandomGeneratorFactory.getDefault();
			} catch (Throwable u) {
				try {
					RANDOM_FACTORY = RandomGeneratorFactory.all().findFirst().get();
				} catch (Throwable v) {
					ScarvesMod.LOGGER.error("Host system has no RandomGenerator algorithms at all - no factories exist. Falling back to java.util.Random");
				}
			}
		}
		
	}
	
	public ScarfDesign(PacketByteBuf buf) {
		this(
				buf.readEnumConstant(RepeatType.class),
				buf.readVarInt(),
				buf.readList(FabricSquare.PACKET_CODEC)
			);
	}
	
	public void write(PacketByteBuf buf) {
		buf.writeEnumConstant(repeatType);
		buf.writeVarInt(repeatCount);
		buf.writeCollection(squares, FabricSquare.PACKET_CODEC);
	}
	
	public int getLength() {
		if (repeatType == RepeatType.PING_PONG) return (squares.size()-1) * repeatCount + 1;
		return repeatCount * squares.size();
	}
	
	public FabricSquare get(int i) {
		if (repeatType == RepeatType.PING_PONG) {
			int effectiveRepeat = squares.size() + (squares.size() - 2);
			int effectiveIndex = i % effectiveRepeat;
			if (effectiveIndex < squares.size()) return squares.get(effectiveIndex);
			
			effectiveIndex -= squares.size();
			effectiveIndex = (squares.size() - 2) - effectiveIndex;
			return squares.get(effectiveIndex);
		} else if (repeatType == RepeatType.RANDOM) {
			if (RANDOM_FACTORY == null) {
				int square = ((int) (Math.random() * squares.size())) % squares.size();
				return squares.get(square);
			}
			int effectiveBin = i / squares.size();
			int effectiveIndex = i % squares.size();
			ArrayList<FabricSquare> bin = new ArrayList<>(squares);
			RandomGenerator binRandom = RANDOM_FACTORY.create(effectiveBin);
			FabricSquare result = bin.remove(binRandom.nextInt(bin.size()));
			for(int j = 0; j < effectiveIndex; j++) {
				if (!squares.isEmpty()) result = bin.remove(binRandom.nextInt(bin.size()));
			}
			return result;
		}
		
		return squares.get(i % squares.size());
	}
}