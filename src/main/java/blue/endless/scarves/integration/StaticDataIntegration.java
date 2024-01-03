package blue.endless.scarves.integration;

import java.util.Optional;

import blue.endless.jankson.Jankson;
import blue.endless.jankson.JsonElement;
import blue.endless.jankson.JsonObject;
import blue.endless.jankson.JsonPrimitive;
import blue.endless.scarves.ScarvesMod;
import blue.endless.scarves.api.FabricSquare;
import blue.endless.scarves.api.FabricSquareRegistry;
import gay.debuggy.staticdata.api.StaticData;
import net.minecraft.block.MapColor;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

public class StaticDataIntegration {
	public static void init() {
		Jankson jankson = Jankson.builder().build();
		for(var dataItem : StaticData.getDataInDirectory(new Identifier("scarves:fabric_squares"), true)) {
			try {
				JsonElement elem = jankson.loadElement(dataItem.getAsStream());
				if (elem instanceof JsonObject obj) {
					obj.forEach((itemId, squareSpec) -> {
						Item item = Registries.ITEM.get(new Identifier(itemId));
						getFabricSquare(squareSpec, getColorHint(item), getDefaultEmissive(item)).ifPresent(square -> {
							FabricSquareRegistry.register(item, square);
						});
					});
				}
			} catch (Throwable t) {
				ScarvesMod.LOGGER.error("Could not load static data \"" + dataItem.getResourceId() + "\"", t);
			}
		}
	}
	
	public static Optional<FabricSquare> getFabricSquare(JsonElement elem, int defaultColor, boolean defaultEmissive) {
		if (elem instanceof JsonPrimitive prim) {
			if (prim.getValue() instanceof String str) {
				return Optional.of(new FabricSquare(new Identifier(str), 4, 4, 0xFF_FFFFFF, defaultColor, defaultEmissive));
			} else if (prim.getValue() instanceof Long l) {
				return Optional.of(new FabricSquare(new Identifier("minecraft:block/white_wool"), 4, 4, l.intValue(), l.intValue(), defaultEmissive));
			} else {
				return Optional.empty();
			}
		} else if (elem instanceof JsonObject obj) {
			String textureId = obj.get(String.class, "texture");
			if (textureId == null) return Optional.empty();
			int xofs = obj.getInt("x", 4);
			int yofs = obj.getInt("y", 4);
			int color = parseColor(obj.get("color"), 0xFF_FFFFFF);
			int colorHint = parseColor(obj.get("color_hint"), defaultColor);
			boolean emissive = obj.getBoolean("emissive", defaultEmissive);
			
			return Optional.of(new FabricSquare(new Identifier(textureId), xofs, yofs, color, colorHint, emissive));
		} else {
			return Optional.empty();
		}
	}
	
	public static int getColorHint(Item item) {
		if (item instanceof BlockItem blockItem) {
			int baseColor = blockItem.getBlock().getDefaultMapColor().getRenderColor(MapColor.Brightness.NORMAL);
			
			int b = (baseColor >> 16) & 0xFF;
			int g = (baseColor >> 8) & 0xFF;
			int r = baseColor & 0xFF;
			
			return (r << 16) | (g << 8) | b;
		} else {
			return 0xFF_FFFFFF;
		}
	}
	
	public static boolean getDefaultEmissive(Item item) {
		if (item instanceof BlockItem blockItem) {
			return blockItem.getBlock().getDefaultState().getLuminance() >= 8;
		}
		return false;
	}
	
	public static int parseColor(JsonElement elem, int defaultColor) {
		if (elem instanceof JsonPrimitive prim) {
			if (prim.getValue() instanceof String str) {
				if (str.startsWith("#")) str = str.substring(1);
				if (str.length() == 3) {
					int r = hexDigit(str.charAt(0)); r = r | (r << 4);
					int g = hexDigit(str.charAt(1)); g = g | (g << 4);
					int b = hexDigit(str.charAt(2)); b = b | (b << 4);
					return 0xFF_000000 | (r << 16) | (g << 8) | b;
				} else if (str.length() == 6) {
					int r = hexDigit(str.charAt(0)) << 4 | hexDigit(str.charAt(1));
					int g = hexDigit(str.charAt(2)) << 4 | hexDigit(str.charAt(3));
					int b = hexDigit(str.charAt(4)) << 4 | hexDigit(str.charAt(5));
					return 0xFF_000000 | (r << 16) | (g << 8) | b;
				} else {
					return defaultColor;
				}
			} else if (prim.getValue() instanceof Long l) {
				return l.intValue() | 0xFF_000000;
			}
		}
		
		return defaultColor;
	}
	
	private static final char[] HEX_DIGITS = {
		'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'
	};
	private static final int hexDigit(char ch) {
		ch = Character.toLowerCase(ch);
		for(int i=0; i<HEX_DIGITS.length; i++) {
			if (HEX_DIGITS[i] == ch) return i;
		}
		return 0;
	}
}
