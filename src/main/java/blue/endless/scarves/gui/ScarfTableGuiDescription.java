package blue.endless.scarves.gui;

import java.util.Map;

import org.jetbrains.annotations.Nullable;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import blue.endless.scarves.ScarfStaplerBlockEntity;
import blue.endless.scarves.ScarfTableBlockEntity;
import blue.endless.scarves.ScarvesBlocks;
import blue.endless.scarves.ScarvesItems;
import blue.endless.scarves.ScarvesMod;
import blue.endless.scarves.WScarfPreview;
import blue.endless.scarves.api.FabricSquare;
import blue.endless.scarves.api.FabricSquareRegistry;
import blue.endless.scarves.ghost.GhostInventory;
import blue.endless.scarves.ghost.GhostInventoryHolder;
import blue.endless.scarves.ghost.WGhostSlot;
import dev.emi.trinkets.api.TrinketInventory;
import dev.emi.trinkets.api.TrinketsApi;
import io.github.cottonmc.cotton.gui.SyncedGuiDescription;
import io.github.cottonmc.cotton.gui.networking.NetworkSide;
import io.github.cottonmc.cotton.gui.networking.ScreenNetworking;
import io.github.cottonmc.cotton.gui.widget.WGridPanel;
import io.github.cottonmc.cotton.gui.widget.WPlainPanel;
import io.github.cottonmc.cotton.gui.widget.WItemSlot;
import io.github.cottonmc.cotton.gui.widget.WButton;
import io.github.cottonmc.cotton.gui.widget.WLabeledSlider;
import io.github.cottonmc.cotton.gui.widget.data.Axis;
import io.github.cottonmc.cotton.gui.widget.data.Insets;
import io.github.cottonmc.cotton.gui.widget.icon.TextureIcon;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.Codecs;

public class ScarfTableGuiDescription extends SyncedGuiDescription implements GhostInventoryHolder {
	@Nullable
	private ScarfTableBlockEntity blockEntity = null;
	private GhostInventory ghostInventory = GhostInventory.ofSize(8);
	private WPlainPanel bobbinPanel = new WPlainPanel();
	private WLabeledSlider patternSizeSlider = new WLabeledSlider(1, 8, Axis.HORIZONTAL, Text.translatable("gui.scarves.pattern_length", 8));
	private WLabeledSlider repetitionsSlider = new WLabeledSlider(1, 24, Axis.HORIZONTAL, Text.translatable("gui.scarves.repetitions", 6));
	private WButton applyLeftButton = new WButton(Text.translatable("gui.scarves.apply"));
	
	public ScarfTableGuiDescription(int syncId, PlayerInventory playerInventory, ScreenHandlerContext context) {
		super(ScarvesBlocks.SCARF_TABLE_SCREEN_HANDLER, syncId, playerInventory, getBlockInventory(context, 10), null);
		
		context.run((world, pos) -> {
			world.getBlockEntity(pos, ScarvesBlocks.SCARF_TABLE_ENTITY).ifPresent(it -> {
				blockEntity = it;
				ghostInventory = it;
			});
		});
		
		ScreenNetworking.of(this, NetworkSide.SERVER).<ApplyMessage>receive(ApplyMessage.ID, ApplyMessage.CODEC, this::apply);
		
		WGridPanel root = new WGridPanel();
		setRootPanel(root);

		root.setSize(212, 256);
		root.setInsets(Insets.ROOT_PANEL);
		
		bobbinPanel.add(new WScarfPreview(ghostInventory, Axis.HORIZONTAL), 1, 0, 9, 2);
		
		for(int i=0; i<8; i++) {
			WGhostSlot ghostSlot = new WGhostSlot(ghostInventory, i);
			ghostSlot.setFilter((it) -> it.get(FabricSquare.COMPONENT) != null);
			bobbinPanel.add(ghostSlot, 6 + (i * 24), 25);
		}
		
		root.add(bobbinPanel, 0, 1);
		
		patternSizeSlider.setLabelUpdater(it -> Text.translatable("gui.scarves.pattern_length", it));
		patternSizeSlider.setValue(8, false);
		root.add(patternSizeSlider, 1, 4, 9, 1);
		repetitionsSlider.setLabelUpdater(it -> Text.translatable("gui.scarves.repetitions", it));
		repetitionsSlider.setValue(6, false);
		root.add(repetitionsSlider, 1, 5, 9, 1);
		
		WItemSlot scarfSlot = WItemSlot.of(blockInventory, ScarfStaplerBlockEntity.SCARF_SLOT);
		scarfSlot.setInputFilter(it -> it.isOf(ScarvesItems.SCARF));
		scarfSlot.setIcon(new TextureIcon(ScarfStaplerGuiDescription.SCARF_SLOT_ICON));
		root.add(scarfSlot, 5, 7);
		
		applyLeftButton.setOnClick(() -> {
			ScreenNetworking.of(this, NetworkSide.CLIENT).send(ApplyMessage.ID, ApplyMessage.CODEC, new ApplyMessage(patternSizeSlider.getValue(), repetitionsSlider.getValue()));
		});
		
		root.add(applyLeftButton, 1, 7, 4, 1);
		
		Map<String, Map<String, TrinketInventory>> inventoryMap = TrinketsApi.getTrinketComponent(playerInventory.player).get().getInventory();
		Map<String, TrinketInventory> chestGroup = inventoryMap.get("chest");
		if (chestGroup!=null) {
			TrinketInventory scarfInventory = chestGroup.get("scarf");
			if (scarfInventory!=null && scarfInventory.size()>0) {
				WItemSlot playerScarfSlot = WItemSlot.of(scarfInventory, 0);
				playerScarfSlot.setInputFilter(it -> it.isOf(ScarvesItems.SCARF));
				playerScarfSlot.setIcon(new TextureIcon(ScarfStaplerGuiDescription.SCARF_SLOT_ICON));
				root.add(playerScarfSlot, 0, 7);
			}
		}
		
		root.add(this.createPlayerInventoryPanel(), 1, 9);

		root.validate(this);
	}
	
	public void apply(ApplyMessage message) {
		if (blockEntity == null) return;
		blockEntity.applyPattern(message.patternSize(), message.repeatCount());
	}
	
	@Override
	public GhostInventory getGhostInventory() {
		return ghostInventory;
	}
	
	public static record ApplyMessage(int patternSize, int repeatCount) {
		public static final Identifier ID = Identifier.of(ScarvesMod.MODID, "apply");
		public static final PacketCodec<PacketByteBuf, ApplyMessage> PACKET_CODEC = PacketCodec.of(ApplyMessage::write, ApplyMessage::new);
		public static final Codec<ApplyMessage> CODEC = RecordCodecBuilder.create(
				(instance) -> {
					return instance.group(
							Codecs.NONNEGATIVE_INT.fieldOf("PatternSize").forGetter(ApplyMessage::patternSize),
							Codecs.NONNEGATIVE_INT.fieldOf("RepeatCount").forGetter(ApplyMessage::repeatCount)
					).apply(instance, ApplyMessage::new);
				});
		
		public ApplyMessage(PacketByteBuf buf) {
			this(buf.readVarInt(), buf.readVarInt());
		}
		
		public void write(PacketByteBuf buf) {
			buf.writeVarInt(patternSize);
			buf.writeVarInt(repeatCount);
		}
	}
}
