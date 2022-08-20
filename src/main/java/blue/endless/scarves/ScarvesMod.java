package blue.endless.scarves;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public class ScarvesMod implements ModInitializer {
	public static final String MODID = "scarves";
	public static final Logger LOGGER = LoggerFactory.getLogger("Scarves");
	
	public static ItemGroup ITEM_GROUP;
	
	@Override
	public void onInitialize() {
		ITEM_GROUP = FabricItemGroupBuilder
			.create(new Identifier(MODID, "general"))
			
			.build();
			//.build(
			//	new Identifier(MODID, "general"),
			//	()->new ItemStack(ScarvesItems.SCARF_TABLE)
			//);
		
		
		ScarvesBlocks.register();
		ScarvesItems.register();
	}

}
