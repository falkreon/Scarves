package blue.endless.scarves.integration;

import dev.emi.emi.api.EmiDragDropHandler;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiInfoRecipe;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import io.github.cottonmc.cotton.gui.widget.WWidget;
import net.minecraft.component.ComponentChanges;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import blue.endless.scarves.ScarvesItems;
import blue.endless.scarves.api.FabricSquareRegistry;
import blue.endless.scarves.client.ScarfTableScreen;
import blue.endless.scarves.ghost.GhostInventoryNetworking;
import blue.endless.scarves.ghost.WGhostSlot;

public class EmiIntegration implements EmiPlugin {
	private static final Identifier STAPLING_CATEGORY_ID = Identifier.of("scarves", "stapling");
	private static final EmiRecipeCategory STAPLING_RECIPES = new EmiRecipeCategory(
			STAPLING_CATEGORY_ID,
			EmiStack.of(ScarvesItems.SCARF_STAPLER, ComponentChanges.EMPTY, 1)
			);
	
	@Override
	public void register(EmiRegistry registry) {
		registry.addDragDropHandler(ScarfTableScreen.class, new GhostItemDragDropHandler());
		
		registry.addCategory(STAPLING_RECIPES);
		registry.addWorkstation(STAPLING_RECIPES, EmiIngredient.of(Ingredient.ofItems(ScarvesItems.SCARF_STAPLER)));
		
		for(Item item : FabricSquareRegistry.allRegistrations()) {
			EmiRecipe staplingRecipe = new StaplerRecipe(item);
			registry.addRecipe(staplingRecipe);
		}
		
		registry.addRecipe(new EmiInfoRecipe(
				List.of(EmiIngredient.of(Ingredient.ofItems(ScarvesItems.SCARF_TABLE))),
				List.of(Text.translatable("info.scarves.serger")),
				Identifier.of("scarves","/serger_info")
				));
	}
	
	public static class GhostItemDragDropHandler implements EmiDragDropHandler<ScarfTableScreen> {

		@Override
		public boolean dropStack(ScarfTableScreen screen, EmiIngredient stack, int x, int y) {
			
			ItemStack targetItem = convertEmiIngredient(stack);
			if (targetItem.isEmpty()) return false;
			
			int hitX = x - screen.getX();
			int hitY = y - screen.getY();
			
			WWidget widget = screen.getScreenHandler().getRootPanel().hit(hitX, hitY);
			if (widget instanceof WGhostSlot ghostSlot) {
				if (!ghostSlot.getFilter().test(targetItem)) {
					System.out.println(targetItem.getComponentChanges());
					return false;
				}
				int index = ghostSlot.getIndex();
				
				screen.getScreenHandler().getGhostInventory().setGhostItem(index, targetItem);
				GhostInventoryNetworking.sendGhostItemToServer(index, targetItem);
				
				return true;
			}
			
			return false;
		}
		
	}
	
	public static ItemStack convertEmiIngredient(EmiIngredient ingredient) {
		if (ingredient.isEmpty()) return ItemStack.EMPTY;
		
		List<EmiStack> stacks = ingredient.getEmiStacks();
		if (stacks.isEmpty()) return ItemStack.EMPTY;
		
		EmiStack stack = stacks.get(0);
		return stack.getItemStack();
	}
	
	private static class StaplerRecipe implements EmiRecipe {
		private static final EmiIngredient SCARF_INGREDIENT = EmiIngredient.of(Ingredient.ofItems(ScarvesItems.SCARF));
		private static final EmiStack scarfStack = EmiStack.of(ScarvesItems.SCARF);
		private final Identifier itemId;
		//private final ItemEmiStack stack;
		private final EmiIngredient ingredient;
		
		public StaplerRecipe(ItemConvertible item) {
			itemId = Registries.ITEM.getId(item.asItem());
			//stack = new ItemEmiStack(new ItemStack(item));
			ingredient = EmiIngredient.of(Ingredient.ofItems(item));
		}
		
		@Override
		public EmiRecipeCategory getCategory() {
			return STAPLING_RECIPES;
		}

		@Override
		public @Nullable Identifier getId() {
			return Identifier.of("scarves", "/stapling."+itemId.getNamespace()+"."+itemId.getPath().replace('/', '.'));
		}

		@Override
		public List<EmiIngredient> getInputs() {
			return List.of(SCARF_INGREDIENT, ingredient);
		}

		@Override
		public List<EmiStack> getOutputs() {
			return List.of(scarfStack);
		}

		@Override
		public int getDisplayWidth() {
			return 144;
		}

		@Override
		public int getDisplayHeight() {
			return 18;
		}

		@Override
		public void addWidgets(WidgetHolder widgets) {
			widgets.addSlot(ingredient, 18, 0);
			widgets.addTexture(EmiTexture.EMPTY_ARROW, getDisplayWidth() / 2 - (EmiTexture.EMPTY_ARROW.regionWidth / 2), 1);
			widgets.addSlot(EmiStack.of(ScarvesItems.SCARF), getDisplayWidth() - 18 - 18, 0).recipeContext(this);
		}
		
	}
}
