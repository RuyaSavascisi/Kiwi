package snownee.kiwi.recipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import snownee.kiwi.util.NotNullByDefault;

@NotNullByDefault
public class EmptyRecipeInput implements RecipeInput {

	@Override
	public ItemStack getItem(int index) {
		return ItemStack.EMPTY;
	}

	@Override
	public int size() {
		return 0;
	}
}
