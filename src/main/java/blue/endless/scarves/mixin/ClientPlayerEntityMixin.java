package blue.endless.scarves.mixin;

import java.util.stream.Stream;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.authlib.GameProfile;

import blue.endless.scarves.ScarvesItems;
import blue.endless.scarves.api.ScarfLogic;
import blue.endless.scarves.client.IScarfHaver;
import blue.endless.scarves.client.ScarfAttachment;
import blue.endless.scarves.client.SimpleScarfAttachment;
import dev.emi.trinkets.api.TrinketComponent;
import dev.emi.trinkets.api.TrinketsApi;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.encryption.PlayerPublicKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

@Mixin({ClientPlayerEntity.class, OtherClientPlayerEntity.class})
public abstract class ClientPlayerEntityMixin extends PlayerEntity implements IScarfHaver {

	public ClientPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile gameProfile, PlayerPublicKey publicKey) {
		super(world, pos, yaw, gameProfile, publicKey);
	}

	private static final float SCARF_TAIL_SEPARATION = 0.19f;

	private SimpleScarfAttachment scarves_leftScarf;
	private SimpleScarfAttachment scarves_rightScarf;
	
	@Override
	public Stream<ScarfAttachment> iScarfHaver_getAttachments(float delta) {
		
		TrinketComponent component = TrinketsApi.getTrinketComponent((LivingEntity)(Object) this).orElse(null);
		if (component !=null) {
			for(var equipped : component.getEquipped(ScarvesItems.SCARF)) {
				
				if (scarves_leftScarf==null) {
					scarves_leftScarf = new SimpleScarfAttachment();
				}
				if (scarves_rightScarf==null) {
					scarves_rightScarf = new SimpleScarfAttachment();
				}
				
				Vec3d planarLookVec = Vec3d.fromPolar(0, getYaw());
				Vec3d upVec = new Vec3d(0, 1, 0);
				Vec3d rightVec = planarLookVec.crossProduct(upVec);
				
				EntityPose pose = getPose();
				
				double posedEyeHeight = this.getEyeHeight(getPose()) - 0.4;
				Vec3d referencePos = new Vec3d(0, posedEyeHeight, 0);
				
				if (!(this.getClass().equals(ClientPlayerEntity.class) && MinecraftClient.getInstance().options.getPerspective().isFirstPerson())) {
					if (pose==EntityPose.FALL_FLYING) {
						Vec3d lookVec = Vec3d.fromPolar(getPitch(), scarves$getBodyYaw(delta));
						referencePos = referencePos.add(lookVec.multiply(1.5));
					} else if (pose==EntityPose.SWIMMING) { 
						Vec3d lookVec = Vec3d.fromPolar(getPitch(), scarves$getBodyYaw(delta));
						referencePos = referencePos.add(lookVec.multiply(0.7)).add(0, 0.25, 0);
					}
				}

				scarves_leftScarf.setLocation(this.getLerpedPos(delta).add(referencePos).add(rightVec.multiply(-SCARF_TAIL_SEPARATION)).add(planarLookVec.multiply(-0.25)));
				scarves_rightScarf.setLocation(this.getLerpedPos(delta).add(referencePos).add(rightVec.multiply(SCARF_TAIL_SEPARATION)).add(planarLookVec.multiply(-0.25)));
				
				return Stream.of(scarves_leftScarf, scarves_rightScarf);
			}
			
			return Stream.empty();
		} else {
			return Stream.empty();
		}
	}
	
	@Inject(at = { @At("TAIL") }, method="tick()V")
	public void afterTick(CallbackInfo ci) {
		scarves$updateScarfAttachments();
	}
	
	private void scarves$updateScarfAttachments() {
		TrinketComponent component = TrinketsApi.getTrinketComponent((LivingEntity)(Object) this).orElse(null);
		if (component !=null) {
			for(var equipped : component.getEquipped(ScarvesItems.SCARF)) {
				ItemStack stack = equipped.getRight();
				
				NbtCompound tag = stack.getNbt();
				if (tag==null) {
					//This is an empty scarf
					if (scarves_leftScarf!=null) scarves_leftScarf.nodes().clear();
					if (scarves_rightScarf!=null) scarves_rightScarf.nodes().clear();
				} else {
					
					NbtList leftScarfTag = tag.getList("LeftScarf", NbtElement.COMPOUND_TYPE);
					if (leftScarfTag!=null) {
						if (scarves_leftScarf==null) scarves_leftScarf = new SimpleScarfAttachment();
						ScarfLogic.updateScarfAttachment(scarves_leftScarf, this.world, (Entity)(Object)this, this.getPos(), leftScarfTag);
					}
					
					NbtList rightScarfTag = tag.getList("RightScarf", NbtElement.COMPOUND_TYPE);
					if (rightScarfTag!=null) {
						if (scarves_rightScarf==null) scarves_rightScarf = new SimpleScarfAttachment();
						ScarfLogic.updateScarfAttachment(scarves_rightScarf, this.world, (Entity)(Object)this, this.getPos(), rightScarfTag);
					}
				}
			}
		}
	}
	
	/*
	 * THANKS Zephyr!!! The Limits helped me finally pin scarves to the right place while elytra flying
	 */
	private float scarves$getBodyYaw(float tickDelta) {
		float result = MathHelper.lerpAngleDegrees(tickDelta, prevBodyYaw, bodyYaw);
		float lerpedHedYaw = MathHelper.lerpAngleDegrees(tickDelta, prevHeadYaw, headYaw);
		
		if (hasVehicle() && getVehicle() instanceof LivingEntity livingEntity2) {
			result = MathHelper.lerpAngleDegrees(tickDelta, livingEntity2.prevBodyYaw, livingEntity2.bodyYaw);
			float k = lerpedHedYaw - result;
			float l = MathHelper.wrapDegrees(k);
			if (l < -85.0F) {
				l = -85.0F;
			}
			if (l >= 85.0F) {
				l = 85.0F;
			}
			result = lerpedHedYaw - l;
			if (l * l > 2500.0F) {
				result += l * 0.2F;
			}
		}
		return result;
	}
}
