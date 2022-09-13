package blue.endless.scarves.api;

import blue.endless.scarves.ScarvesApiImpl;
import net.minecraft.block.Block;
import net.minecraft.item.ItemConvertible;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public interface ScarvesApi {
	
	public static ScarvesApi instance() {
		return ScarvesApiImpl.getInstance();
	}
	
	//API
	
	/**
	 * Registers a block and allows it to be used in the left or right slots of the Scarf Stapler. The center of the
	 * texture will be used for the appearance of the square.
	 * 
	 * <p>Notes: Animated textures, and textures that have been synthetically stitched into the texture atlas used for
	 * blocks and items, will all work seamlessly. Glowing blocks will automatically be rendered emissively.
	 * Substituting a different texture from the atlas, a vanilla texture, a texture from a namespace you do not own,
	 * these things are ALL ALLOWED. Cut out the middle of the diamond pickaxe texture and put it on a square, I'm not
	 * your dad. Basically, if you can figure out its name, it's fair game. Putting any invalid identifier, including
	 * "", will result in the pink checkers we all know and love.
	 * 
	 * @param block     The block to be accepted as a Fabric Square by the Scarf Stapler in its left or right slot.
	 * @param textureId The resulting fabric square's appearance. For example, if you use cube_all, the same identifier
	 *                  string that you use for the "all" texture parameter in the block's model will work here.
	 */
	void register(Block block, String textureId);
	
	/**
	 * Registers a block, item, or other item-like object and allows it to be used in the left or right slots of the
	 * Scarf Stapler. The appearance of the square will be governed completely by the FabricSquare provided.
	 * 
	 * <p>See Also: {@link #register(Block, String)}
	 * 
	 * @param item   The item or item-like object to be accepted as a Fabric Square by the Scarf Stapler in its left or
	 *               right slot.
	 * @param square The appearance this item should have when stitched to the end of a scarf.
	 */
	public void register(ItemConvertible item, FabricSquare square);
	
	/**
	 * Registers a wind provider. These providers let Scarves know about wind that may vary based on location in the
	 * world. This allows scarves to move in unison with other wind-affected objects, or serve as indicators of strong
	 * winds for mods which provide windmills.
	 * @param wind A function which yields a Vec3d of wind direction/strength at the provided world location.
	 */
	public void provideWind(WindVectorProvider wind);
	
	public Vec3d getWind(World world, Vec3d pos);
}
