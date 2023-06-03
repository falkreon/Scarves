package blue.endless.scarves.client;

import org.joml.AxisAngle4f;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.client.TrinketRenderer;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;

public class ScarfTrinketRenderer implements TrinketRenderer {

	@SuppressWarnings("unchecked")
	@Override
	public void render(
			ItemStack stack,
			SlotReference slotReference,
			EntityModel<? extends LivingEntity> contextModel,
			MatrixStack matrices,
			VertexConsumerProvider vertexConsumers,
			int light,
			LivingEntity entity,
			float limbAngle,
			float limbDistance,
			float tickDelta,
			float animationProgress,
			float headYaw,
			float headPitch) {
		
		matrices.push();
		
		TrinketRenderer.translateToFace(matrices, (PlayerEntityModel<AbstractClientPlayerEntity>) contextModel, (AbstractClientPlayerEntity) entity, headYaw, headPitch);
		
		matrices.multiply(new Quaternionf(new AxisAngle4f((float) Math.PI, 0, 0, 1)));
		matrices.translate(0, -0.25f, 1);
		
		//TrinketRenderer.translateToFace(matrices, (PlayerEntityModel<AbstractClientPlayerEntity>)contextModel, (AbstractClientPlayerEntity) entity, headYaw, headPitch);
		//matrices.translate(0d, 3.0, 0d);
		//Matrix4f modelMatrix = matrices.peek().getPositionMatrix();
		//Matrix3f normalMatrix = matrices.peek().getNormalMatrix();
		
		//ModelIdentifier modelId = new ModelIdentifier("minecraft:block/stone");
		//BakedModel model = BakedModelManagerHelper.getModel(MinecraftClient.getInstance().getBakedModelManager(), new Identifier("minecraft", "block/stone"));
		//VertexConsumer buf = vertexConsumers.getBuffer(RenderLayer.getCutoutMipped());
		//MinecraftClient.getInstance().getItemRenderer().renderItem(stack, Mode.FIXED, light, 0, matrices, vertexConsumers, 0);
		Matrix4f modelMatrix = matrices.peek().getPositionMatrix();
		Matrix3f normalMatrix = matrices.peek().getNormalMatrix();
		//ItemRenderer itemRenderer = MinecraftClient.getInstance().getItemRenderer();
		/*
		VertexConsumer buf = vertexConsumers.getBuffer(RenderLayer.getEntityCutout(new Identifier("scarves:missingno")));
		buf.vertex(modelMatrix, 0,-3,0).color(0xFF_77FF77).texture(0, 0).light(light).normal(normalMatrix, 0, 1, 0).next();
		buf.vertex(modelMatrix, 1,-3,0).color(0xFF_77FF77).texture(1, 0).light(light).normal(normalMatrix, 0, 1, 0).next();
		buf.vertex(modelMatrix, 1,-3,1).color(0xFF_77FF77).texture(1, 1).light(light).normal(normalMatrix, 0, 1, 0).next();
		buf.vertex(modelMatrix, 0,-3,1).color(0xFF_77FF77).texture(0, 1).light(light).normal(normalMatrix, 0, 1, 0).next();
		*/
		//VertexConsumer lineBuf = vertexConsumers.getBuffer(RenderLayer.getLines());
		//WorldRenderer.drawBox(matrices, lineBuf, -2, -2, -2, 2, 2, 2, 1.0f, 0.0f, 1.0f, 1.0f, 1.0f, 0.0f, 1.0f);
		
		//buf.vertex(1,1,0).color(0xFF_77FF77).texture(0, 0).overlay(0,0).light(light).normal(0, 1, 0).next();
		
		//WorldRenderer.getLightmapCoordinates(blockInfo.blockView, blockState, mpos);
		
		
		matrices.pop();
		
		matrices.push();
		//BlockPos cameraPos = entity.getBlockPos();
		//Vec3d entityPos = entity.getPos();
		//matrices.multiply(Quaternion.fromEulerXyz((float) (entity.getYaw()*Math.PI/180.0), (float) (entity.getPitch()*Math.PI/180.0), 0));
		//matrices.translate(entityPos.getZ(), entityPos.getY(), entityPos.getX());
		
		//Matrix4f toUndo = matrices.peek().getPositionMatrix();
		//toUndo.invert();
		//matrices.multiplyPositionMatrix(toUndo);
		matrices.translate(0, -0.25f, 1);
		
		BakedModel bakedModel = MinecraftClient.getInstance().getItemRenderer().getModel(stack, entity.getWorld(), entity, 0);
		BakedModel stoneModel = MinecraftClient.getInstance().getBakedModelManager().getBlockModels().getModel(Blocks.STONE.getDefaultState());
		
		MinecraftClient.getInstance().getItemRenderer().renderItem(stack, ModelTransformationMode.FIXED, false, matrices, vertexConsumers, light, 0, stoneModel);
		//MinecraftClient.getInstance().getItemRenderer().renderItem(stack, Mode.FIXED, light, 0, matrices, vertexConsumers, 0);
		
		matrices.pop();
		//Vector4f testVec = new Vector4f(0, 0, 0, 1);
		//testVec.transform(matrix);
		//System.out.println(testVec.toString());
		
		
		
		//MinecraftClient.getInstance().getItemRenderer().renderItem(stack, ModelTransformation.Mode.FIXED, light, light, matrices, vertexConsumers, light);
	}

}
