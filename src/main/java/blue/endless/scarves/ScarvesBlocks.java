package blue.endless.scarves;

import blue.endless.scarves.gui.ScarfStaplerGuiDescription;
import blue.endless.scarves.gui.ScarfTableGuiDescription;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;

public class ScarvesBlocks {
	
	public static ScarfStaplerBlock SCARF_STAPLER;
	public static ScarfTableBlock SCARF_TABLE;
	
	public static BlockEntityType<ScarfStaplerBlockEntity> SCARF_STAPLER_ENTITY;
	public static BlockEntityType<ScarfTableBlockEntity> SCARF_TABLE_ENTITY;
	
	public static ScreenHandlerType<ScarfStaplerGuiDescription> SCARF_STAPLER_SCREEN_HANDLER;
	public static ScreenHandlerType<ScarfTableGuiDescription> SCARF_TABLE_SCREEN_HANDLER;
	
	public static void register() {
		SCARF_STAPLER = register(new ScarfStaplerBlock(), ScarfStaplerBlock.ID);
		SCARF_TABLE = register(new ScarfTableBlock(), ScarfTableBlock.ID);
		
		SCARF_STAPLER_ENTITY = register(ScarfStaplerBlock.ID, ScarfStaplerBlockEntity::new, SCARF_STAPLER);
		
		SCARF_TABLE_ENTITY = register(ScarfTableBlock.ID, ScarfTableBlockEntity::new, SCARF_TABLE);
		
		SCARF_STAPLER_SCREEN_HANDLER = Registry.register(
				Registries.SCREEN_HANDLER,
				ScarfStaplerBlock.ID,
				new ScreenHandlerType<ScarfStaplerGuiDescription>(
						(syncId, inventory) -> new ScarfStaplerGuiDescription(syncId, inventory, ScreenHandlerContext.EMPTY),
						FeatureSet.empty()
						)
				);
		SCARF_TABLE_SCREEN_HANDLER = Registry.register(
				Registries.SCREEN_HANDLER,
				ScarfTableBlock.ID,
				new ScreenHandlerType<ScarfTableGuiDescription>(
						(syncId, inventory) -> new ScarfTableGuiDescription(syncId, inventory, ScreenHandlerContext.EMPTY),
						FeatureSet.empty()
						)
				);
	}
	
	private static <T extends Block> T register(T block, String id) {
		Registry.register(Registries.BLOCK, new Identifier(ScarvesMod.MODID, id), (Block) block);
		return block;
	}
	
	private static <T extends BlockEntity> BlockEntityType<T> register(String id, FabricBlockEntityTypeBuilder.Factory<T> factory, Block block) {
		BlockEntityType<T> result = FabricBlockEntityTypeBuilder.<T>create(factory, block).build();
		Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(ScarvesMod.MODID, id), result);
		return result;
	}
}
