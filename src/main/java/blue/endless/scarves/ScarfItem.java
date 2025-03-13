package blue.endless.scarves;

import java.util.ArrayList;
import java.util.List;

import blue.endless.scarves.api.FabricSquare;
import blue.endless.scarves.api.RepeatType;
import blue.endless.scarves.api.ScarfDesign;
import dev.emi.trinkets.api.TrinketItem;
import io.github.queerbric.pride.PrideFlag;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;

public class ScarfItem extends TrinketItem {
	public static final String ID = "scarf";
	public static final int MAX_CREATIVE_SCARF_LENGTH = 8;
	
	public ScarfItem() {
		super(new Item.Settings()
				.component(ScarfDesign.COMPONENT, new ScarfDesign(RepeatType.FROM_START, 0, List.of()))
				.rarity(Rarity.UNCOMMON));
	}
	
	
	
	public static ScarfDesign createScarf(PrideFlag flag, int minLength) {
		List<FabricSquare> pattern = new ArrayList<>();
		for(int col : flag.getColors()) {
			pattern.add(new FabricSquare(Identifier.of("minecraft", "block/white_wool"), col | 0xFF_000000));
		}
		int repeatCount = 1;
		while(repeatCount * pattern.size() < minLength) repeatCount++;
		
		return new ScarfDesign(RepeatType.FROM_START, repeatCount, List.copyOf(pattern));
	}
}
