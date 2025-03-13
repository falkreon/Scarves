package blue.endless.scarves.mixin;

import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.google.common.collect.ImmutableList;
import com.mojang.authlib.GameProfile;

import blue.endless.scarves.api.AnchoredSlot;
import blue.endless.scarves.client.IScarfHaver;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.Identifier;

@Mixin(AbstractClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin {
	@Inject(method="<init>(Lnet/minecraft/world/ClientWorld;Lcom/mojang/authlib/GameProfile;)V", at = @At("TAIL"))
	public void afterInit(ClientWorld world, GameProfile profile, CallbackInfo info) {
		System.out.println("ClientPlayer Init");
		IScarfHaver scarfHaver = (IScarfHaver) this;
		ImmutableList<AnchoredSlot> slotConfig = ImmutableList.<AnchoredSlot>builder()
			.add(new AnchoredSlot("body", new Vector3f(-0.19f, -0.25f, 0f), Identifier.of("trinkets", "head/left_scarf/0")))
			.add(new AnchoredSlot("body", new Vector3f( 0.19f, -0.25f, 0f), Identifier.of("trinkets", "head/right_scarf/0")))
			//TODO: This works *terribly*. There's a lot more work to be done matching up registration points with model locations.
			//.add(new AnchoredSlot("left_arm", new Vector3f( 0, 0, 0), Identifier.of("minecraft", "weapon.offhand")))
			.build();
		
		scarfHaver.iScarfHaver_setAnchoredSlots(slotConfig);
	}
}
