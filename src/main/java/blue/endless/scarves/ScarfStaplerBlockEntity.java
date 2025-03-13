package blue.endless.scarves;

import java.util.ArrayList;
import java.util.List;

import blue.endless.scarves.api.FabricSquare;
import blue.endless.scarves.api.RepeatType;
import blue.endless.scarves.api.ScarfDesign;
import blue.endless.scarves.gui.ScarfStaplerGuiDescription;
import blue.endless.scarves.util.ImplementedInventory;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper.WrapperLookup;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.text.Text;
import net.minecraft.util.Nameable;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;

public class ScarfStaplerBlockEntity extends BlockEntity implements ImplementedInventory, Nameable, NamedScreenHandlerFactory {
	public static final int STAPLER_CAP = 256;
	
	private Text customName;
	public static final int SCARF_SLOT = 0;
	public static final int LEFT_SLOT = 1;
	private DefaultedList<ItemStack> inventory = DefaultedList.ofSize(2, ItemStack.EMPTY);
	
	public ScarfStaplerBlockEntity(BlockPos pos, BlockState state) {
		super(ScarvesBlocks.SCARF_STAPLER_ENTITY, pos, state);
	}
	
	@Override
	public DefaultedList<ItemStack> getItems() {
		return inventory;
	}
	
	@Override
	public void readNbt(NbtCompound nbt, WrapperLookup registryLookup) {
		Inventories.readNbt(nbt, inventory, registryLookup);
		super.readNbt(nbt, registryLookup);
	}
	
	@Override
	protected void writeNbt(NbtCompound nbt, WrapperLookup registryLookup) {
		Inventories.writeNbt(nbt, inventory, registryLookup);
		super.writeNbt(nbt, registryLookup);
	}
	
	public void setCustomName(Text name) {
		this.customName = name;
	}
	
	@Override
	public Text getName() {
		return customName;
	}

	@Override
	public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
		return new ScarfStaplerGuiDescription(syncId, playerInventory, ScreenHandlerContext.create(world, pos));
	}
	
	@Override
	public Text getDisplayName() {
		return Text.translatable(getCachedState().getBlock().getTranslationKey());
	}
	
	private <T> List<T> immutableMerge(List<T> list, T t) {
		ArrayList<T> tmp = new ArrayList<>();
		tmp.addAll(list);
		tmp.add(t);
		return List.copyOf(tmp);
	}
	
	private <T> List<T> immutableMerge(List<T> a, List<T> b) {
		ArrayList<T> tmp = new ArrayList<>();
		tmp.addAll(a);
		tmp.addAll(b);
		return List.copyOf(tmp);
	}
	
	public boolean staple() {
		ItemStack leftSlot = this.getStack(LEFT_SLOT);
		if (leftSlot.isEmpty()) return false;
		
		ItemStack scarf = this.getStack(SCARF_SLOT);
		if (scarf.isEmpty()) return false;
		
		ScarfDesign component = scarf.get(ScarfDesign.COMPONENT);
		if (component == null) component = new ScarfDesign(RepeatType.FROM_START, 0, List.of());
		if (component.squares().size() * component.repeatCount() >= STAPLER_CAP) return false; // No room to staple more
		
		ItemStack toStaple = this.removeStack(LEFT_SLOT, 1);
		if (toStaple.isEmpty()) return false; // Shouldn't happen
		
		// If this is a scarf, try to preserve or reconcile the repeat pattern.
		// If this is a fabric square, use the repeat pattern already on the scarf.
		
		ScarfDesign toStapleDesign = toStaple.get(ScarfDesign.COMPONENT);
		if (toStapleDesign != null) {
			if (component.repeatCount() == 0 || component.squares().isEmpty()) {
				// Existing scarf is empty, and repeatType doesn't matter. Just set the new scarf design
				scarf.set(ScarfDesign.COMPONENT, toStapleDesign);
				this.setStack(SCARF_SLOT, scarf);
				return true;
			}
			
			if (component.squares().equals(toStapleDesign.squares())) {
				// We can just add their repeat counts together
				ScarfDesign newDesign = new ScarfDesign(
						component.repeatType(),
						component.repeatCount() + toStapleDesign.repeatCount(),
						component.squares()
						);
				scarf.set(ScarfDesign.COMPONENT, newDesign);
				this.setStack(SCARF_SLOT, scarf);
				return true;
			}
			
			if (component.repeatCount() > 1 || toStapleDesign.repeatCount() > 1) {
				// We can't represent X repeats of A and then Y repeats of B without unrolling the repeats. For now,
				// reject this case.
				inventory.get(LEFT_SLOT).increment(1);
				this.setStack(LEFT_SLOT, inventory.get(LEFT_SLOT));
				return false;
			}
			
			//It's a single-repeat pattern tacked onto a single-repeat pattern. Merge them into one long single-repeat!
			ScarfDesign newDesign = new ScarfDesign(
					component.repeatType(),
					1,
					immutableMerge(component.squares(), toStapleDesign.squares())
					);
			scarf.set(ScarfDesign.COMPONENT, newDesign);
			this.setStack(SCARF_SLOT, scarf);
			return true;
			
		} else {
			FabricSquare toStapleSquare = toStaple.get(FabricSquare.COMPONENT);
			if (toStapleSquare == null) return false; // neither a scarf nor a square
			
			// Tack this square onto the end of the existing scarf component
			ScarfDesign newDesign = new ScarfDesign(
					component.repeatType(),
					Math.max(1, component.repeatCount()),
					immutableMerge(component.squares(), toStapleSquare)
					);
			
			scarf.set(ScarfDesign.COMPONENT, newDesign);
			this.setStack(SCARF_SLOT, scarf);
			return true;
		}
	}
}
