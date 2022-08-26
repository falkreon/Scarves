package blue.endless.scarves;

import blue.endless.scarves.api.FabricSquare;
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
		ItemStack toStapleLeft = this.removeStack(LEFT_SLOT, 1);
		ItemStack toStapleRight = this.removeStack(RIGHT_SLOT, 1);
		ItemStack scarf = this.getStack(SCARF_SLOT);
		
		if (scarf.isEmpty()) return;
		if (toStapleLeft.isEmpty() && toStapleRight.isEmpty()) return;
		
		NbtCompound tag = scarf.getOrCreateNbt();
		
		if (toStapleLeft!=ItemStack.EMPTY) {
			FabricSquare toAdd = FabricSquareRegistry.forItem(toStapleLeft);
			NbtList squares = tag.getList("LeftScarf", NbtElement.COMPOUND_TYPE);
			squares.add(toAdd.toCompound());
			tag.put("LeftScarf", squares);
		}
		
		if (toStapleRight!=ItemStack.EMPTY) {
			FabricSquare toAdd = FabricSquareRegistry.forItem(toStapleRight);
			NbtList squares = tag.getList("RightScarf", NbtElement.COMPOUND_TYPE);
			squares.add(toAdd.toCompound());
			tag.put("RightScarf", squares);
		}
		
		this.setStack(SCARF_SLOT, scarf);
	}
}
