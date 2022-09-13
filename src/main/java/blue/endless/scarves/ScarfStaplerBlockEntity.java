package blue.endless.scarves;

import blue.endless.scarves.api.FabricSquareRegistry;
import blue.endless.scarves.gui.ScarfStaplerGuiDescription;
import blue.endless.scarves.util.ImplementedInventory;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
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
	public static final int RIGHT_SLOT = 2;
	private DefaultedList<ItemStack> inventory = DefaultedList.ofSize(3, ItemStack.EMPTY);
	
	public ScarfStaplerBlockEntity(BlockPos pos, BlockState state) {
		super(ScarvesBlocks.SCARF_STAPLER_ENTITY, pos, state);
	}
	
	@Override
	public DefaultedList<ItemStack> getItems() {
		return inventory;
	}
	
	@Override
	public void readNbt(NbtCompound nbt) {
		Inventories.readNbt(nbt, inventory);
		super.readNbt(nbt);
	}
	
	@Override
	protected void writeNbt(NbtCompound nbt) {
		Inventories.writeNbt(nbt, inventory);
		super.writeNbt(nbt);
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
	
	public void staple() {
		ItemStack leftSlot = this.getStack(LEFT_SLOT);
		ItemStack rightSlot = this.getStack(RIGHT_SLOT);
		if (leftSlot.isEmpty() && rightSlot.isEmpty()) return;
		
		ItemStack scarf = this.getStack(SCARF_SLOT);
		if (scarf.isEmpty()) return;
		
		//Check for capped lengths
		NbtCompound tag = scarf.getOrCreateNbt();
		NbtList leftSquares = tag.getList("LeftScarf", NbtElement.COMPOUND_TYPE);
		NbtList rightSquares = tag.getList("RightScarf", NbtElement.COMPOUND_TYPE);
		
		if (!leftSlot.isEmpty() && leftSquares.size()>=STAPLER_CAP) return;
		if (!rightSlot.isEmpty() && rightSquares.size()>=STAPLER_CAP) return;
		
		ItemStack toStapleLeft = this.removeStack(LEFT_SLOT, 1);
		ItemStack toStapleRight = this.removeStack(RIGHT_SLOT, 1);
		
		if (toStapleLeft!=ItemStack.EMPTY) {
			NbtList toAdd = FabricSquareRegistry.getStaplerData(toStapleLeft);
			for(NbtElement elem : toAdd) {
				if (leftSquares.size()>=512) break;
				if (elem instanceof NbtCompound compound) leftSquares.add(compound);
			}
			tag.put("LeftScarf", leftSquares);
		}
		
		if (toStapleRight!=ItemStack.EMPTY) {
			NbtList toAdd = FabricSquareRegistry.getStaplerData(toStapleRight);
			for(NbtElement elem : toAdd) {
				if (rightSquares.size()>=512) break;
				if (elem instanceof NbtCompound compound) rightSquares.add(compound);
			}
			tag.put("RightScarf", rightSquares);
		}
		
		this.setStack(SCARF_SLOT, scarf);
	}
}
