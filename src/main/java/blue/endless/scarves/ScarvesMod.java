package blue.endless.scarves;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import blue.endless.scarves.api.ScarvesApi;
import blue.endless.scarves.api.ScarvesIntegration;
import blue.endless.scarves.ghost.GhostInventoryNetworking;
import blue.endless.scarves.integration.StaticDataIntegration;
import io.github.queerbric.pride.PrideFlag;
import io.github.queerbric.pride.PrideFlags;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.entrypoint.EntrypointContainer;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ScarvesMod implements ModInitializer {
	public static final String MODID = "scarves";
	public static final Logger LOGGER = LoggerFactory.getLogger("Scarves");
	
	public static final Identifier GHOST_SLOT_MESSAGE = new Identifier(MODID, "ghost");
	
	public static ItemGroup ITEM_GROUP;
	
	private static final List<ItemStack> creativeScarves = new ArrayList<>();
	
	@Override
	public void onInitialize() {
		ITEM_GROUP = FabricItemGroup.builder()
			.displayName(Text.literal("Scarves"))
			.icon(()->new ItemStack(ScarvesItems.SCARF))
			.entries((ItemGroup.DisplayContext context, ItemGroup.Entries entries) -> {
				entries.add(new ItemStack(ScarvesItems.SCARF));
				entries.add(new ItemStack(ScarvesItems.SCARF_STAPLER));
				entries.add(new ItemStack(ScarvesItems.SCARF_TABLE));
				
				addPrideScarves(entries);
				for(ItemStack stack : creativeScarves) entries.add(stack);
			})
			.build();
		Registry.register(Registries.ITEM_GROUP, new Identifier(MODID, "general"), ITEM_GROUP);
		
		ScarvesBlocks.register();
		ScarvesItems.register();
		
		//TODO: I'd love to use static data to load in custom scarves squares but I can't.
		
		GhostInventoryNetworking.init();
		
		for (EntrypointContainer<ScarvesIntegration> entrypoint : FabricLoader.getInstance().getEntrypointContainers(MODID, ScarvesIntegration.class)) {
			try {
				entrypoint.getEntrypoint().integrateWithScarves(ScarvesApi.instance());
			} catch (Throwable t) {
				LOGGER.error("Mod '"+entrypoint.getProvider().getMetadata().getId()+"' threw an exception trying to activate Scarves integration.", t);
			}
		}
		
		StaticDataIntegration.init();
	}
	
	public static void addCreativeScarf(ItemStack scarfItem) {
		creativeScarves.add(scarfItem);
	}
	
	public static void addPrideScarves(ItemGroup.Entries entries) {
		for(PrideFlag flag : PrideFlags.getFlags()) {
			if (flag==null) {
				ScarvesMod.LOGGER.warn("Null pride flag found!");
				continue;
			}
			int reps = ScarfItem.MAX_CREATIVE_SCARF_LENGTH / flag.getColors().size();
			if (reps < 1) reps = 1;
			int flagLength = flag.getColors().size() * reps;
			
			ItemStack scarf = ScarfItem.createScarf(flag, flagLength, flag, 0);
			
			//Create name
			String flagKey = "flag.pridelib."+flag.getId();
			Text flagName = (I18n.hasTranslation(flagKey)) ? Text.translatable(flagKey) : Text.literal(StringUtils.capitalize(flag.getId()));
			
			Text name = Text.translatable("item.scarves.scarf.named", flagName);
			scarf = ScarfItem.setName(scarf, name);
			
			entries.add(scarf);
		}
		
		PrideFlag biFlag = PrideFlags.getFlag("bisexual");
		PrideFlag panFlag = PrideFlags.getFlag("pansexual");
		if (biFlag!=null && panFlag!=null) {
			ItemStack biPanScarf = ScarfItem.createScarf(biFlag, 30, panFlag, 15);
			Text biPanName = Text.literal(StringUtils.capitalize("Bi/Pan"));
			Text name = Text.translatable("item.scarves.scarf.named", biPanName);
			biPanScarf = ScarfItem.setName(biPanScarf, name);
			entries.add(biPanScarf);
		}
	}
}
