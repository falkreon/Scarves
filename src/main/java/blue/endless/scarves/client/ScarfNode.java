package blue.endless.scarves.client;

import blue.endless.scarves.api.FabricSquare;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class ScarfNode {
	/** Width of each fabric square in-world. */
	public static final float FABRIC_SQUARE_WIDTH = 0.3f; // == 8 pixels / 16 pxPerMeter
	/** Maximum d^2 allowed between fabric squares; the squared width of a square. */
	public static final float SQUARED_WIDTH = FABRIC_SQUARE_WIDTH * FABRIC_SQUARE_WIDTH;
	protected Vec3d position;
	protected Vec3d lastPosition;
	//protected Vec3d velocity;
	protected FabricSquare square;
	
	public ScarfNode(Vec3d pos, FabricSquare square) {
		this.position = pos;
		this.lastPosition = pos;
		this.square = square;
	};
	
	public void pullTowards(Vec3d point) {
		Vec3d backToMe = position.subtract(point);
		if (backToMe.lengthSquared() > SQUARED_WIDTH) {
			Vec3d newPosition = backToMe.normalize().multiply(FABRIC_SQUARE_WIDTH).add(point);
			position = newPosition;
		}
	}

	public Vec3d getPosition() {
		return position;
	}
	
	public Vec3d getLastPosition() {
		return lastPosition;
	}
	
	public void setPosition(Vec3d pos) {
		position = pos;
	}
	
	public void setLastPosition(Vec3d pos) {
		lastPosition = pos;
	}
	
	public Vec3d getLerpedPosition(double deltaTime) {
		return new Vec3d(
				MathHelper.lerp(deltaTime, lastPosition.x, position.x),
				MathHelper.lerp(deltaTime, lastPosition.y, position.y),
				MathHelper.lerp(deltaTime, lastPosition.z, position.z)
				);
	}
	
	public void setSquare(FabricSquare square) {
		this.square = square;
	}
}
