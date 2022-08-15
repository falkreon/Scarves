package blue.endless.scarves;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.fabricmc.api.ModInitializer;

public class ScarvesMod implements ModInitializer {
	public static final String MODID = "scarves";
	public static final Logger LOGGER = LoggerFactory.getLogger("Scarves");
	
	@Override
	public void onInitialize() {
		
		//ScarvesBlocks.register();
		ScarvesItems.register();
	}

}
