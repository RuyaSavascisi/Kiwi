package snownee.kiwi.contributor.client.gui;

import java.util.Objects;

import javax.annotation.Nullable;

import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import snownee.kiwi.KiwiClientConfig;
import snownee.kiwi.config.ConfigHandler;
import snownee.kiwi.config.KiwiConfigManager;
import snownee.kiwi.contributor.Contributors;

public class CosmeticScreen extends Screen {

	private List list;
	@Nullable
	private ResourceLocation currentCosmetic;
	private Entry selectedEntry;

	public CosmeticScreen() {
		super(Component.translatable("gui.kiwi.cosmetic"));
	}

	@Override
	protected void init() {
		currentCosmetic = Contributors.PLAYER_COSMETICS.get(getPlayerName());
		list = new List(minecraft, 150, height, 0, height, 20);
		list.setLeftPos(20);
		list.addEntry(selectedEntry = new Entry(this, null));
		String playerName = getPlayerName();
		boolean added = false;
		for (ResourceLocation tier : Contributors.getRenderableTiers()) {
			if (Contributors.isContributor(tier.getNamespace(), playerName, tier.getPath())) {
				Entry entry = new Entry(this, tier);
				list.addEntry(entry);
				added = true;
				if (tier.equals(currentCosmetic)) {
					selectedEntry = entry;
				}
			}
		}
		if (!added) {
			minecraft.setScreen(null);
		}
		addRenderableWidget(Button.builder(
				Component.translatable(KiwiClientConfig.cosmeticScreenKeybind ? "gui.kiwi.cosmetic.enabled" : "gui.kiwi.cosmetic.disabled"),
				b -> {
					KiwiClientConfig.cosmeticScreenKeybind = !KiwiClientConfig.cosmeticScreenKeybind;
					KiwiConfigManager.getHandler(KiwiClientConfig.class).save();
					b.setMessage(Component.translatable(KiwiClientConfig.cosmeticScreenKeybind ?
							"gui.kiwi.cosmetic.enabled" :
							"gui.kiwi.cosmetic.disabled"));
				}).pos(180, 30).build());
	}

	@Override
	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float pTicks) {
		renderBackground(guiGraphics);
		super.render(guiGraphics, mouseX, mouseY, pTicks);
		list.render(guiGraphics, mouseX, mouseY, pTicks);
		guiGraphics.drawString(minecraft.font, title, 180, 10, 0xFFFFFF);
	}

	@Override
	public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
		list.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_);
		return super.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_);
	}

	@Override
	public boolean mouseDragged(
			double p_mouseDragged_1_,
			double p_mouseDragged_3_,
			int p_mouseDragged_5_,
			double p_mouseDragged_6_,
			double p_mouseDragged_8_) {
		list.mouseDragged(p_mouseDragged_1_, p_mouseDragged_3_, p_mouseDragged_5_, p_mouseDragged_6_, p_mouseDragged_8_);
		return super.mouseDragged(p_mouseDragged_1_, p_mouseDragged_3_, p_mouseDragged_5_, p_mouseDragged_6_, p_mouseDragged_8_);
	}

	@Override
	public boolean mouseReleased(double p_mouseReleased_1_, double p_mouseReleased_3_, int p_mouseReleased_5_) {
		list.mouseReleased(p_mouseReleased_1_, p_mouseReleased_3_, p_mouseReleased_5_);
		return super.mouseReleased(p_mouseReleased_1_, p_mouseReleased_3_, p_mouseReleased_5_);
	}

	@Override
	public boolean mouseScrolled(double p_mouseScrolled_1_, double p_mouseScrolled_3_, double p_mouseScrolled_5_) {
		list.mouseScrolled(p_mouseScrolled_1_, p_mouseScrolled_3_, p_mouseScrolled_5_);
		return super.mouseScrolled(p_mouseScrolled_1_, p_mouseScrolled_3_, p_mouseScrolled_5_);
	}

	@Override
	public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
		list.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
		return super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
	}

	@Override
	public void onClose() {
		super.onClose();
		list = null;
		ConfigHandler cfg = KiwiConfigManager.getHandler(KiwiClientConfig.class);
		if (currentCosmetic != null && selectedEntry.id == null) {
			KiwiClientConfig.contributorCosmetic = "";
			cfg.save();
			Contributors.changeCosmetic();
		} else if (selectedEntry != null && !Objects.equals(selectedEntry.id, currentCosmetic)) {
			KiwiClientConfig.contributorCosmetic = selectedEntry.id.toString();
			cfg.save();
			Contributors.changeCosmetic();
		}
	}

	private static String getPlayerName() {
		return Minecraft.getInstance().getUser().getName();
	}

	private static class List extends ObjectSelectionList<Entry> {

		public List(Minecraft mcIn, int widthIn, int heightIn, int topIn, int bottomIn, int slotHeightIn) {
			super(mcIn, widthIn, heightIn, topIn, bottomIn, slotHeightIn);
		}

		@Override
		public int addEntry(snownee.kiwi.contributor.client.gui.CosmeticScreen.Entry p_93487_) {
			return super.addEntry(p_93487_);
		}

	}

	private static class Entry extends ObjectSelectionList.Entry<Entry> {

		private final CosmeticScreen parent;
		@Nullable
		private final ResourceLocation id;
		private final String name;

		public Entry(CosmeticScreen parent, ResourceLocation id) {
			this.parent = parent;
			this.id = id;
			name = id == null ? "-" : I18n.get(Util.makeDescriptionId("cosmetic", id));
		}

		@Override
		public void render(
				GuiGraphics guiGraphics,
				int entryIdx,
				int top,
				int left,
				int entryWidth,
				int entryHeight,
				int mouseX,
				int mouseY,
				boolean hover,
				float partialTicks) {
			int color = hover ? 0xFFFFAA : 0xFFFFFF;
			if (this == parent.selectedEntry) {
				color = 0xFFFF77;
			}
			guiGraphics.drawString(parent.font, name, left + 43, top + 2, color);
		}

		@Override
		public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
			parent.selectedEntry = this;
			return false;
		}

		@Override
		public Component getNarration() {
			return Component.literal(name);
		}

	}

}
