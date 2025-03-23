package blue.endless.scarves.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelPartNames;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;

public class ModelExtractor {
	
	@SuppressWarnings("unchecked")
	public static <T extends Entity> Map<String, Part> extractFully(T entity, float tickDelta) {
		HashMap<String, Part> modelMap = new HashMap<>();
		
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
			
			model.riding = entity.hasVehicle();
			
			if (entity instanceof LivingEntity living) {
				model.handSwingProgress = living.getHandSwingProgress(tickDelta);
				model.child = living.isBaby();
			}
			
			if (entity instanceof AbstractClientPlayerEntity player) {
				setPlayerTransforms(player, matrixStack, 0f, 0f, tickDelta, 0f);
			}
			
			
			///FOO - this block was copied/adapted out of LivingEntityRenderer::render. There is no other way to reproduce these steps.
			if (entity instanceof LivingEntity living) {
				float lerpedBodyYaw = MathHelper.lerpAngleDegrees(tickDelta, living.prevBodyYaw, living.bodyYaw);
				float lerpedHeadYaw = MathHelper.lerpAngleDegrees(tickDelta, living.prevHeadYaw, living.headYaw);
				float headDelta = lerpedHeadYaw - lerpedBodyYaw;
				if (living.hasVehicle() && living.getVehicle() instanceof LivingEntity livingVehicle) {
					float lerpedVehicleYaw = MathHelper.lerpAngleDegrees(tickDelta, livingVehicle.prevBodyYaw, livingVehicle.bodyYaw);
					headDelta = lerpedHeadYaw - lerpedVehicleYaw;
					float headRotationDegrees = MathHelper.wrapDegrees(headDelta);
					if (headRotationDegrees < -85.0F) {
						headRotationDegrees = -85.0F;
					}
	
					if (headRotationDegrees >= 85.0F) {
						headRotationDegrees = 85.0F;
					}
	
					lerpedBodyYaw = lerpedHeadYaw - headRotationDegrees;
					if (headRotationDegrees * headRotationDegrees > 2500.0F) {
						lerpedBodyYaw += headRotationDegrees * 0.2F;
					}
	
					headDelta = lerpedHeadYaw - lerpedBodyYaw;
				}
	
				float lerpedPitch = MathHelper.lerp(tickDelta, living.prevPitch, living.getPitch());
				if (LivingEntityRenderer.shouldFlipUpsideDown(living)) {
					lerpedPitch *= -1.0F;
					headDelta *= -1.0F;
				}
	
				headDelta = MathHelper.wrapDegrees(headDelta);
				if (living.isInPose(EntityPose.SLEEPING)) {
					Direction direction = living.getSleepingDirection();
					if (direction != null) {
						float sleepingEyeHeight = living.getEyeHeight(EntityPose.STANDING) - 0.1F;
						matrixStack.translate(-direction.getOffsetX() * sleepingEyeHeight, 0.0F, -direction.getOffsetZ() * sleepingEyeHeight);
					}
				}
	
				float lx = living.getScale();
				matrixStack.scale(lx, lx, lx);
				float animationProgress2 = entity.age + tickDelta;
				setupLivingTransforms(living, matrixStack, animationProgress2, lerpedBodyYaw, tickDelta, lx);
				
				if (!FabricLoader.getInstance().isModLoaded("sodium")) {
					matrixStack.scale(-1.0F, -1.0F, 1.0F);
					//this.scale(livingEntity, matrixStack, g); // We really don't have an equivalent here.
					matrixStack.translate(0.0F, -1.501F, 0.0F);
				}
				
				float o = 0.0F;
				float p = 0.0F;
				if (!living.hasVehicle() && living.isAlive()) {
					o = living.limbAnimator.getSpeed(tickDelta);
					p = living.limbAnimator.getPos(tickDelta);
					if (living.isBaby()) {
						p *= 3.0F;
					}
	
					if (o > 1.0F) {
						o = 1.0F;
					}
				}
	
				model.animateModel((T)living, p, o, tickDelta);
				model.setAngles((T)living, p, o, animationProgress2, headDelta, lerpedPitch);
			} else {
			///FOO
			
				model.animateModel(entity, limbPos, limbSpeed, tickDelta);
				model.setAngles(entity, limbPos, limbSpeed, animationProgress, entity.getHeadYaw(), entity.getPitch());
			}
			
			//if (FabricLoader.getInstance().isModLoaded("sodium")) {
			//	matrixStack.scale(0, -1, 0);
			//}
			
			
			Matrix4f base = matrixStack.peek().getPositionMatrix().invert(new Matrix4f());
			
			
			
			if (model instanceof BipedEntityModel biped) {
				//System.out.println("Extracting biped modelparts");
				modelMap.put(EntityModelPartNames.BODY, Part.of(biped.body, base));
				modelMap.put(EntityModelPartNames.LEFT_ARM, Part.of(biped.leftArm, base));
				modelMap.put(EntityModelPartNames.RIGHT_ARM, Part.of(biped.rightArm, base));
				modelMap.put(EntityModelPartNames.LEFT_LEG, Part.of(biped.leftLeg, base));
				modelMap.put(EntityModelPartNames.RIGHT_LEG, Part.of(biped.rightLeg, base));
				modelMap.put(EntityModelPartNames.HEAD, Part.of(biped.head, base));
				modelMap.put(EntityModelPartNames.HAT, Part.of(biped.hat, base));
			}
		}
		
