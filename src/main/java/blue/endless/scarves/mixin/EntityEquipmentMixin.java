package blue.endless.scarves.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import blue.endless.scarves.ScarvesItems;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;

@Mixin(LivingEntity.class)
public class EntityEquipmentMixin {

	
	@Inject(at = { @At("HEAD") }, method = "getPreferredEquipmentSlot", cancellable = true)
	private static EquipmentSlot getPreferredEquipmentSlot(ItemStack stack, CallbackInfoReturnable<EquipmentSlot> ci) {
		if (stack.isOf(ScarvesItems.SCARF_STAPLER)) ci.setReturnValue(EquipmentSlot.HEAD);
		
		return null;
	}
}
