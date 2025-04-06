package blue.endless.scarves;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import blue.endless.scarves.api.EntityAttachmentRegistry;
import blue.endless.scarves.api.FabricSquare;
import blue.endless.scarves.api.RepeatType;
import blue.endless.scarves.api.ScarfDesign;
import dev.emi.trinkets.api.TrinketComponent;
import dev.emi.trinkets.api.TrinketInventory;
import dev.emi.trinkets.api.TrinketItem;
import dev.emi.trinkets.api.TrinketsApi;
import io.github.queerbric.pride.PrideFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Tameable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;

public class ScarfItem extends TrinketItem {
	public static final String ID = "scarf";
	public static final int MAX_CREATIVE_SCARF_LENGTH = 8;
	
	//private static final Set<Identifier> TRY_TO_EQUIP = Set.of(Identifier.of("minecraft", "bee"), Identifier.of("minecraft", "fox"));
	
	public ScarfItem() {
		super(new Item.Settings()
				.component(ScarfDesign.COMPONENT, new ScarfDesign(RepeatType.FROM_START, 0, List.of()))
				.rarity(Rarity.UNCOMMON));
	}
	
	public static ScarfDesign createScarf(PrideFlag flag, int minLength) {
		List<FabricSquare> pattern = new ArrayList<>();
		for(int col : flag.getColors()) {
			pattern.add(new FabricSquare(Identifier.of("minecraft", "block/white_wool"), col | 0xFF_000000));
		}
		int repeatCount = 1;
		while(repeatCount * pattern.size() < minLength) repeatCount++;
		
		return new ScarfDesign(RepeatType.FROM_START, repeatCount, List.copyOf(pattern));
	}
	
	@Override
	public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
		//System.out.println("Clicked on "+Registries.ENTITY_TYPE.getId(entity.getType()));
		
		if (entity instanceof PlayerEntity) return ActionResult.FAIL;
		
		//if (TRY_TO_EQUIP.contains(Registries.ENTITY_TYPE.getId(entity.getType()))) {
		if (entity.getEntityWorld().isClient) return ActionResult.SUCCESS;
		
		if (EntityAttachmentRegistry.getSlotConfig(entity).isEmpty()) return ActionResult.FAIL;
		
		
		if (entity instanceof Tameable tameable) {
			//System.out.println("Owner: "+Optional.ofNullable(tameable.getOwner()).map(LivingEntity::getName).map(it -> it.asTruncatedString(255)).orElse(null));
			if (user != tameable.getOwner()) {
				return ActionResult.FAIL;
			}
		}
		
		Optional<TrinketComponent> component = TrinketsApi.getTrinketComponent(entity);
		if (component.isPresent()) {
			Map<String, Map<String, TrinketInventory>> inventoryMap = component.get().getInventory();
			Map<String, TrinketInventory> headGroup = inventoryMap.get("head");
			if (headGroup == null) {
				// System.out.println("No head group!");
			} else {
				
				TrinketInventory scarfInventory = headGroup.get("left_scarf");
				if (scarfInventory == null) {
					//System.out.println("No left_scarf!");
				} else {
					//Yeet the old item outta there
					ItemStack oldItem = scarfInventory.removeStack(0);
					scarfInventory.setStack(0, stack);
					user.setStackInHand(hand, oldItem);
					
					return ActionResult.SUCCESS;
				}
			}
		}
		
		//return ActionResult.SUCCESS;
		//}
		
		
		return ActionResult.PASS;
	}
}
