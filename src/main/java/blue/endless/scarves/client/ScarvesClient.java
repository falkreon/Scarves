package blue.endless.scarves.client;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import blue.endless.scarves.ScarvesBlocks;
import blue.endless.scarves.ScarvesItems;
import blue.endless.scarves.ghost.GhostInventoryNetworking;
import blue.endless.scarves.gui.ScarfStaplerGuiDescription;
import blue.endless.scarves.gui.ScarfTableGuiDescription;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.block.Blocks;
import net.minecraft.block.MapColor;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.LightType;

public class ScarvesClient implements ClientModInitializer {
	public static final double SCARF_GRAVITY = -0.01;
	public static final int UNCOLORED_SCARF_TINT = 0xFF_ddc8ab;
	
	private static Map<String, Integer> ALT_COLORS = new HashMap<>();
	
	static {
		ALT_COLORS.put("minecraft:block/white_wool",      Blocks.WHITE_WOOL.getDefaultMapColor().getRenderColor(MapColor.Brightness.NORMAL));
		ALT_COLORS.put("minecraft:block/orange_wool",     Blocks.ORANGE_WOOL.getDefaultMapColor().getRenderColor(MapColor.Brightness.NORMAL));
		ALT_COLORS.put("minecraft:block/magenta_wool",    Blocks.MAGENTA_WOOL.getDefaultMapColor().getRenderColor(MapColor.Brightness.NORMAL));
		ALT_COLORS.put("minecraft:block/light_blue_wool", Blocks.LIGHT_BLUE_WOOL.getDefaultMapColor().getRenderColor(MapColor.Brightness.NORMAL));
		ALT_COLORS.put("minecraft:block/yellow_wool",     Blocks.YELLOW_WOOL.getDefaultMapColor().getRenderColor(MapColor.Brightness.NORMAL));
		ALT_COLORS.put("minecraft:block/lime_wool",       Blocks.LIME_WOOL.getDefaultMapColor().getRenderColor(MapColor.Brightness.NORMAL));
		ALT_COLORS.put("minecraft:block/pink_wool",       Blocks.PINK_WOOL.getDefaultMapColor().getRenderColor(MapColor.Brightness.NORMAL));
		ALT_COLORS.put("minecraft:block/gray_wool",       Blocks.GRAY_WOOL.getDefaultMapColor().getRenderColor(MapColor.Brightness.NORMAL));
		ALT_COLORS.put("minecraft:block/light_gray_wool", Blocks.LIGHT_GRAY_WOOL.getDefaultMapColor().getRenderColor(MapColor.Brightness.NORMAL));
		ALT_COLORS.put("minecraft:block/cyan_wool",       Blocks.CYAN_WOOL.getDefaultMapColor().getRenderColor(MapColor.Brightness.NORMAL));
		ALT_COLORS.put("minecraft:block/purple_wool",     Blocks.PURPLE_WOOL.getDefaultMapColor().getRenderColor(MapColor.Brightness.NORMAL));
		ALT_COLORS.put("minecraft:block/blue_wool",       Blocks.BLUE_WOOL.getDefaultMapColor().getRenderColor(MapColor.Brightness.NORMAL));
		ALT_COLORS.put("minecraft:block/brown_wool",      Blocks.BROWN_WOOL.getDefaultMapColor().getRenderColor(MapColor.Brightness.NORMAL));
		ALT_COLORS.put("minecraft:block/green_wool",      Blocks.GREEN_WOOL.getDefaultMapColor().getRenderColor(MapColor.Brightness.NORMAL));
		ALT_COLORS.put("minecraft:block/red_wool",        Blocks.RED_WOOL.getDefaultMapColor().getRenderColor(MapColor.Brightness.NORMAL));
		ALT_COLORS.put("minecraft:block/black_wool",      Blocks.BLACK_WOOL.getDefaultMapColor().getRenderColor(MapColor.Brightness.NORMAL));
		ALT_COLORS.put("minecraft:block/water_still",     0xFF_3f51eb);
		ALT_COLORS.put("minecraft:block/lava_still",      0xFF_ff5100);
		ALT_COLORS.put("minecraft:block/redstone_block",  0xFF_b50000);
		ALT_COLORS.put("minecraft:block/glowstone",       0xFF_f6c921);
		ALT_COLORS.put("minecraft:block/bedrock",         0xFF_4a4a4a);
	}
	
	@Override
	public void onInitializeClient() {
		WorldRenderEvents.BEFORE_ENTITIES.register(ScarvesClient::beforeEntities);
		
		GhostInventoryNetworking.initClient();
		
		HandledScreens.<ScarfStaplerGuiDescription, ScarfStaplerScreen>register(ScarvesBlocks.SCARF_STAPLER_SCREEN_HANDLER, (gui, inventory, title) -> new ScarfStaplerScreen(gui, inventory, title));
		HandledScreens.<ScarfTableGuiDescription, ScarfTableScreen>register(ScarvesBlocks.SCARF_TABLE_SCREEN_HANDLER, (gui, inventory, title) -> new ScarfTableScreen(gui, inventory, title));
		
		ColorProviderRegistry.ITEM.register(ScarvesClient::getScarfTint, ScarvesItems.SCARF);
	}
	
	public static int getScarfTint(ItemStack stack, int index) {
		NbtCompound tag = stack.getNbt();
		if (tag == null) return UNCOLORED_SCARF_TINT;
		NbtList leftScarf = tag.getList("LeftScarf", NbtElement.COMPOUND_TYPE);
		NbtList rightScarf = tag.getList("RightScarf", NbtElement.COMPOUND_TYPE);
		NbtList scarf = (leftScarf == null || leftScarf.size() == 0) ? rightScarf : leftScarf;
		if (scarf.size() == 0) return UNCOLORED_SCARF_TINT;
		
		NbtCompound square = scarf.getCompound(index % scarf.size());
		
		int color = (square.contains("Color", NbtElement.INT_TYPE)) ? square.getInt("Color") : 0xFF_FFFFFF;
		if (color == 0xFF_FFFFFF) {
			try {
				String id = square.getString("Id");
				return ALT_COLORS.getOrDefault(id, 0xFF_FFFFFF);
			} catch (Throwable t) {}
		}
		
		return color;
	}
	
	public static void beforeEntities(WorldRenderContext ctx) {
		ctx.matrixStack().push();
		
		//Get into worldspace from cameraspace
		Vec3d pos = ctx.camera().getPos();
		ctx.matrixStack().translate(-pos.x, -pos.y, -pos.z);
		
		for(Entity entity : ctx.world().getEntities()) {
			if (entity instanceof IScarfHaver scarfHaver) {
				try {
					final boolean tickDeprived = (entity instanceof ITickDeprivationAware depAware) ?
						depAware.engination_isTickDeprived(ctx.world().getTime()) :
						false;
					
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
							Vec3d lerpedPos = (tickDeprived) ? 
									cur.getPosition() :
									cur.getLerpedPosition(ctx.tickDelta());
							
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
