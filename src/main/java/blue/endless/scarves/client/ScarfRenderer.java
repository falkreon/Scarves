package blue.endless.scarves.client;

import org.joml.Matrix4f;

import blue.endless.scarves.ScarvesMod;
import blue.endless.scarves.api.FabricSquare;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

public class ScarfRenderer {
	public static void quad(Vec3d a, Vec3d b, Vec3d c, Vec3d d, Vec2f texA, Vec2f texB, Vec2f texC, Vec2f texD, boolean doubleSided, VertexConsumerProvider vertexConsumers, MatrixStack matrices, int color, int light) {
		//Get normal from quad
		Vec3d ab = b.subtract(a);
		Vec3d ad = d.subtract(a);
		Vec3d normal = ab.crossProduct(ad);
		
		VertexConsumer buf = vertexConsumers.getBuffer(RenderLayer.getCutoutMipped());
		Matrix4f matrix = matrices.peek().getPositionMatrix();
		
		buf.vertex(matrix, (float) a.x, (float) a.y, (float) a.z).color(color).texture(texA.x, texA.y).light(light).normal((float) normal.x, (float) normal.y, (float) normal.z).next();
		buf.vertex(matrix, (float) b.x, (float) b.y, (float) b.z).color(color).texture(texB.x, texB.y).light(light).normal((float) normal.x, (float) normal.y, (float) normal.z).next();
		buf.vertex(matrix, (float) c.x, (float) c.y, (float) c.z).color(color).texture(texC.x, texC.y).light(light).normal((float) normal.x, (float) normal.y, (float) normal.z).next();
		buf.vertex(matrix, (float) d.x, (float) d.y, (float) d.z).color(color).texture(texD.x, texD.y).light(light).normal((float) normal.x, (float) normal.y, (float) normal.z).next();
		
		normal = normal.multiply(-1);
		
		if (doubleSided) {
			buf.vertex(matrix, (float) d.x, (float) d.y, (float) d.z).color(color).texture(texD.x, texD.y).light(light).normal((float) normal.x, (float) normal.y, (float) normal.z).next();
			buf.vertex(matrix, (float) c.x, (float) c.y, (float) c.z).color(color).texture(texC.x, texC.y).light(light).normal((float) normal.x, (float) normal.y, (float) normal.z).next();
			buf.vertex(matrix, (float) b.x, (float) b.y, (float) b.z).color(color).texture(texB.x, texB.y).light(light).normal((float) normal.x, (float) normal.y, (float) normal.z).next();
			buf.vertex(matrix, (float) a.x, (float) a.y, (float) a.z).color(color).texture(texA.x, texA.y).light(light).normal((float) normal.x, (float) normal.y, (float) normal.z).next();
		}
	}
	
	public static void quad(Vec3d a, Vec3d b, Vec3d c, Vec3d d, FabricSquare square, VertexConsumerProvider vertexConsumers, MatrixStack matrices, int light) {
		AbstractTexture tex = MinecraftClient.getInstance().getTextureManager().getTexture(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE);
		if (tex instanceof SpriteAtlasTexture atlas) {
			Sprite sprite = atlas.getSprite(square.id());
			float uPx = (sprite.getMaxU() - sprite.getMinU()) / 16f;
			float vPx = (sprite.getMaxV() - sprite.getMinV()) / 16f;
			
			float uofs = uPx * square.xofs();
			float vofs = vPx * square.yofs();
			
			float minU = sprite.getMinU() + uofs;
			float minV = sprite.getMinV() + vofs;
			float maxU = minU + (uPx * 8);
			float maxV = minV + (vPx * 8);
		
		
			quad(a,b,c,d,
				new Vec2f(minU, minV),
				new Vec2f(minU, maxV),
				new Vec2f(maxU, maxV),
				new Vec2f(maxU, minV),
				
				true,
				
				vertexConsumers, matrices,
				square.color(), light
				);
		} else {
			ScarvesMod.LOGGER.error("Block Atlas Texture isn't a block atlas");
		}
	}
}
