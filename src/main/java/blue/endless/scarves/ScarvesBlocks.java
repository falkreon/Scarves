package blue.endless.scarves;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ScarvesBlocks {
	public static ScarfTableBlock SCARF_TABLE;
	
	public static BlockEntityType<ScarfTableBlockEntity> SCARF_TABLE_ENTITY;
	
	public static void register() {
		SCARF_TABLE = register(new ScarfTableBlock(), ScarfTableBlock.ID);
		
		SCARF_TABLE_ENTITY = Registry.register(
				Registry.BLOCK_ENTITY_TYPE,
				new Identifier(ScarvesMod.MODID, ScarfTableBlock.ID),
				FabricBlockEntityTypeBuilder.<ScarfTableBlockEntity>create(ScarfTableBlockEntity::new, SCARF_TABLE).build()
				);
	}
	
	private  static <T extends Block> T register(T block, String id) {
		Registry.register(Registry.BLOCK, new Identifier(ScarvesMod.MODID, id), block);
		return block;
	}
}
