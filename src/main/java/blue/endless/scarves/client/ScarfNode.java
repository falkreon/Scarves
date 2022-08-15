package blue.endless.scarves.client;

import net.minecraft.util.math.Vec3d;

public class ScarfNode {
	/** Width of each fabric square in-world. */
	public static final float FABRIC_SQUARE_WIDTH = 0.5f; // == 8 pixels / 16 pxPerMeter
	/** Maximum d^2 allowed between fabric squares; the squared width of a square. */
	public static final float SQUARED_WIDTH = FABRIC_SQUARE_WIDTH * FABRIC_SQUARE_WIDTH;
	protected Vec3d position;
	protected Vec3d velocity;
	protected FabricSquare square;
	
	public ScarfNode(Vec3d pos, FabricSquare square) {
		this.position = pos;
		this.square = square;
	};
	
	public void pullTowards(Vec3d point) {
		Vec3d backToMe = position.subtract(point);
		if (backToMe.lengthSquared() > SQUARED_WIDTH) {
			Vec3d newPosition = backToMe.normalize().multiply(FABRIC_SQUARE_WIDTH).add(point);
			position = newPosition;
		}
	}
}
