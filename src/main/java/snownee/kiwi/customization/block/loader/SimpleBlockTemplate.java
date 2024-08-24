package snownee.kiwi.customization.block.loader;

import java.util.Optional;
import java.util.function.Function;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import snownee.kiwi.customization.block.BlockFundamentals;
import snownee.kiwi.util.resource.OneTimeLoader;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public final class SimpleBlockTemplate extends KBlockTemplate {
	private final String clazz;
	private Function<BlockBehaviour.Properties, Block> constructor;

	public SimpleBlockTemplate(Optional<BlockDefinitionProperties> properties, String clazz) {
		super(properties);
		this.clazz = clazz;
	}

	public static MapCodec<SimpleBlockTemplate> directCodec(BlockFundamentals.CodecCreationContext context) {
		return RecordCodecBuilder.mapCodec(instance -> instance.group(
				BlockDefinitionProperties.mapCodecField(context).forGetter(SimpleBlockTemplate::properties),
				Codec.STRING.optionalFieldOf("class", "").forGetter(SimpleBlockTemplate::clazz)
		).apply(instance, SimpleBlockTemplate::new));
	}

	@Override
	public KBlockTemplate.Type<?> type() {
		return KBlockTemplates.SIMPLE.getOrCreate();
	}

	@Override
	public void resolve(ResourceLocation key, OneTimeLoader.Context context) {
		if (clazz.isEmpty()) {
			constructor = BlockCodecs.SIMPLE_BLOCK_FACTORY;
			return;
		}
		try {
			Class<?> clazz = Class.forName(this.clazz);
			this.constructor = $ -> {
				try {
					return (Block) clazz.getConstructor(BlockBehaviour.Properties.class).newInstance($);
				} catch (Throwable e) {
					throw new RuntimeException(e);
				}
			};
		} catch (Throwable e) {
			throw new IllegalStateException(e);
		}
	}

	@Override
	public Block createBlock(ResourceLocation id, BlockBehaviour.Properties settings, JsonObject input) {
		return this.constructor.apply(settings);
	}

	public String clazz() {
		return clazz;
	}

	@Override
	public String toString() {
		return "SimpleBlockTemplate[" + "properties=" + properties + ", " + "clazz=" + clazz + ", " + "constructor=" + constructor + ']';
	}

}
