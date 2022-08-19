package blue.endless.scarves.api;

//import org.spongepowered.include.com.google.common.base.Preconditions;

import net.minecraft.util.Identifier;

public record FabricSquare(Identifier id, int xofs, int yofs, int color, boolean emissive) {
	
	public FabricSquare(String id) {
		this(new Identifier(id), 4, 4, 0xFF_FFFFFF, false);
	}
	
	/**
	 * Creates a fabric square of the specified block/item on the block atlas texture, and uses the middle 8x8
	 * @param id a texture id, such as "block/white_wool"
	 * @return a fabric square representing the middle 8x8 with no tint.
	 */
	public FabricSquare(Identifier id) {
		this(id, 4, 4, 0xFF_FFFFFF, false);
	}
	
	public FabricSquare(Identifier id, int color) {
		this(id, 4, 4, color, false);
	}
	
	public FabricSquare fullbright() {
		return new FabricSquare(id, xofs, yofs, color, true);
	}
	
	public FabricSquare withColor(int color) {
		return new FabricSquare(id, xofs, yofs, color, emissive);
	}
	
	public FabricSquare withOffset(int xofs, int yofs) {
		//Preconditions.checkArgument(xofs>=0 && yofs>=0, "Negative offsets are not allowed, because they will spill over onto other textures.");
		//Preconditions.checkArgument(xofs<8 && yofs<8, "Offsets of more than 7 are not allowed, because they will spill over onto other textures.");
		return new FabricSquare(id, xofs, yofs, color, emissive);
	}
	
	/*
	public static FabricSquare of(Identifier id, int xofs, int yofs, int color, boolean emissive) {
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
			
			return new FabricSquare(minU, minV, maxU, maxV, color, emissive);
		} else {
			throw new IllegalStateException("Somehow the Block Atlas texture is not an atlas (found "+tex.getClass().getCanonicalName()+")");
		}
	}*/
}