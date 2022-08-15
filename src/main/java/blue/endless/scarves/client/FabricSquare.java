package blue.endless.scarves.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.util.Identifier;

public record FabricSquare(float uMin, float vMin, float uMax, float vMax, int color) {
	
	/**
	 * Creates a fabric square of the specified block/item on the block atlas texture, and uses the middle 8x8
	 * @param id a texture id, such as "block/white_wool"
	 * @return a fabric square representing the middle 8x8 with no tint.
	 */
	public static FabricSquare of(Identifier id) {
		return FabricSquare.of(id, 4, 4, 0xFF_FFFFFF);
	}
	
	public static FabricSquare of(Identifier id, int color) {
		return FabricSquare.of(id, 4, 4, color);
	}
	
	public static FabricSquare of(Identifier id, int xofs, int yofs, int color) {
		AbstractTexture tex = MinecraftClient.getInstance().getTextureManager().getTexture(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE);
		if (tex instanceof SpriteAtlasTexture atlas) {
			Sprite sprite = atlas.getSprite(id);
			float uPx = (sprite.getMaxU() - sprite.getMinU()) / 16f;
			float vPx = (sprite.getMaxV() - sprite.getMinV()) / 16f;
			
			float uofs = uPx * xofs;
			float vofs = vPx * yofs;
			
			float minU = sprite.getMinU() + uofs;
			float minV = sprite.getMinV() + vofs;
			float maxU = minU + (uPx * 8);
			float maxV = minV + (vPx * 8);
			
			return new FabricSquare(minU, minV, maxU, maxV, color);
		} else {
			throw new IllegalStateException("Somehow the Block Atlas texture is not an atlas (found "+tex.getClass().getCanonicalName()+")");
		}
	}
}