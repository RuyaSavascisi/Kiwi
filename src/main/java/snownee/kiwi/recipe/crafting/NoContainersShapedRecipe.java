package snownee.kiwi.recipe.crafting;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapedRecipePattern;
import snownee.kiwi.data.DataModule;

public class NoContainersShapedRecipe extends ShapedRecipe {
	private final boolean noContainers;

	public NoContainersShapedRecipe(
			String group,
			CraftingBookCategory category,
			ShapedRecipePattern pattern,
			ItemStack result,
			boolean showNotification,
			boolean noContainers) {
		super(group, category, pattern, result, showNotification);
		this.noContainers = noContainers;
	}

	@Override
	public NonNullList<ItemStack> getRemainingItems(CraftingContainer inv) {
		return NonNullList.withSize(inv.getContainerSize(), ItemStack.EMPTY);
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return DataModule.SHAPED_NO_CONTAINERS.get();
	}

	public boolean noContainers() {
		return noContainers;
	}

	public static class Serializer implements RecipeSerializer<NoContainersShapedRecipe> {
		public static final Codec<NoContainersShapedRecipe> CODEC = RecordCodecBuilder.create(instance -> instance.group(
				ExtraCodecs.strictOptionalField(Codec.STRING, "group", "").forGetter(ShapedRecipe::getGroup),
				CraftingBookCategory.CODEC.fieldOf("category").orElse(CraftingBookCategory.MISC).forGetter(ShapedRecipe::category),
				ShapedRecipePattern.MAP_CODEC.forGetter(shapedRecipe -> shapedRecipe.pattern),
				ItemStack.CODEC.fieldOf("result").forGetter(shapedRecipe -> shapedRecipe.result),
				ExtraCodecs.strictOptionalField(Codec.BOOL, "show_notification", true).forGetter(ShapedRecipe::showNotification),
				Codec.BOOL.fieldOf("no_containers").orElse(false).forGetter(NoContainersShapedRecipe::noContainers)
		).apply(instance, NoContainersShapedRecipe::new));
		public static final StreamCodec<RegistryFriendlyByteBuf, NoContainersShapedRecipe> STREAM_CODEC = StreamCodec.of(
				Serializer::toNetwork,
				Serializer::fromNetwork);

		@Override
		public Codec<NoContainersShapedRecipe> codec() {
			return CODEC;
		}

		@Override
		public StreamCodec<RegistryFriendlyByteBuf, NoContainersShapedRecipe> streamCodec() {
			return STREAM_CODEC;
		}

		public static NoContainersShapedRecipe fromNetwork(RegistryFriendlyByteBuf buffer) {
			ShapedRecipe recipe = RecipeSerializer.SHAPED_RECIPE.streamCodec().decode(buffer);
			boolean noContainers = buffer.readBoolean();
			return new NoContainersShapedRecipe(
					recipe.getGroup(),
					recipe.category(),
					recipe.pattern,
					recipe.result,
					recipe.showNotification(),
					noContainers);
		}

		public static void toNetwork(RegistryFriendlyByteBuf buffer, NoContainersShapedRecipe recipe) {
			RecipeSerializer.SHAPED_RECIPE.streamCodec().encode(buffer, recipe);
			buffer.writeBoolean(recipe.noContainers);
		}
	}
}
