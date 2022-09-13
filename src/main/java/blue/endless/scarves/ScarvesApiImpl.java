package blue.endless.scarves;

import java.util.ArrayList;

import blue.endless.scarves.api.FabricSquare;
import blue.endless.scarves.api.FabricSquareRegistry;
import blue.endless.scarves.api.ScarvesApi;
import blue.endless.scarves.api.WindVectorProvider;
import net.minecraft.block.Block;
import net.minecraft.item.ItemConvertible;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class ScarvesApiImpl implements ScarvesApi {
	private static ScarvesApiImpl INSTANCE = new ScarvesApiImpl();
	
	private ArrayList<WindVectorProvider> windProviders = new ArrayList<>();
	
	public static ScarvesApiImpl getInstance() {
		return INSTANCE;
	}

	@Override
	public void register(Block block, String textureId) {
		FabricSquareRegistry.register(block, textureId);
	}

	@Override
	public void register(ItemConvertible item, FabricSquare square) {
		FabricSquareRegistry.register(item, square);
	}

	@Override
	public void provideWind(WindVectorProvider wind) {
		windProviders.add(wind);
	}
	
	@Override
	public Vec3d getWind(World world, Vec3d pos) {
		Vec3d result = new Vec3d(0,0,0);
		
		for(WindVectorProvider provider : windProviders) {
			result = result.add(provider.apply(world, pos));
		}
		
		return result;
	}
}
