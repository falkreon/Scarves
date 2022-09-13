package blue.endless.scarves.api;

import java.util.function.BiFunction;

import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

@FunctionalInterface
public interface WindVectorProvider extends BiFunction<World, Vec3d, Vec3d> {
}
