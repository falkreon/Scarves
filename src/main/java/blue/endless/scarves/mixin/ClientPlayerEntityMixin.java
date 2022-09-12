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
		
		//TEMP FIGURE OUT ATTACHMENT POINT INFO
		float bodyYaw = MathHelper.lerpAngleDegrees(delta, this.prevBodyYaw, this.bodyYaw);
		float headYaw = MathHelper.lerpAngleDegrees(delta, this.prevHeadYaw, this.headYaw);
		float pitch = MathHelper.lerp(delta, this.prevPitch, this.getPitch());
		
		
		
		Vec3d pos = this.getLerpedPos(delta);
		/*
		EntityRenderer<?> renderer = MinecraftClient.getInstance().getEntityRenderDispatcher().getRenderer(this);
		if (renderer instanceof PlayerEntityRenderer playerRender) {
			PlayerEntityModel<AbstractClientPlayerEntity> model = playerRender.getModel();
			scarves_updatePlayerEntityModel(model);
			ModelPart head = model.getHead();
			System.out.println(head.yaw+" / "+head.pitch);
		}*/
		//END FIGURE OUT ATTACHMENT POINT INFO
		
		TrinketComponent component = TrinketsApi.getTrinketComponent((LivingEntity)(Object) this).orElse(null);
		if (component !=null) {
			for(var equipped : component.getEquipped(ScarvesItems.SCARF)) {
				
				if (scarves_leftScarf==null) {
					scarves_leftScarf = new SimpleScarfAttachment();
				}
				if (scarves_rightScarf==null) {
					scarves_rightScarf = new SimpleScarfAttachment();
				}
				
				//Vec3d lookVec = Vec3d.fromPolar(getPitch(), getYaw());
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
				//double posedEyeHeight = this.getEyeHeight(getPose()) - 0.4;
				//Vec3d referencePos = new Vec3d(0, posedEyeHeight, 0);
				//referencePos = referencePos.rotateX(this.getLeaningPitch(delta));
				
				
				
				//TODO: I THINK I CRACKED IT
				// Use getLeaningPitch to get our body pitch, and rotate the referencePos vector to get our new base head location.
				// Then displace relative to *that*. It may be that we don't even want to use the crouching/fallflying pose, just
				// the standing one.
				
				//TODO: This is not working at *all*.
				//if (isFallFlying() || isInSwimmingPose()) {
				//	ScarvesMod.LOGGER.info("Translating for pose "+this.getPose()+" by "+planarLookVec);
				//	referencePos = referencePos.rotateX((float) (0.25*Math.PI));
				//	//TODO: Translate forward
				//}
				
				//TODO: Attach to shoulders
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
	
	/*
	private void scarves_updatePlayerEntityModel(PlayerEntityModel<AbstractClientPlayerEntity> model) {
		float tickDelta = 0f;
		model.handSwingProgress = this.getHandSwingProgress(tickDelta);
		model.riding = this.hasVehicle();
		model.child = this.isBaby();
		float bodyYaw = MathHelper.lerpAngleDegrees(tickDelta, this.prevBodyYaw, this.bodyYaw);
		float headYaw = MathHelper.lerpAngleDegrees(tickDelta, this.prevHeadYaw, this.headYaw);
		float deltaHeadYaw = headYaw - bodyYaw;
		if (this.hasVehicle() && this.getVehicle() instanceof LivingEntity) {
			LivingEntity livingEntity2 = (LivingEntity) this.getVehicle();
			bodyYaw = MathHelper.lerpAngleDegrees(tickDelta, livingEntity2.prevBodyYaw, livingEntity2.bodyYaw);
			deltaHeadYaw = headYaw - bodyYaw;
			float relativeLookYaw = MathHelper.wrapDegrees(deltaHeadYaw);
			if (relativeLookYaw < -85.0f) {
				relativeLookYaw = -85.0f;
			}
			if (relativeLookYaw >= 85.0f) {
				relativeLookYaw = 85.0f;
			}
			bodyYaw = headYaw - relativeLookYaw;
			if (relativeLookYaw * relativeLookYaw > 2500.0f) {
				bodyYaw += relativeLookYaw * 0.2f;
			}
			deltaHeadYaw = headYaw - bodyYaw;
		}
		float pitch = MathHelper.lerp(tickDelta, this.prevPitch, this.getPitch());
		if (LivingEntityRenderer.shouldFlipUpsideDown(this)) {
			pitch *= -1.0f;
			deltaHeadYaw *= -1.0f;
		}
		
		//float n = 0f;
		//if (this.isInPose(EntityPose.SLEEPING) && this.getSleepingDirection() != null) {
		//	n = this.getEyeHeight(EntityPose.STANDING) - 0.1f;
		//}
		float ticksLived = this.age + tickDelta;
		float n = 0.0f;
		float o = 0.0f;
		if (!this.hasVehicle() && this.isAlive()) {
			n = MathHelper.lerp(tickDelta, this.lastLimbDistance, this.limbDistance);
			o = this.limbAngle - this.limbDistance * (1.0f - tickDelta);
			if (this.isBaby()) {
				o *= 3.0f;
			}
			if (n > 1.0f) {
				n = 1.0f;
			}
		}
		model.animateModel((AbstractClientPlayerEntity)(Object)this, o, n, tickDelta);
		model.setAngles((AbstractClientPlayerEntity)(Object)this, o, n, ticksLived, deltaHeadYaw, pitch);

		//TODO: Determine whether the model should be visible to the player
	}*/
}
