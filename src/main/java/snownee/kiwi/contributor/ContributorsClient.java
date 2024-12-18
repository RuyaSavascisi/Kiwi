package snownee.kiwi.contributor;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.InputConstants.Key;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import snownee.kiwi.KiwiClientConfig;
import snownee.kiwi.contributor.client.CosmeticLayer;
import snownee.kiwi.contributor.client.gui.CosmeticScreen;

@OnlyIn(Dist.CLIENT)
public class ContributorsClient {

	@SubscribeEvent
	public static void onClientPlayerLoggedIn(ClientPlayerNetworkEvent.LoggingIn event) {
		Contributors.changeCosmetic();
	}

	@SubscribeEvent
	public static void onClientPlayerLoggedOut(ClientPlayerNetworkEvent.LoggingOut event) {
		Contributors.PLAYER_COSMETICS.clear();
		CosmeticLayer.ALL_LAYERS.forEach(l -> l.getCache().invalidateAll());
	}

	public static void addLayers(EntityRenderersEvent.AddLayers event) {
		for (String name : event.getSkins()) {
			LivingEntityRenderer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> o = event.getSkin(name);
			CosmeticLayer layer = new CosmeticLayer(o);
			CosmeticLayer.ALL_LAYERS.add(layer);
			o.addLayer(layer);
		}
	}

	private static int hold;

	@SubscribeEvent
	public static void onKeyInput(InputEvent.Key event) {
		Minecraft mc = Minecraft.getInstance();
		if (!KiwiClientConfig.cosmeticScreenKeybind || mc.screen != null || mc.player == null || !mc.isWindowActive()) {
			return;
		}
		if (event.getModifiers() != 0) {
			return;
		}
		Key input = InputConstants.getKey(event.getKey(), event.getScanCode());
		if (input.getValue() != 75) {
			return;
		}
		if (event.getAction() != 2) {
			hold = 0;
		} else if (++hold == 30) {
			CosmeticScreen screen = new CosmeticScreen();
			mc.setScreen(screen);
		}
	}

}
