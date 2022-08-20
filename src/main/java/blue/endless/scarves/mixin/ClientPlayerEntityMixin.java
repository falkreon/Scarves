package blue.endless.scarves.mixin;

import java.util.List;
import java.util.stream.Stream;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.authlib.GameProfile;

import blue.endless.scarves.ScarvesItems;
import blue.endless.scarves.api.FabricSquare;
import blue.endless.scarves.api.ScarfLogic;
import blue.endless.scarves.client.IScarfHaver;
import blue.endless.scarves.client.ScarfAttachment;
import blue.endless.scarves.client.ScarfNode;
import blue.endless.scarves.client.ScarvesClient;
import blue.endless.scarves.client.SimpleScarfAttachment;
import dev.emi.trinkets.api.TrinketComponent;
import dev.emi.trinkets.api.TrinketsApi;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.encryption.PlayerPublicKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

@Mixin({ClientPlayerEntity.class, OtherClientPlayerEntity.class})
public abstract class ClientPlayerEntityMixin extends PlayerEntity implements IScarfHaver {

	public ClientPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile gameProfile,
			PlayerPublicKey publicKey) {
		super(world, pos, yaw, gameProfile, publicKey);
		// TODO Auto-generated constructor stub
	}

	private static final float SCARF_TAIL_SEPARATION = 0.19f;


	private SimpleScarfAttachment scarves_leftScarf;
	private SimpleScarfAttachment scarves_rightScarf;
	
	
	
	@Override
	public Stream<ScarfAttachment> iScarfHaver_getAttachments(float delta) {
		
		TrinketComponent component = TrinketsApi.getTrinketComponent((LivingEntity)(Object) this).orElse(null);
		if (component !=null) {
			for(var equipped : component.getEquipped(ScarvesItems.SCARF)) {
				
				if (scarves_leftScarf==null) {
					scarves_leftScarf = new SimpleScarfAttachment();
				}
				if (scarves_rightScarf==null) {
					scarves_rightScarf = new SimpleScarfAttachment();
				}
				
				//Vec3d lookVec = Vec3d.fromPolar(getPitch(), getYaw());
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
	
	@Inject(at = { @At("TAIL") }, method="tick()V")
	public void afterTick(CallbackInfo ci) {
		scarves_updateScarfAttachments();
	}
	
	private void scarves_updateScarfAttachments() {
		TrinketComponent component = TrinketsApi.getTrinketComponent((LivingEntity)(Object) this).orElse(null);
		if (component !=null) {
			for(var equipped : component.getEquipped(ScarvesItems.SCARF)) {
				ItemStack stack = equipped.getRight();
				
				NbtCompound tag = stack.getNbt();
				if (tag==null) {
					//This is an empty scarf
					if (scarves_leftScarf!=null) scarves_leftScarf.nodes().clear();
					if (scarves_rightScarf!=null) scarves_rightScarf.nodes().clear();
				} else {
					
					NbtList leftScarfTag = tag.getList("LeftScarf", NbtElement.COMPOUND_TYPE);
					if (leftScarfTag!=null) {
						if (scarves_leftScarf==null) scarves_leftScarf = new SimpleScarfAttachment();
						ScarfLogic.updateScarfAttachment(scarves_leftScarf, this.world, (Entity)(Object)this, this.getPos(), leftScarfTag);
					}
					
					NbtList rightScarfTag = tag.getList("RightScarf", NbtElement.COMPOUND_TYPE);
					if (rightScarfTag!=null) {
						if (scarves_rightScarf==null) scarves_rightScarf = new SimpleScarfAttachment();
						ScarfLogic.updateScarfAttachment(scarves_rightScarf, this.world, (Entity)(Object)this, this.getPos(), rightScarfTag);
					}
				}
			}
		}
	}
}
