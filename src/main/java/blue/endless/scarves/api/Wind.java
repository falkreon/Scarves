package blue.endless.scarves.api;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class Wind {
	private static List<WindVectorProvider> providers = new ArrayList<>();
	
	private static double BUILTIN_WAVE_AMPLITUDE = Math.PI / 12;
	private static double BUILTIN_WIND_STRENGTH = 0.05f;
	private static double BUILTIN_WAVE_SPEED = 0.8f;
	
	//static {
	//	providers.add(Wind::builtin);
	//}
	
	public static Vec3d getWind(World world, Vec3d vec) {
		Vec3d result = Vec3d.ZERO;
		for(WindVectorProvider provider : providers) {
			result = provider.apply(world, vec);
		}
		
		return result;
	}
	
	private static Vec3d builtin(World world, Vec3d pos) {
		//TODO: Create a gameRule key to turn wind on/off
		//if (world.getGameRules().getBoolean(...)) return Vec3d.ZERO;
		double waveYaw = Math.sin(world.getTime() * BUILTIN_WAVE_SPEED + (0.4 * pos.x) + (0.4 * pos.z)) * BUILTIN_WAVE_AMPLITUDE;
		Vec3d baseWind = new Vec3d(0.9, 0, 0.5).normalize().multiply(BUILTIN_WIND_STRENGTH);
		return baseWind.rotateY((float) waveYaw);
	}
}
