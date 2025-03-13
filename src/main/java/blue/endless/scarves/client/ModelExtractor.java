package blue.endless.scarves.client;

import java.util.HashMap;
import java.util.Map;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelPartNames;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;

public class ModelExtractor {
	@SuppressWarnings("unchecked")
	public static <T extends Entity> Map<String, Matrix4f> extract(T entity, float tickDelta) {
		//HashMap<String, ModelPart> modelMap = new HashMap<>();
		HashMap<String, Matrix4f> matrixMap = new HashMap<>();
		
		EntityRenderer<T> renderer = (EntityRenderer<T>) MinecraftClient.getInstance().getEntityRenderDispatcher().getRenderer(entity);
		if (renderer instanceof FeatureRendererContext<?, ?> ctx) {
			EntityModel<T> model = (EntityModel<T>) ctx.getModel();
			float animationProgress = 0f;
			float limbPos = 0f;
			float limbSpeed = 0f;
			if (entity instanceof LivingEntity living) {
				animationProgress = living.age + tickDelta;
				limbPos = living.limbAnimator.getPos(tickDelta);
				if (living.isBaby()) limbPos *= 3f;
				limbSpeed = living.limbAnimator.getSpeed(tickDelta);
				if (limbSpeed > 1) limbSpeed = 1;
			}
			
			MatrixStack matrixStack = new MatrixStack();
			
			if (entity instanceof AbstractClientPlayerEntity player) {
				setPlayerTransforms(player, matrixStack, 0f, 0f, tickDelta, 0f);
			}
			model.animateModel(entity, limbPos, limbSpeed, tickDelta);
			model.setAngles(entity, limbPos, limbSpeed, animationProgress, entity.getHeadYaw(), entity.getPitch());
			
			Matrix4f base = matrixStack.peek().getPositionMatrix().invert(new Matrix4f());
			
			if (model instanceof BipedEntityModel biped) {
				matrixMap.put(EntityModelPartNames.BODY, extractMatrix(biped.body, base));
				matrixMap.put(EntityModelPartNames.LEFT_ARM, extractMatrix(biped.leftArm, base));
				matrixMap.put(EntityModelPartNames.RIGHT_ARM, extractMatrix(biped.rightArm, base));
				matrixMap.put(EntityModelPartNames.LEFT_LEG, extractMatrix(biped.leftLeg, base));
				matrixMap.put(EntityModelPartNames.RIGHT_LEG, extractMatrix(biped.rightLeg, base));
				matrixMap.put(EntityModelPartNames.HEAD, extractMatrix(biped.head, base));
				matrixMap.put(EntityModelPartNames.HAT, extractMatrix(biped.hat, base));
			}
		}
		
		return matrixMap;
	}
	
	public static Matrix4f fetch(Entity entity, Matrix4f baseMatrix, Map<String, Matrix4f> mapping, String path, Vector3f translate) {
		Matrix4f part = mapping.getOrDefault(path, new Matrix4f());
		return part.translate(translate, new Matrix4f());
	}
	
	private static Matrix4f extractMatrix(final ModelPart part, Matrix4f base) {
		MatrixStack partMatrix = new MatrixStack();
		partMatrix.push();
		partMatrix.multiplyPositionMatrix(base);
		
		part.rotate(partMatrix);
		partMatrix.translate(part.pivotX, part.pivotY, part.pivotZ);
		return partMatrix.peek().getPositionMatrix();
	}
	
	private static void setPlayerTransforms(AbstractClientPlayerEntity abstractClientPlayerEntity, MatrixStack matrixStack, float f, float g, float h, float i) {
		float j = abstractClientPlayerEntity.getLeaningPitch(h);
		float k = abstractClientPlayerEntity.getPitch(h);
		if (abstractClientPlayerEntity.isFallFlying()) {
			//super.setupTransforms(abstractClientPlayerEntity, matrixStack, f, g, h, i);
			float l = abstractClientPlayerEntity.getFallFlyingTicks() + h;
			float m = MathHelper.clamp(l * l / 100.0F, 0.0F, 1.0F);
			if (!abstractClientPlayerEntity.isUsingRiptide()) {
				matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(m * (-90.0F - k)));
			}

			Vec3d vec3d = abstractClientPlayerEntity.getRotationVec(h);
			Vec3d vec3d2 = abstractClientPlayerEntity.lerpVelocity(h);
			double d = vec3d2.horizontalLengthSquared();
			double e = vec3d.horizontalLengthSquared();
			if (d > 0.0 && e > 0.0) {
				double n = (vec3d2.x * vec3d.x + vec3d2.z * vec3d.z) / Math.sqrt(d * e);
				double o = vec3d2.x * vec3d.z - vec3d2.z * vec3d.x;
				matrixStack.multiply(RotationAxis.POSITIVE_Y.rotation((float)(Math.signum(o) * Math.acos(n))));
			}
		} else if (j > 0.0F) {
			//super.setupTransforms(abstractClientPlayerEntity, matrixStack, f, g, h, i);
			float lx = abstractClientPlayerEntity.isTouchingWater() ? -90.0F - k : -90.0F;
			float mx = MathHelper.lerp(j, 0.0F, lx);
			matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(mx));
			if (abstractClientPlayerEntity.isInSwimmingPose()) {
				matrixStack.translate(0.0F, -1.0F, 0.3F);
			}
		} else {
			//super.setupTransforms(abstractClientPlayerEntity, matrixStack, f, g, h, i);
		}
	}
}
