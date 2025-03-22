package blue.endless.scarves.integration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import blue.endless.jankson.Jankson;
import blue.endless.jankson.JsonArray;
import blue.endless.jankson.JsonElement;
import blue.endless.jankson.JsonObject;
import blue.endless.jankson.JsonPrimitive;
import blue.endless.scarves.ScarvesMod;
import blue.endless.scarves.api.AnchoredSlot;
import blue.endless.scarves.api.EntityAttachmentRegistry;
import blue.endless.scarves.api.FabricSquare;
import blue.endless.scarves.api.FabricSquareRegistry;
import gay.debuggy.staticdata.api.StaticData;
import net.fabricmc.fabric.api.event.registry.RegistryEntryAddedCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.MapColor;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.joml.Vector3f;

public class StaticDataIntegration {
	private static Map<Identifier, JsonElement> deferrals = new HashMap<>();
	
	public static void init() {
		Jankson jankson = Jankson.builder().build();
		for(var dataItem : StaticData.getDataInDirectory(Identifier.of("scarves:fabric_squares"), true)) {
			try {
				JsonElement elem = jankson.loadElement(dataItem.getAsStream());
				if (elem instanceof JsonObject obj) {
					obj.forEach((itemIdString, squareSpec) -> {
						Identifier itemId = Identifier.of(itemIdString);
						Item item = Registries.ITEM.get(itemId);
						if (item == null || item == Items.AIR) {
							//defer
							deferrals.put(itemId, squareSpec);
						} else {
							getFabricSquare(squareSpec, getColorHint(item), getDefaultEmissive(item)).ifPresent(square -> {
								FabricSquareRegistry.register(item, square);
							});
						}
					});
				}
			} catch (Throwable t) {
				ScarvesMod.LOGGER.error("[StaticData] Could not load fabric square: \"" + dataItem.getResourceId() + "\"", t);
			}
		}
		
		RegistryEntryAddedCallback.event(Registries.ITEM).register((rawId, id, item) -> {
			JsonElement elem = deferrals.remove(id);
			if (elem == null) return;
			
			try {
				getFabricSquare(elem, getColorHint(item), getDefaultEmissive(item)).ifPresent(square -> {
					FabricSquareRegistry.register(item, square);
				});
			} catch (Throwable t) {
				ScarvesMod.LOGGER.error("[StaticData] Could not load deferred item \"" + id + "\"", t);
			}
		});
		
		for(var dataItem : StaticData.getDataInDirectory(Identifier.of("scarves:slot_configs"), true)) {
			try {
				JsonElement rootElem = jankson.loadElement(dataItem.getAsStream());
				if (rootElem instanceof JsonObject rootObj) {
					rootObj.forEach((String entityTypeIdString, JsonElement slotSpec) -> {
						Identifier typeId = Identifier.of(entityTypeIdString);
						List<AnchoredSlot> slots = new ArrayList<>();
						if (slotSpec instanceof JsonObject configObj) {
							String ifLoaded = configObj.get(String.class, "ifLoaded");
							if (ifLoaded != null) if (!FabricLoader.getInstance().isModLoaded(ifLoaded)) return;
							
							JsonElement slotsElem = configObj.get("slots");
							if (slotsElem instanceof JsonArray slotsArray) {
								// Array of AnchoredSlot
								for(JsonElement elem : slotsArray) {
									Optional<AnchoredSlot> slot = getAnchoredSlot(elem);
									if (slot.isEmpty()) {
										ScarvesMod.LOGGER.warn("[StaticData] Bad AnchoredSlot for entity \""+typeId.toString()+"\" supplied by namespace "+dataItem.getModId());
									} else {
										slots.add(slot.get());
									}
								}
								
							} else if (slotsElem instanceof JsonObject slotsObject) {
								// Just one AnchoredSlot
								Optional<AnchoredSlot> slot = getAnchoredSlot(slotsObject);
								if (slot.isEmpty()) {
									ScarvesMod.LOGGER.warn("[StaticData] Bad AnchoredSlot for entity \""+typeId.toString()+"\" supplied by namespace "+dataItem.getModId());
								} else {
									slots.add(slot.get());
								}
							}
						} else if (slotSpec instanceof JsonArray configArray) {
							// Just a bare array of AnchoredSlot
							
							for(JsonElement elem : configArray) {
								Optional<AnchoredSlot> slot = getAnchoredSlot(elem);
								if (slot.isEmpty()) {
									ScarvesMod.LOGGER.warn("[StaticData] Bad AnchoredSlot for entity \""+typeId.toString()+"\" supplied by namespace "+dataItem.getModId());
								} else {
									slots.add(slot.get());
								}
							}
						}
						
						if (!slots.isEmpty()) {
							EntityAttachmentRegistry.addSlotConfig(typeId, slots);
						}
					});
				}
			} catch (Throwable t) {
				ScarvesMod.LOGGER.error("[StaticData] Could not load slot config: \"" + dataItem.getResourceId() + "\"", t);
			}
		}
		
		Set<Identifier> configured = EntityAttachmentRegistry.getRegisteredTypes();
		ScarvesMod.LOGGER.info("[StaticData] Entity attachment registrations processed for the following entities: "+configured.toString());
		
	}
	
	public static Optional<AnchoredSlot> getAnchoredSlot(JsonElement elem) {
		if (elem instanceof JsonObject obj) {
			try {
				String anchorPoint = obj.get(String.class, "anchor_point");
				if (anchorPoint == null) anchorPoint = "";
				String slot = obj.get(String.class, "slot");
				if (slot == null) return Optional.empty();
				
				Vector3f offset = new Vector3f();
				JsonElement offsetElem = obj.get("offset");
				if (offsetElem instanceof JsonArray arr && arr.size() == 3) {
					offset = new Vector3f(
							arr.getFloat(0, 0f),
							arr.getFloat(1, 0f),
							arr.getFloat(2, 0f)
							);
				}
				
				return Optional.of(new AnchoredSlot(anchorPoint, offset, Identifier.of(slot)));
			} catch (Throwable t) {
				return Optional.empty();
			}
		}
		
		return Optional.empty();
	}
	
	public static Optional<FabricSquare> getFabricSquare(JsonElement elem, int defaultColor, boolean defaultEmissive) {
		if (elem instanceof JsonPrimitive prim) {
			if (prim.getValue() instanceof String str) {
				return Optional.of(new FabricSquare(Identifier.of(str), 4, 4, 0xFF_FFFFFF, defaultColor, defaultEmissive));
			} else if (prim.getValue() instanceof Long l) {
				return Optional.of(new FabricSquare(Identifier.of("minecraft:block/white_wool"), 4, 4, l.intValue(), l.intValue(), defaultEmissive));
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
			
			return Optional.of(new FabricSquare(Identifier.of(textureId), xofs, yofs, color, colorHint, emissive));
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
