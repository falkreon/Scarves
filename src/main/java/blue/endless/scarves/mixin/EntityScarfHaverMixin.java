package blue.endless.scarves.mixin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.google.common.collect.ImmutableList;

import blue.endless.scarves.api.AnchoredSlot;
import blue.endless.scarves.api.ScarfLogic;
import blue.endless.scarves.client.IScarfHaver;
import blue.endless.scarves.client.ITickDeprivationAware;
import blue.endless.scarves.client.ModelExtractor;
import blue.endless.scarves.client.SimpleScarfAttachment;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

@Mixin(Entity.class)
public abstract class EntityScarfHaverMixin implements IScarfHaver, ITickDeprivationAware {

	private ImmutableList<AnchoredSlot> iScarfHaver_scarfSlotConfiguration = null;
	
	@Environment(EnvType.CLIENT)
	private HashMap<AnchoredSlot, SimpleScarfAttachment> iScarfHaver_scarfAttachments = new HashMap<>();
	
	private long iScarfHaver_lastValidTick = 0L;

	@Override
	public ImmutableList<AnchoredSlot> iScarfHaver_getAnchoredSlots() {
		return iScarfHaver_scarfSlotConfiguration;
	}
	
	@Override
	public void iScarfHaver_setAnchoredSlots(ImmutableList<AnchoredSlot> slots) {
		this.iScarfHaver_scarfSlotConfiguration = slots;
	}

	@Override
	@Environment(EnvType.CLIENT)
	public Collection<SimpleScarfAttachment> iScarfHaver_getAttachments(float tickDelta) {
		if (iScarfHaver_scarfSlotConfiguration == null) return List.of();
		
		Map<String, Matrix4f> matrices = ModelExtractor.extract((Entity) (Object) this, tickDelta);
		
		//Clear outdated attachments from cache
		List<AnchoredSlot> toRemove = new ArrayList<>();
		for(AnchoredSlot slot : iScarfHaver_scarfAttachments.keySet()) {
			if (!iScarfHaver_scarfSlotConfiguration.contains(slot)) toRemove.add(slot);
		}
		for(AnchoredSlot slot : toRemove) iScarfHaver_scarfAttachments.remove(slot);
		
		// Update / add current attachments
		for(AnchoredSlot slot : iScarfHaver_scarfSlotConfiguration) {
			SimpleScarfAttachment attachment = iScarfHaver_scarfAttachments.computeIfAbsent(slot, (it) -> {
				SimpleScarfAttachment result = new SimpleScarfAttachment();
				return result;
			});
			
			Vec3d lerpedPosD = ((Entity) (Object) this).getLerpedPos(tickDelta);
			Vector4f lerpedPos = new Vector4f((float) lerpedPosD.x, (float) lerpedPosD.y, (float) lerpedPosD.z, 1);
			
			Matrix4f bodyRotation = new Matrix4f().rotationY((float) -(this.iScarfHaver_getBodyYaw(tickDelta) * Math.PI / 180d));
			
			Vector4f a = new Vector4f(0, 0, 0, 1);
			
			
			a.add(0, -1.501f, 0, 0).mul(-1, -1, 1, 1);
			
			Matrix4f bodyMatrix = ModelExtractor.fetch((Entity) (Object) this, new Matrix4f(), matrices, slot.anchorPoint(), slot.offset());
			bodyMatrix.transform(a);
			
			a.add(0,0,-0.25f, 0);
			
			bodyRotation.transform(a);
			
			a.add(lerpedPos);
			attachment.setLocation(new Vec3d(a.x, a.y, a.z));
			
			
			
			/*
			Vec3d baseMojang = ((Entity) (Object) this).getLerpedPos(tickDelta);
			Vector3f base = new Vector3f((float) baseMojang.x, (float) baseMojang.y, (float) baseMojang.z);
			Matrix4f mat = ModelExtractor.fetch((Entity) (Object) this, new Matrix4f(), matrices, slot.anchorPoint(), slot.offset());
			
			Matrix4f bodyRotate = new Matrix4f().rotationY((float) -(iScarfHaver_getBodyYaw(tickDelta) * Math.PI / 180.0));
			Vector4f transformed = mat.transform(new Vector4f(0f, 0f, 0f, 1f));
			transformed = bodyRotate.transform(transformed);
			attachment.setLocation(new Vec3d(transformed.x + base.x, transformed.y + base.y, transformed.z + base.z));*/
			
			attachment.setDesign(slot.getScarfDesign((Entity) (Object) this).orElse(null));
		}
		
		return iScarfHaver_scarfAttachments.values();
	}
	
	@Override
	@Environment(EnvType.CLIENT)
	public float iScarfHaver_getBodyYaw(float tickDelta) {
		if (((Object) this) instanceof LivingEntity living) {
			float result = MathHelper.lerpAngleDegrees(tickDelta, living.prevBodyYaw, living.bodyYaw);
			float lerpedHedYaw = MathHelper.lerpAngleDegrees(tickDelta, living.prevHeadYaw, living.headYaw);
			
			if (living.hasVehicle() && living.getVehicle() instanceof LivingEntity livingEntity2) {
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
		} else {
			return ((Entity) (Object) this).getYaw(tickDelta);
		}
	}
	
	@Inject(at = { @At("TAIL") }, method="tick()V")
	public void afterTick(CallbackInfo ci) {
		World world = ((Entity) (Object) this).getEntityWorld();
		if (world != null) {
			iScarfHaver_lastValidTick = world.getTime();
		}
		
		// Force attachments to update and reposition
		Collection<SimpleScarfAttachment> attachments = iScarfHaver_getAttachments(0f);
		for(SimpleScarfAttachment attachment : attachments) {
			ScarfLogic.updateScarfAttachment(attachment, (Entity) (Object) this);
		}
	}
	
	@Override
	public boolean scarves_isTickDeprived(long currentTick) {
		long elapsed = currentTick - iScarfHaver_lastValidTick;
		return elapsed > 3;
	}
}
