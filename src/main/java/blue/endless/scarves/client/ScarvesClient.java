package blue.endless.scarves.client;

import java.util.List;
import java.util.Map;

import org.joml.Vector3f;

import blue.endless.scarves.ScarvesBlocks;
import blue.endless.scarves.ScarvesItems;
import blue.endless.scarves.api.FabricSquare;
import blue.endless.scarves.api.ScarfDesign;
import blue.endless.scarves.ghost.GhostInventoryNetworking;
import blue.endless.scarves.gui.ScarfStaplerGuiDescription;
import blue.endless.scarves.gui.ScarfTableGuiDescription;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.LightType;

public class ScarvesClient implements ClientModInitializer {
	public static final double SCARF_GRAVITY = -0.01;
	public static final int UNCOLORED_SCARF_TINT = 0xFF_ddc8ab;
	
	@Override
	public void onInitializeClient() {
		WorldRenderEvents.AFTER_ENTITIES.register(ScarvesClient::afterEntities);
		
		GhostInventoryNetworking.initClient();
		
		HandledScreens.<ScarfStaplerGuiDescription, ScarfStaplerScreen>register(ScarvesBlocks.SCARF_STAPLER_SCREEN_HANDLER, (gui, inventory, title) -> new ScarfStaplerScreen(gui, inventory, title));
		HandledScreens.<ScarfTableGuiDescription, ScarfTableScreen>register(ScarvesBlocks.SCARF_TABLE_SCREEN_HANDLER, (gui, inventory, title) -> new ScarfTableScreen(gui, inventory, title));
		
		ColorProviderRegistry.ITEM.register(ScarvesClient::getScarfTint, ScarvesItems.SCARF);
		
	}
	
	public static int getScarfTint(ItemStack stack, int index) {
		ScarfDesign design = stack.get(ScarfDesign.COMPONENT);
		if (design == null) return UNCOLORED_SCARF_TINT;
		if (design.squares().size() == 0) return UNCOLORED_SCARF_TINT;
		
		FabricSquare square = design.squares().get(index % design.squares().size());
		
		return square.colorHint() | 0xFF_000000;
	}
	
	
	
	public static void afterEntities(WorldRenderContext ctx) {
		if (ctx.matrixStack() == null) {
			return; // New kind of crash!
		}
		ctx.matrixStack().push();
		
		final float tickDelta = Math.min(ctx.tickCounter().getTickDelta(false), 1.0f);
		
		//Get into worldspace from cameraspace
		
		Vec3d pos = ctx.camera().getPos();
		ctx.matrixStack().translate(-pos.x, -pos.y, -pos.z);
		
		
		for(Entity entity : ctx.world().getEntities()) {
			if (entity instanceof IScarfHaver scarfHaver) {
				
				try {
					
					//Render debug cage
					/*
					final Vec3d lerpedPos = entity.getLerpedPos(tickDelta);
					final Vector3f lerpedPosF = new Vector3f((float) lerpedPos.x, (float) lerpedPos.y, (float) lerpedPos.z);
					Map<String, ModelExtractor.Part> parts = ModelExtractor.extractFully(entity, tickDelta);
					for(ModelExtractor.Part part : parts.values()) {
						//float bodyYaw = (float) -(scarfHaver.iScarfHaver_getBodyYaw(tickDelta) * Math.PI / 180);
						FabricSquare wool = new FabricSquare(Identifier.of("minecraft", "block/white_wool"));
						int fullbright = LightmapTextureManager.pack(LightmapTextureManager.MAX_BLOCK_LIGHT_COORDINATE, LightmapTextureManager.MAX_SKY_LIGHT_COORDINATE);
						
						// lerping outside 0<=t<=1 extrapolates instead of interpolating. This inflates the cube just a tiny bit.
						Vector3f a = part.transformRelative(new Vector3f(-0.01f,-0.01f, -0.01f)).add(lerpedPosF);
						Vector3f b = part.transformRelative(new Vector3f(-0.01f, 1.01f, -0.01f)).add(lerpedPosF);
						Vector3f c = part.transformRelative(new Vector3f( 1.01f, 1.01f, -0.01f)).add(lerpedPosF);
						Vector3f d = part.transformRelative(new Vector3f( 1.01f,-0.01f, -0.01f)).add(lerpedPosF);
						
						ScarfRenderer.quad(
								a, b, c, d,
								wool,
								ctx.consumers(),
								ctx.matrixStack(),
								fullbright
								);
						
						Vector3f e = part.transformRelative(new Vector3f( 1.01f, -0.01f, 1.01f)).add(lerpedPosF);
						Vector3f f = part.transformRelative(new Vector3f( 1.01f,  1.01f, 1.01f)).add(lerpedPosF);
						
						
						ScarfRenderer.quad(
								d, c, f, e,
								wool,
								ctx.consumers(),
								ctx.matrixStack(),
								fullbright
								);
					}*/
					
					scarfHaver.iScarfHaver_getAttachments(ctx.tickCounter().getTickDelta(false)).forEach( it-> {
						
						List<ScarfNode> nodes = it.nodes();
						if (nodes.isEmpty()) return;
						
						//Rendering
						Vec3d prev = it.getLocation();
						Vec3d prevUp = new Vec3d(0,ScarfNode.FABRIC_SQUARE_WIDTH,0);
						for(int i=0; i<nodes.size(); i++) {
							ScarfNode cur = nodes.get(i);
							Vec3d lerpedNodePos = cur.getLerpedPosition(tickDelta);
							
							BlockPos curPos = new BlockPos(
									(int) lerpedNodePos.x,
									(int) (lerpedNodePos.y + 0.25),
									(int) lerpedNodePos.z
									);
							Vec3d forwardVec = lerpedNodePos.subtract(prev).normalize();
							Vec3d tempUpVec = (forwardVec.x==0&&forwardVec.z==0) ? new Vec3d(1,0,0) : new Vec3d(0,1,0);
							Vec3d rightVec = forwardVec.crossProduct(tempUpVec);
							Vec3d curUp = forwardVec.crossProduct(rightVec).multiply(ScarfNode.FABRIC_SQUARE_WIDTH);
							
							int nodeLight = (cur.square.emissive()) ?
									LightmapTextureManager.pack(15,15) :
									
									LightmapTextureManager.pack(
									ctx.world().getLightLevel(LightType.BLOCK, curPos),
									ctx.world().getLightLevel(LightType.SKY, curPos)
									);
							
							ScarfRenderer.quad(
									prev,
									prev.add(prevUp),
									lerpedNodePos.add(curUp),
									lerpedNodePos,
									
									cur.square,
									
									ctx.consumers(),
									ctx.matrixStack(),
									nodeLight
									);
							
							prev = lerpedNodePos;
							prevUp = curUp;
						}
					});
				} catch (Throwable t) {
					//TODO: Quietly flag the player with an error?
					t.printStackTrace();
				}
			}
		}
		
		ctx.matrixStack().pop();
	}
	
	
}