		return modelMap;
	}
	
	private static void setupLivingTransforms(LivingEntity entity, MatrixStack matrices, float animationProgress, float bodyYaw, float tickDelta, float scale) {
		if (entity.isFrozen()) {
			bodyYaw += (float)(Math.cos(entity.age * 3.25) * Math.PI * 0.4F);
		}

		if (!entity.isInPose(EntityPose.SLEEPING)) {
			matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0F - bodyYaw));
		}

		if (entity.deathTime > 0) {
			float f = (entity.deathTime + tickDelta - 1.0F) / 20.0F * 1.6F;
			f = MathHelper.sqrt(f);
			if (f > 1.0F) {
				f = 1.0F;
			}

			matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(f * 90f)); // Might not be 90 for some entities. A LivingEntityRenderer::getLyingAngle accessor would fix this
		} else if (entity.isUsingRiptide()) {
			matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-90.0F - entity.getPitch()));
			matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees((entity.age + tickDelta) * -75.0F));
		} else if (entity.isInPose(EntityPose.SLEEPING)) {
			Direction direction = entity.getSleepingDirection();
			float g = direction != null ? getYaw(direction) : bodyYaw;
			matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(g));
			matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(90f));
			matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(270.0F));
		} else if (LivingEntityRenderer.shouldFlipUpsideDown(entity)) {
			matrices.translate(0.0F, (entity.getHeight() + 0.1F) / scale, 0.0F);
			matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(180.0F));
		}
	}
	
	private static float getYaw(Direction direction) {
		switch (direction) {
			case SOUTH:
				return 90.0F;
			case WEST:
				return 0.0F;
			case NORTH:
				return 270.0F;
			case EAST:
				return 180.0F;
			default:
				return 0.0F;
		}
	}
	
	public static record Part(Matrix4f matrix, Vector3f pivot, float x1, float y1, float z1, float x2, float y2, float z2) {
		public static Part of(ModelPart part, Matrix4f base) {
			MatrixStack partMatrix = new MatrixStack();
			partMatrix.push();
			
			partMatrix.multiplyPositionMatrix(base);
			part.rotate(partMatrix);
			
			Matrix4f matrix = partMatrix.peek().getPositionMatrix();
			
			partMatrix.pop();
			
			List<ModelPart.Cuboid> cuboids = new ArrayList<>();
			part.forEachCuboid(partMatrix, (MatrixStack.Entry entry, String id, int index, ModelPart.Cuboid cuboid) -> {
				cuboids.add(cuboid);
			});
			ModelPart.Cuboid cuboid = (cuboids.isEmpty()) ?
					new ModelPart.Cuboid(0, 0, 0f, 0f, 0f, 1/16f, 1/16f, 1/16f, 0f, 0f, 0f, false, 0f, 0f, Set.of()) :
					cuboids.get(0);
			
			return new Part(
					matrix,
					new Vector3f(part.pivotX/16f, part.pivotY/16f, part.pivotZ/16f),
					cuboid.minX/16f, cuboid.minY/16f, cuboid.minZ/16f,
					cuboid.maxX/16f, cuboid.maxY/16f, cuboid.maxZ/16f
					);
		}
		
		public Vector3f transform(Vector3f vec, float bodyYaw, float pitchEstimate) {
			Vector4f working = new Vector4f(vec.x, vec.y, vec.z, 1);
			matrix.transform(working);
			if (FabricLoader.getInstance().isModLoaded("sodium")) {
				return new Vector3f(-working.x, -working.y + 1.501f, working.z).rotateX(pitchEstimate).rotateY(bodyYaw);
			} else {
				return new Vector3f(working.x, working.y, working.z);
			}
		}
		
		public Vector3f corner(Vector3f relative) {
			return new Vector3f(
					lerp(x1, x2, relative.x),
					lerp(y1, y2, relative.y),
					lerp(z1, z2, relative.z)
					);
		}
		
		public Vector3f transformRelative(Vector3f relative, float bodyYaw, float pitchEstimate) {
			return transform(corner(relative), bodyYaw, pitchEstimate);
		}
		
		private float lerp(float a, float b, float t) {
			return (1-t) * b + t * a;
		}
	}
	
	public static Matrix4f fetch(Entity entity, Matrix4f baseMatrix, Map<String, Matrix4f> mapping, String path, Vector3f translate) {
		Matrix4f part = mapping.getOrDefault(path, new Matrix4f());
		return part.translate(translate, new Matrix4f());
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
