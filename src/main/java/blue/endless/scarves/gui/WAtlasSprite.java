package blue.endless.scarves.gui;

import io.github.cottonmc.cotton.gui.client.ScreenDrawing;
import io.github.cottonmc.cotton.gui.widget.WWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.texture.GuiAtlasManager;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.texture.atlas.AtlasSprite;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.util.Identifier;

public class WAtlasSprite extends WWidget {
	private Identifier atlas;
	private Identifier spriteId;
	
	public WAtlasSprite(Identifier atlas, Identifier spriteId) {
		this.atlas = atlas;
		this.spriteId = spriteId;
	}
	
	public WAtlasSprite(Identifier spriteId) {
		this(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, spriteId);
	}
	
	@Override
	public void paint(DrawContext context, int x, int y, int mouseX, int mouseY) {
		try {
			Sprite sprite = MinecraftClient.getInstance().getSpriteAtlas(atlas).apply(spriteId);
			ScreenDrawing.texturedRect(
					context,
					x, y, getWidth(), getHeight(),
					atlas,
					sprite.getMinU(), sprite.getMinV(), sprite.getMaxU(), sprite.getMaxV(),
					0xFF_FFFFFF);
		} catch (Throwable t) {
			ScreenDrawing.texturedRect(context, x, y, getWidth(), getHeight(), TextureManager.MISSING_IDENTIFIER, 0xFF_FFFFFF);
		}
		/*
		
		ScreenDrawing.texturedRect(context, x, y, getWidth(), getHeight(), texture, tint);*/
		
	}
}
