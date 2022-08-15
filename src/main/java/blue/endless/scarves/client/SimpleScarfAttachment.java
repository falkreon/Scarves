package blue.endless.scarves.client;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.util.math.Vec3d;

public class SimpleScarfAttachment implements ScarfAttachment {
	protected Vec3d location = new Vec3d(0,0,0);
	protected final ArrayList<ScarfNode> nodes = new ArrayList<>();
	
	@Override
	public Vec3d getLocation() {
		return location;
	}

	@Override
	public List<ScarfNode> nodes() {
		return nodes;
	}
	
	public void setLocation(double x, double y, double z) {
		location = new Vec3d(x,y,z);
	}
	
	public void setLocation(Vec3d v) {
		location = v;
	}
}
