package blue.endless.scarves.gui;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Decoder;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Encoder;

public class EmptyCodecs {
	public static final EmptyDecoder DECODER = new EmptyDecoder();
	public static final EmptyEncoder ENCODER = new EmptyEncoder();
	
	public static class EmptyDecoder implements Decoder<Void> {
		@Override
		public <T> DataResult<Pair<Void, T>> decode(DynamicOps<T> ops, T input) {
			return null;
		}
	}
	
	public static class EmptyEncoder implements Encoder<Void> {
		@Override
		public <T> DataResult<T> encode(Void input, DynamicOps<T> ops, T prefix) {
			return DataResult.success(prefix);
		}
		
	}
}
