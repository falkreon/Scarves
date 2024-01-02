package blue.endless.scarves;

import blue.endless.scarves.api.FabricSquare;
import dev.emi.trinkets.api.TrinketItem;
import io.github.queerbric.pride.PrideFlag;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;

public class ScarfItem extends TrinketItem {
	public static final String ID = "scarf";
	public static final int MAX_CREATIVE_SCARF_LENGTH = 8;
	
	public ScarfItem() {
		super(new FabricItemSettings().rarity(Rarity.UNCOMMON));
				//.group(ScarvesMod.ITEM_GROUP)); //TODO: Uh oh
	}
	
	//TODO: The creative menu changed again, and probably in an obnoxious way
	/*
	@Override
	public void appendStacks(ItemGroup group, DefaultedList<ItemStack> stacks) {
		super.appendStacks(group, stacks);
		if (this.isIn(group)) {
			for(PrideFlag flag : PrideFlags.getFlags()) {
				int reps = MAX_CREATIVE_SCARF_LENGTH / flag.getColors().size();
				if (reps < 1) reps = 1;
				int flagLength = flag.getColors().size() * reps;
				
				ItemStack scarf = createScarf(flag, flagLength, flag, 0);
				
				//Create name
				String flagKey = "flag.pridelib."+flag.getId();
				Text flagName = (I18n.hasTranslation(flagKey)) ? Text.translatable(flagKey) : Text.literal(StringUtils.capitalize(flag.getId()));
				
				Text name = Text.translatable("item.scarves.scarf.named", flagName);
				
				stacks.add(setName(scarf, name));
			}
			
			ItemStack biPanScarf = createScarf(PrideFlags.getFlag("bisexual"), 30, PrideFlags.getFlag("pansexual"), 15);
			Text biPanName = Text.literal(StringUtils.capitalize("Bi/Pan"));
			Text name = Text.translatable("item.scarves.scarf.named", biPanName);
			stacks.add(setName(biPanScarf, name));
		}
	}*/
	
	public static NbtList createTail(PrideFlag flag, int minLength) {
		NbtList result = new NbtList();
		
		int length = 0;
		while(length<minLength) {
			for(int col : flag.getColors()) {
				FabricSquare square = new FabricSquare(new Identifier("minecraft", "block/white_wool"), col | 0xFF_000000);
				result.add(square.toCompound());
				
				length++;
			}
		}
		
		return result;
	}
	
	public static ItemStack createScarf(PrideFlag leftFlag, int leftLength, PrideFlag rightFlag, int rightLength) {
		ItemStack stack = new ItemStack(ScarvesItems.SCARF);
		
		NbtCompound tag = stack.getOrCreateNbt();
		tag.put("LeftScarf", createTail(leftFlag, leftLength));
		tag.put("RightScarf", createTail(rightFlag, rightLength));
		
		return stack;
	}
	
	public static ItemStack setName(ItemStack stack, Text name) {
		NbtCompound tag = stack.getOrCreateNbt();
		NbtCompound display = new NbtCompound();
		
			String json = Text.Serialization.toJsonString(name);
			display.putString("Name", json);
		tag.put("display", display);
		return stack;
	}
}
