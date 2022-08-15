package blue.endless.scarves.client;

import java.util.List;

import net.minecraft.util.math.Vec3d;

public interface ScarfAttachment {
	public Vec3d getLocation();
	public List<ScarfNode> nodes();
}
