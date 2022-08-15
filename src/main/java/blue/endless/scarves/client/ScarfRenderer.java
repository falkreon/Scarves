package blue.endless.scarves.client;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Matrix4f;
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
		
		buf.vertex(matrix, (float) a.x, (float) a.y, (float) a.z).color(0xFF_FFFFFF).texture(texA.x, texA.y).light(light).normal((float) normal.x, (float) normal.y, (float) normal.z).next();
		buf.vertex(matrix, (float) b.x, (float) b.y, (float) b.z).color(0xFF_FFFFFF).texture(texB.x, texB.y).light(light).normal((float) normal.x, (float) normal.y, (float) normal.z).next();
		buf.vertex(matrix, (float) c.x, (float) c.y, (float) c.z).color(0xFF_FFFFFF).texture(texC.x, texC.y).light(light).normal((float) normal.x, (float) normal.y, (float) normal.z).next();
		buf.vertex(matrix, (float) d.x, (float) d.y, (float) d.z).color(0xFF_FFFFFF).texture(texD.x, texD.y).light(light).normal((float) normal.x, (float) normal.y, (float) normal.z).next();
		
		normal = normal.multiply(-1);
		
		if (doubleSided) {
			buf.vertex(matrix, (float) d.x, (float) d.y, (float) d.z).color(0xFF_FFFFFF).texture(texD.x, texD.y).light(light).normal((float) normal.x, (float) normal.y, (float) normal.z).next();
			buf.vertex(matrix, (float) c.x, (float) c.y, (float) c.z).color(0xFF_FFFFFF).texture(texC.x, texC.y).light(light).normal((float) normal.x, (float) normal.y, (float) normal.z).next();
			buf.vertex(matrix, (float) b.x, (float) b.y, (float) b.z).color(0xFF_FFFFFF).texture(texB.x, texB.y).light(light).normal((float) normal.x, (float) normal.y, (float) normal.z).next();
			buf.vertex(matrix, (float) a.x, (float) a.y, (float) a.z).color(0xFF_FFFFFF).texture(texA.x, texA.y).light(light).normal((float) normal.x, (float) normal.y, (float) normal.z).next();
		}
	}
	
	public static void quad(Vec3d a, Vec3d b, Vec3d c, Vec3d d, FabricSquare tex, boolean doubleSided, VertexConsumerProvider vertexConsumers, MatrixStack matrices, int light) {
		
	}
}
