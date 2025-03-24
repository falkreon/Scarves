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
	private final float BODY_WIDTH = 16/16f;
	private final float BODY_CENTERLINE = BODY_WIDTH / 2f;
	
	@Inject(method="<init>", at = @At("TAIL"))
	public void afterInit(CallbackInfo info) {
		
		if (this instanceof IScarfHaver scarfHaver) {
			ImmutableList<AnchoredSlot> slotConfig = ImmutableList.<AnchoredSlot>builder()
				.add(new AnchoredSlot("body", new Vector3f( BODY_CENTERLINE + 0.38f, SCARF_Y, 0.2f), Identifier.of("trinkets", "head/left_scarf/0")))
				.add(new AnchoredSlot("body", new Vector3f( BODY_CENTERLINE - 0.38f, SCARF_Y, 0.2f), Identifier.of("trinkets", "head/right_scarf/0")))
				.build();
			
			scarfHaver.iScarfHaver_setAnchoredSlots(slotConfig);
		}
	}
}
