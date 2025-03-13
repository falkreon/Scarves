package blue.endless.scarves.api;

import net.minecraft.util.StringIdentifiable;

public enum RepeatType implements StringIdentifiable {
	/** For pattern ABCD, repeats will look like ABCDABCDABCD. Length will be squares.size() times repeatCount. */
	FROM_START("from_start"),
	/** For pattern ABCD, repeats will look like ABCDCBABCD. Length will be <code>(squares.size()-1) * repeatCount + 1</code>. */
	PING_PONG("ping_pong"),
	/**
	 * For pattern ABCD, repeats will look like ACBDBCADBACD. Length will be squares.size() times repeatCount, and each
	 * color is guaranteed to occur once per repetition but in random order. It is possible for two squares of the same
	 * color to be adjacent due to the randomness.
	 */
	RANDOM("random");
	
	private final String name;
	
	RepeatType(String name) { this.name = name; }

	@Override
	public String asString() {
		return name;
	}
}
