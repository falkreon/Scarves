package blue.endless.scarves.client;

import java.util.List;

import blue.endless.scarves.ScarvesBlocks;
import blue.endless.scarves.gui.ScarfStaplerGuiDescription;
import blue.endless.scarves.gui.ScarfTableGuiDescription;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.LightType;

public class ScarvesClient implements ClientModInitializer {
	public static final double SCARF_GRAVITY = -0.0025;
	
	@Override
	public void onInitializeClient() {
		
		WorldRenderEvents.BEFORE_ENTITIES.register(ScarvesClient::beforeEntities);
		
		HandledScreens.<ScarfStaplerGuiDescription, ScarfStaplerScreen>register(ScarvesBlocks.SCARF_STAPLER_SCREEN_HANDLER, (gui, inventory, title) -> new ScarfStaplerScreen(gui, inventory, title));
		HandledScreens.<ScarfTableGuiDescription, ScarfTableScreen>register(ScarvesBlocks.SCARF_TABLE_SCREEN_HANDLER, (gui, inventory, title) -> new ScarfTableScreen(gui, inventory, title));
	}
	
	
	public static void beforeEntities(WorldRenderContext ctx) {
		ctx.matrixStack().push();
		
		//Get into worldspace from cameraspace
		Vec3d pos = ctx.camera().getPos();
		ctx.matrixStack().translate(-pos.x, -pos.y, -pos.z);
		
		
		//BlockPos scarfPos = new BlockPos(0,0,0);
		//int blockLight = ctx.world().getLightLevel(LightType.BLOCK, scarfPos);
		//int skyLight = ctx.world().getLightLevel(LightType.SKY, scarfPos);
		//int light = LightmapTextureManager.pack(blockLight, skyLight);

		
		for(Entity entity : ctx.world().getEntities()) {
			if (entity instanceof IScarfHaver scarfHaver) {
				try {
					scarfHaver.iScarfHaver_getAttachments(ctx.tickDelta()).forEach( it-> {
						//Physics
						List<ScarfNode> nodes = it.nodes();
						if (nodes.isEmpty()) return;
						nodes.get(0).pullTowards(it.getLocation());
						if (nodes.size()>1) for(int i=1; i<nodes.size(); i++) {
							ScarfNode prev = nodes.get(i-1);
							ScarfNode cur = nodes.get(i);
							cur.pullTowards(prev.position);
						}
						
						//TODO: Gravity, floor collisions
						
						//Rendering
						Vec3d prev = it.getLocation();
						for(int i=0; i<nodes.size(); i++) {
							ScarfNode cur = nodes.get(i);
							
							BlockPos curPos = new BlockPos(cur.position.add(0,0.25,0));
							int nodeLight = (cur.square.emissive()) ?
									LightmapTextureManager.pack(15,15) :
									
									LightmapTextureManager.pack(
									ctx.world().getLightLevel(LightType.BLOCK, curPos),
									ctx.world().getLightLevel(LightType.SKY, curPos)
									);
							
							ScarfRenderer.quad(
									prev,
									prev.add(0,ScarfNode.FABRIC_SQUARE_WIDTH,0),
									cur.position.add(0,ScarfNode.FABRIC_SQUARE_WIDTH,0),
									cur.position,
									
									cur.square,
									
									ctx.consumers(),
									ctx.matrixStack(),
									nodeLight
									);
							
							prev = cur.position;
						}
					});
				} catch (Throwable t) {
					//TODO: Quietly flag the player with an error?
					t.printStackTrace();
				}
			}
		}
		
		/*
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
		*/
		//buf.vertex(ctx.matrixStack().peek().getPositionMatrix(), 0, 0, 0).color(0xFF_FFFFFF).texture(1, 0).light(light).normal(0, 1, 0).next();
		//buf.vertex(ctx.matrixStack().peek().getPositionMatrix(), 1, 0, 1).color(0xFF_FFFFFF).texture(0, 0).light(light).normal(0, 1, 0).next();
		
		
		
		ctx.matrixStack().pop();
	}
}
