package blue.endless.scarves.mixin;

import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.google.common.collect.ImmutableList;

import blue.endless.scarves.api.AnchoredSlot;
import blue.endless.scarves.client.IScarfHaver;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.util.Identifier;

@Mixin(AbstractClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin {
	private final float BODY_HEIGHT = 12/16f;
	private final float SCARF_Y = BODY_HEIGHT - 3/16f;
	
	@Inject(method="<init>", at = @At("TAIL"))
	public void afterInit(CallbackInfo info) {
		if (this instanceof IScarfHaver scarfHaver) {
			ImmutableList<AnchoredSlot> slotConfig = ImmutableList.<AnchoredSlot>builder()
				.add(new AnchoredSlot("body", new Vector3f( 0.19f, SCARF_Y, 0.1f), Identifier.of("trinkets", "head/left_scarf/0")))
				.add(new AnchoredSlot("body", new Vector3f(-0.19f, SCARF_Y, 0.1f), Identifier.of("trinkets", "head/right_scarf/0")))
				//TODO: This works *terribly*. There's a lot more work to be done matching up registration points with model locations.
				//.add(new AnchoredSlot("left_arm", new Vector3f( 0, 0, 0), Identifier.of("minecraft", "weapon.offhand")))
				.build();
			
			scarfHaver.iScarfHaver_setAnchoredSlots(slotConfig);
		}
	}
}
