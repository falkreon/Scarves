package blue.endless.scarves.client;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import blue.endless.scarves.ScarfItem;
import blue.endless.scarves.ScarvesBlocks;
import blue.endless.scarves.ScarvesMod;
import blue.endless.scarves.gui.ScarfStaplerGuiDescription;
import blue.endless.scarves.gui.ScarfTableGuiDescription;
import io.github.queerbric.pride.PrideFlag;
import io.github.queerbric.pride.PrideFlags;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.LightType;

public class ScarvesClient implements ClientModInitializer {
	public static final double SCARF_GRAVITY = -0.01;
	
	@Override
	public void onInitializeClient() {
		//addPrideScarves();
		
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
						//Physics - gravity and collisions run on the tick thread
						List<ScarfNode> nodes = it.nodes();
						if (nodes.isEmpty()) return;
						nodes.get(0).pullTowards(it.getLocation());
						if (nodes.size()>1) for(int i=1; i<nodes.size(); i++) {
							ScarfNode prev = nodes.get(i-1);
							ScarfNode cur = nodes.get(i);
							cur.pullTowards(prev.position);
						}
						
						//Rendering
						Vec3d prev = it.getLocation();
						Vec3d prevUp = new Vec3d(0,1,0).multiply(ScarfNode.FABRIC_SQUARE_WIDTH);
						for(int i=0; i<nodes.size(); i++) {
							ScarfNode cur = nodes.get(i);
							Vec3d lerpedPos = cur.getLerpedPosition(ctx.tickDelta());
							
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
									//prev.add(0,ScarfNode.FABRIC_SQUARE_WIDTH,0),
									//cur.position.add(0,ScarfNode.FABRIC_SQUARE_WIDTH,0),
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
