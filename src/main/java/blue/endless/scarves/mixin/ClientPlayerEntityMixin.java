package blue.endless.scarves.mixin;

import java.util.stream.Stream;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import com.mojang.authlib.GameProfile;

import blue.endless.scarves.ScarvesItems;
import blue.endless.scarves.ScarvesMod;
import blue.endless.scarves.client.FabricSquare;
import blue.endless.scarves.client.IScarfHaver;
import blue.endless.scarves.client.ScarfAttachment;
import blue.endless.scarves.client.ScarfNode;
import blue.endless.scarves.client.SimpleScarfAttachment;
import dev.emi.trinkets.api.TrinketComponent;
import dev.emi.trinkets.api.TrinketsApi;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.recipebook.ClientRecipeBook;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.ItemStack;
import net.minecraft.network.encryption.PlayerPublicKey;
import net.minecraft.stat.StatHandler;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin extends AbstractClientPlayerEntity implements IScarfHaver {
	private static final float SCARF_TAIL_SEPARATION = 0.19f;

	public ClientPlayerEntityMixin(ClientWorld world, GameProfile profile, PlayerPublicKey publicKey) {
		super(world, profile, publicKey);
	}

	private SimpleScarfAttachment scarves_leftScarf;
	private SimpleScarfAttachment scarves_rightScarf;
	
	@Override
	public Stream<ScarfAttachment> iScarfHaver_getAttachments(float delta) {
		
		TrinketComponent component = TrinketsApi.getTrinketComponent((ClientPlayerEntity)(Object) this).orElse(null);
		if (component !=null) {
			for(var equipped : component.getEquipped(ScarvesItems.SCARF)) {
				ItemStack stack = equipped.getRight();
				
				//TODO: Update scarf attachment
				if (scarves_leftScarf==null) {
					scarves_leftScarf = new SimpleScarfAttachment();
					scarves_leftScarf.nodes().add(new ScarfNode(
							this.getLerpedPos(delta),
							FabricSquare.of(new Identifier("minecraft:block/white_wool"))
							));
					scarves_leftScarf.nodes().add(new ScarfNode(
							this.getLerpedPos(delta),
							FabricSquare.of(new Identifier("minecraft:block/lime_wool"))
							));
					scarves_leftScarf.nodes().add(new ScarfNode(
							this.getLerpedPos(delta),
							FabricSquare.of(new Identifier("minecraft:block/white_wool"))
							));
					scarves_leftScarf.nodes().add(new ScarfNode(
							this.getLerpedPos(delta),
							FabricSquare.of(new Identifier("minecraft:block/lime_wool"))
							));
					scarves_leftScarf.nodes().add(new ScarfNode(
							this.getLerpedPos(delta),
							FabricSquare.of(new Identifier("minecraft:block/white_wool"))
							));
					scarves_leftScarf.nodes().add(new ScarfNode(
							this.getLerpedPos(delta),
							FabricSquare.of(new Identifier("minecraft:block/lime_wool"))
							));
					scarves_leftScarf.nodes().add(new ScarfNode(
							this.getLerpedPos(delta),
							FabricSquare.of(new Identifier("minecraft:block/white_wool"))
							));
					scarves_leftScarf.nodes().add(new ScarfNode(
							this.getLerpedPos(delta),
							FabricSquare.of(new Identifier("minecraft:block/lime_wool"))
							));
					scarves_leftScarf.nodes().add(new ScarfNode(
							this.getLerpedPos(delta),
							FabricSquare.of(new Identifier("minecraft:block/white_wool"))
							));
					
				}
				if (scarves_rightScarf==null) {
					scarves_rightScarf = new SimpleScarfAttachment();
				}
				
				Vec3d lookVec = Vec3d.fromPolar(getPitch(), getYaw());
				Vec3d planarLookVec = Vec3d.fromPolar(0, getYaw());
				Vec3d upVec = new Vec3d(0, 1, 0);
				Vec3d rightVec = planarLookVec.crossProduct(upVec);
				
				
				double posedEyeHeight = this.getEyeHeight(getPose()) - 0.4;
				Vec3d referencePos = new Vec3d(0, posedEyeHeight, 0);
				
				//TODO: This is not working at *all*.
				//if (isFallFlying() || isInSwimmingPose()) {
				//	ScarvesMod.LOGGER.info("Translating for pose "+this.getPose()+" by "+planarLookVec);
				//	referencePos = referencePos.rotateX((float) (0.25*Math.PI));
				//	//TODO: Translate forward
				//}
				
				//TODO: Attach to shoulders
				scarves_leftScarf.setLocation(this.getLerpedPos(delta).add(referencePos).add(rightVec.multiply(-SCARF_TAIL_SEPARATION)));
				scarves_rightScarf.setLocation(this.getLerpedPos(delta).add(referencePos).add(rightVec.multiply(SCARF_TAIL_SEPARATION)));
				
				return Stream.of(scarves_leftScarf, scarves_rightScarf);
			}
			
			return Stream.empty();
		} else {
			return Stream.empty();
		}
	}
}
