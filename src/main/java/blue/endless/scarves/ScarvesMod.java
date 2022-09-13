package blue.endless.scarves;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import blue.endless.scarves.api.ScarvesIntegration;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.entrypoint.EntrypointContainer;
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
			.icon(()->new ItemStack(ScarvesItems.SCARF))
			.build();
		
		ScarvesBlocks.register();
		ScarvesItems.register();
		
		
		for (EntrypointContainer<ScarvesIntegration> entrypoint : FabricLoader.getInstance().getEntrypointContainers(MODID, ScarvesIntegration.class)) {
			try {
				entrypoint.getEntrypoint().integrateWithScarves();
			} catch (Throwable t) {
				LOGGER.error("Mod '"+entrypoint.getProvider().getMetadata().getId()+"' threw an exception trying to activate Scarves integration.", t);
			}
		}
	}

}
