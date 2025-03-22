package blue.endless.scarves.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

public class EntityAttachmentRegistry {
	private static Map<Identifier, List<AnchoredSlot>> data = new HashMap<>();
	
	public static List<AnchoredSlot> getSlotConfig(Entity entity) {
		Identifier id = Registries.ENTITY_TYPE.getId(entity.getType());
		return data.getOrDefault(id, List.of());
	}
	
	public static List<AnchoredSlot> getSlotConfig(EntityType<?> entityType) {
		Identifier id = Registries.ENTITY_TYPE.getId(entityType);
		return data.getOrDefault(id, List.of());
	}
	
	public static List<AnchoredSlot> getSlotConfig(Identifier typeId) {
		return data.getOrDefault(typeId, List.of());
	}
	
	public static void addSlotConfig(Identifier entityTypeId, List<AnchoredSlot> slotConfig) {
		List<AnchoredSlot> existing = data.get(entityTypeId);
		if (existing != null) {
			ArrayList<AnchoredSlot> combination = new ArrayList<>();
			combination.addAll(existing);
			combination.addAll(slotConfig);
			data.put(entityTypeId, List.copyOf(combination));
		} else {
			data.put(entityTypeId, List.copyOf(slotConfig));
		}
	}
	
	public static void addAnchoredSlot(Identifier entityTypeId, AnchoredSlot slot) {
		List<AnchoredSlot> existing = data.getOrDefault(entityTypeId, List.of());
		ArrayList<AnchoredSlot> combination = new ArrayList<>();
		combination.addAll(existing);
		data.put(entityTypeId, List.copyOf(combination));
	}
	
	public static Set<Identifier> getRegisteredTypes() {
		return data.keySet();
	}
}
