package blue.endless.scarves.client;

import com.mojang.blaze3d.systems.RenderSystem;

import blue.endless.scarves.ScarvesItems;
import blue.endless.scarves.ScarvesMod;
import dev.emi.trinkets.api.client.TrinketRendererRegistry;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.LightType;

public class ScarvesClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		// TODO Auto-generated method stub
		ScarvesMod.LOGGER.info("Client initialized ###############################");
		//TrinketRendererRegistry.registerRenderer(ScarvesItems.SCARF, new ScarfTrinketRenderer());
		
		
		WorldRenderEvents.BEFORE_ENTITIES.register(ScarvesClient::beforeEntities);
		
	}
	
	
	public static void beforeEntities(WorldRenderContext ctx) {
		ctx.matrixStack().push();
		
		Vec3d pos = ctx.camera().getPos();
		ctx.matrixStack().translate(-pos.x, -pos.y, -pos.z);
		
		//RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
		//ItemStack stack = new ItemStack(Blocks.STONE);
		//int light = 0xF000F0; //WorldRenderer.getLightmapCoordinates(ctx.world(), new BlockPos(0,0,0));
		BlockPos scarfPos = new BlockPos(0,0,0);
		int blockLight = ctx.world().getLightLevel(LightType.BLOCK, scarfPos);
		int skyLight = ctx.world().getLightLevel(LightType.SKY, scarfPos);
		int light = LightmapTextureManager.pack(blockLight, skyLight);
		//MinecraftClient.getInstance().getItemRenderer().renderItem(stack, ModelTransformation.Mode.FIXED, light, 0, ctx.matrixStack(), ctx.consumers(), 0);
		
		/*
		VertexConsumer buf = ctx.consumers().getBuffer(RenderLayer.getCutoutMipped());
		buf.vertex(ctx.matrixStack().peek().getPositionMatrix(), 0, 0, 0).color(0xFF_FFFFFF).texture(0, 0).light(light).normal(0, 1, 0).next();
		buf.vertex(ctx.matrixStack().peek().getPositionMatrix(), 0, 0, 1).color(0xFF_FFFFFF).texture(0, 1).light(light).normal(0, 1, 0).next();
		buf.vertex(ctx.matrixStack().peek().getPositionMatrix(), 1, 0, 1).color(0xFF_FFFFFF).texture(1, 1).light(light).normal(0, 1, 0).next();
		buf.vertex(ctx.matrixStack().peek().getPositionMatrix(), 1, 0, 0).color(0xFF_FFFFFF).texture(1, 0).light(light).normal(0, 1, 0).next();
		*/
		
		for(Entity entity : ctx.world().getEntities()) {
			if (entity instanceof IScarfHaver scarfHaver) {
				try {
				scarfHaver.iScarfHaver_getAttachments().forEach( it-> {
					
				});
				} catch (Throwable t) {
					//TODO: Quietly flag the player
				}
			}
		}
		
		SpriteIdentifier spriteId = new SpriteIdentifier(new Identifier("minecraft", "stone"), PlayerScreenHandler.BLOCK_ATLAS_TEXTURE);
		RenderSystem.setShaderTexture(0, spriteId.getTextureId());
		AbstractTexture tex = MinecraftClient.getInstance().getTextureManager().getTexture(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE);
		
		float minU = 0;
		float minV = 0;
		float maxU = 0.5f;
		float maxV = 0.5f;
		
		float pxX = (maxU-minU) / 16.0f;
		float pxY = (maxV-minV) / 16.0f;
		
		if (tex instanceof SpriteAtlasTexture atlas) {
			Sprite sprite = atlas.getSprite(new Identifier("minecraft", "block/white_wool"));
			if (sprite!=null) {
				minU = sprite.getMinU();// + (4*pxX);
				minV = sprite.getMinV();// + (4*pxY);
				maxU = sprite.getMaxU();// - (4*pxX);
				maxV = sprite.getMaxV();// - (4*pxY);
			} //sprite will never be null
		}
		
		ScarfRenderer.quad(
				new Vec3d(0  , 0  , 0  ),
				new Vec3d(0  , 0.5, 0  ),
				new Vec3d(0.5, 0.5, 0  ),
				new Vec3d(0.5, 0  , 0  ),
				
				new Vec2f(minU, minV),
				new Vec2f(minU, maxV),
				new Vec2f(maxU, maxV),
				new Vec2f(maxU, minV),
				
				true,
				
				ctx.consumers(),
				ctx.matrixStack(),
				
				0xFF_77FF77, light);
		
		//buf.vertex(ctx.matrixStack().peek().getPositionMatrix(), 0, 0, 0).color(0xFF_FFFFFF).texture(1, 0).light(light).normal(0, 1, 0).next();
		//buf.vertex(ctx.matrixStack().peek().getPositionMatrix(), 1, 0, 1).color(0xFF_FFFFFF).texture(0, 0).light(light).normal(0, 1, 0).next();
		
		
		
		ctx.matrixStack().pop();
	}
}
