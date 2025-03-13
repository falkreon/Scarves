package blue.endless.scarves.client;

import java.util.List;

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
		
		
		//MinecraftClient.getInstance().getEntityRenderDispatcher().getRenderer(MinecraftClient.getInstance().player);
		//PlayerEntityModel.getModelData(Dilation.NONE, 0).getRoot().createPart(64, 64);
		//PlayerEntityModel<ClientPlayerEntity> model = new PlayerEntityModel<>(ctx.getPart(slim ? EntityModelLayers.PLAYER_SLIM : EntityModelLayers.PLAYER), slim)
		
		//PlayerEntityModel<ClientPlayerEntity> model = new PlayerEntityModel<ClientPlayerEntity>(.getRoot(), false);
	}
	
	public static int getScarfTint(ItemStack stack, int index) {
		ScarfDesign design = stack.get(ScarfDesign.COMPONENT);
		if (design == null) return UNCOLORED_SCARF_TINT;
		if (design.squares().size() == 0) return UNCOLORED_SCARF_TINT;
		
		FabricSquare square = design.squares().get(index % design.squares().size());
		
		return square.colorHint();
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
				
				/*
				Map<String, Matrix4f> registrations = ModelExtractor.extract(entity, tickDelta);
				Matrix4f bodyMatrix = registrations.get("body");
				if (bodyMatrix != null) {
					if (entity instanceof AbstractClientPlayerEntity player) {
						//MORE DEBUG
						System.out.println("\n"+bodyMatrix.toString());
					}
					
					
					Vec3d lerpedPosD = entity.getLerpedPos(tickDelta);
					Vector4f lerpedPos = new Vector4f((float) lerpedPosD.x, (float) lerpedPosD.y, (float) lerpedPosD.z, 1);
					
					ctx.matrixStack().push();
					
					Matrix4f bodyRotation = new Matrix4f().rotationY((float) -(scarfHaver.iScarfHaver_getBodyYaw(tickDelta) * Math.PI / 180d));
					
					Vector4f a = new Vector4f(-4/16f, 0, -2/16f, 1);
					Vector4f b = new Vector4f(-4/16f, 12/16f, -2/16f, 1);
					Vector4f c = new Vector4f( 4/16f, 12/16f, -2/16f, 1);
					Vector4f d = new Vector4f( 4/16f, 0, -2/16f, 1);
					
					a.add(0, -1.501f, 0, 0).mul(-1, -1, 1, 1);
					b.add(0, -1.501f, 0, 0).mul(-1, -1, 1, 1);
					c.add(0, -1.501f, 0, 0).mul(-1, -1, 1, 1);
					d.add(0, -1.501f, 0, 0).mul(-1, -1, 1, 1);
					
					bodyMatrix.transform(a);
					bodyMatrix.transform(b);
					bodyMatrix.transform(c);
					bodyMatrix.transform(d);
					
					a.add(0,0,-0.25f, 0);
					b.add(0,0,-0.25f, 0);
					c.add(0,0,-0.25f, 0);
					d.add(0,0,-0.25f, 0);
					
					bodyRotation.transform(a);
					bodyRotation.transform(b);
					bodyRotation.transform(c);
					bodyRotation.transform(d);
					
					a.add(lerpedPos);
					b.add(lerpedPos);
					c.add(lerpedPos);
					d.add(lerpedPos);
					
					ScarfRenderer.quad(
							a,
							b,
							c,
							d,
							
							new FabricSquare(Identifier.of("minecraft", "block/white_wool")),
							
							ctx.consumers(),
							ctx.matrixStack(),
							LightmapTextureManager.pack(15,15)
							);
					
					ctx.matrixStack().pop();
				}*/
				
				try {
					//final boolean tickDeprived = (entity instanceof ITickDeprivationAware depAware) ?
					//	depAware.scarves_isTickDeprived(ctx.world().getTime()) :
					//	false;
					//final boolean tickDeprived = false;
					
					scarfHaver.iScarfHaver_getAttachments(ctx.tickCounter().getTickDelta(false)).forEach( it-> {
						//Physics - gravity and collisions run on the tick thread
						List<ScarfNode> nodes = it.nodes();
						if (nodes.isEmpty()) return;
						/*
						nodes.get(0).pullTowards(it.getLocation());
						if (nodes.size()>1) for(int i=1; i<nodes.size(); i++) {
							ScarfNode prev = nodes.get(i-1);
							ScarfNode cur = nodes.get(i);
							cur.pullTowards(prev.position);
						}*/
						
						//Rendering
						Vec3d prev = it.getLocation();
						Vec3d prevUp = new Vec3d(0,1,0).multiply(ScarfNode.FABRIC_SQUARE_WIDTH);
						for(int i=0; i<nodes.size(); i++) {
							ScarfNode cur = nodes.get(i);
							Vec3d lerpedPos = cur.getLerpedPosition(tickDelta);
							//Vec3d lerpedPos = (tickDeprived) ? 
							//		cur.getPosition() :
							//		cur.getLerpedPosition(ctx.tickCounter().getTickDelta(false));
							
							BlockPos curPos = new BlockPos(
									(int) lerpedPos.x,
									(int) (lerpedPos.y + 0.25),
									(int) lerpedPos.z
									);
							Vec3d forwardVec = lerpedPos.subtract(prev).normalize();
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
									lerpedPos.add(curUp),
									lerpedPos,
									
									cur.square,
									
									ctx.consumers(),
									ctx.matrixStack(),
									nodeLight
									);
							
							prev = lerpedPos;
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
