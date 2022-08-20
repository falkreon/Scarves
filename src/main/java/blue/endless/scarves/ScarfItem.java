package blue.endless.scarves;

import org.apache.commons.lang3.StringUtils;

import blue.endless.scarves.api.FabricSquare;
import dev.emi.trinkets.api.TrinketItem;
import io.github.queerbric.pride.PrideFlag;
import io.github.queerbric.pride.PrideFlags;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.collection.DefaultedList;

public class ScarfItem extends TrinketItem {
	public static final String ID = "scarf";
	
	public ScarfItem() {
		super(new FabricItemSettings().rarity(Rarity.UNCOMMON).group(ScarvesMod.ITEM_GROUP));
	}

	@Override
	public void appendStacks(ItemGroup group, DefaultedList<ItemStack> stacks) {
		super.appendStacks(group, stacks);
		if (this.isIn(group)) {
			for(PrideFlag flag : PrideFlags.getFlags()) {
				ItemStack scarf = createScarf(flag, 30, flag, 15);
				
				//Create name
				String flagKey = "flag.pridelib."+flag.getId();
				Text flagName = (I18n.hasTranslation(flagKey)) ? Text.translatable(flagKey) : Text.literal(StringUtils.capitalize(flag.getId()));
				
				Text name = Text.translatable("item.scarves.scarf.named", flagName);
				
				stacks.add(setName(scarf, name));
			}
		}
	}
	
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
	
	private ItemStack setName(ItemStack stack, Text name) {
		NbtCompound tag = stack.getOrCreateNbt();
		NbtCompound display = new NbtCompound();
			String json = Text.Serializer.toJson(name);
			display.putString("Name", json);
		tag.put("display", display);
		return stack;
	}
}
