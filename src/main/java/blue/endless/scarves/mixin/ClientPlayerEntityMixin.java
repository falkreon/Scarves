package blue.endless.scarves.mixin;

import java.util.stream.Stream;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import blue.endless.scarves.ScarvesItems;
import blue.endless.scarves.client.IScarfHaver;
import blue.endless.scarves.client.ScarfAttachment;
import blue.endless.scarves.client.SimpleScarfAttachment;
import dev.emi.trinkets.api.TrinketComponent;
import dev.emi.trinkets.api.TrinketsApi;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.recipebook.ClientRecipeBook;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.ItemStack;
import net.minecraft.stat.StatHandler;

@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin extends ClientPlayerEntity implements IScarfHaver {
	
	public ClientPlayerEntityMixin(MinecraftClient client, ClientWorld world, ClientPlayNetworkHandler networkHandler,
			StatHandler stats, ClientRecipeBook recipeBook, boolean lastSneaking, boolean lastSprinting) {
		super(client, world, networkHandler, stats, recipeBook, lastSneaking, lastSprinting);
	}

	private SimpleScarfAttachment scarves_leftScarf;
	private SimpleScarfAttachment scarves_rightScarf;
	
	@Override
	public Stream<ScarfAttachment> iScarfHaver_getAttachments() {
		TrinketComponent component = TrinketsApi.getTrinketComponent((ClientPlayerEntity)(Object) this).orElse(null);
		if (component !=null) {
			for(var equipped : component.getEquipped(ScarvesItems.SCARF)) {
				ItemStack stack = equipped.getRight();
				
				//TODO: Update scarf attachment
				if (scarves_leftScarf==null) {
					scarves_leftScarf = new SimpleScarfAttachment();
				}
				if (scarves_rightScarf==null) {
					scarves_rightScarf = new SimpleScarfAttachment();
				}
				
				float delta = 0f;
				scarves_leftScarf.setLocation(this.getLerpedPos(delta));
				
				return Stream.of(scarves_leftScarf, scarves_rightScarf);
			}
			
			return Stream.empty();
		} else {
			return Stream.empty();
		}
	}
}
