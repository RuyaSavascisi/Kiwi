package snownee.kiwi;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import org.apache.commons.lang3.ClassUtils;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Maps;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

public class RegistryLookup {

	public final Map<Class<?>, ResourceKey<? extends Registry<?>>> registries = Maps.newConcurrentMap();
	public final Cache<Class<?>, Optional<ResourceKey<? extends Registry<?>>>> cache = CacheBuilder.newBuilder().build();

	public ResourceKey<? extends Registry<?>> findRegistry(Object o) {
		try {
			return cache.get(o.getClass(), () -> {
				{
					Class<?> clazz = o.getClass();
					while (clazz != Object.class) {
						ResourceKey<? extends Registry<?>> registry = registries.get(clazz);
						if (registry != null) {
							return Optional.of(registry);
						}
						clazz = clazz.getSuperclass();
					}
				}
				for (Class<?> clazz : ClassUtils.getAllInterfaces(o.getClass())) {
					ResourceKey<? extends Registry<?>> registry = registries.get(clazz);
					if (registry != null) {
						return Optional.of(registry);
					}
				}
				return Optional.empty();
			}).orElse(null);
		} catch (ExecutionException e) {
			return null;
		}
	}

}
