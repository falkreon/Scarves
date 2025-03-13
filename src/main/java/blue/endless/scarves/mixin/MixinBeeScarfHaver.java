package blue.endless.scarves.mixin;

import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.google.common.collect.ImmutableList;

import blue.endless.scarves.api.AnchoredSlot;
import blue.endless.scarves.client.IScarfHaver;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.util.Identifier;

@Mixin(BeeEntity.class)
public class MixinBeeScarfHaver {
	
	@Inject(method="<init>", at = @At("TAIL"))
	public void afterInit(CallbackInfo info) {
		if (this instanceof IScarfHaver scarfHaver) {
			ImmutableList<AnchoredSlot> slotConfig = ImmutableList.<AnchoredSlot>builder()
				.add(new AnchoredSlot("", new Vector3f( 3.5f/16f, -18/16f, 4/16f), Identifier.of("trinkets", "head/left_scarf/0")))
				.build();
			
			scarfHaver.iScarfHaver_setAnchoredSlots(slotConfig);
		}
	}
}
