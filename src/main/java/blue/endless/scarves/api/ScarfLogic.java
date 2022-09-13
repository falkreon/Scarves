package blue.endless.scarves.api;

import java.util.List;

import blue.endless.scarves.ScarvesApiImpl;
import blue.endless.scarves.client.ScarfAttachment;
import blue.endless.scarves.client.ScarfNode;
import blue.endless.scarves.client.ScarvesClient;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class ScarfLogic {
	public static double maxWindMagnitude = 0.5;
	
	/**
	 * Updates the ScarfAttachment with new scarf data and applies gravity. Should be called each tick by an IScarfHaver.
	 */
	public static void updateScarfAttachment(ScarfAttachment attachment, World world, Entity entity, Vec3d anchorPosition, NbtList data) {
		List<ScarfNode> nodes = attachment.nodes();
		while(nodes.size()>data.size()) nodes.remove(nodes.size()-1);
		
		Vec3d lastPos = anchorPosition;
		for(int i=0; i<data.size(); i++) {
			FabricSquare square = FabricSquare.fromCompound(data.getCompound(i));
			if (nodes.size()<=i) {
				ScarfNode node = new ScarfNode(lastPos, square);
				nodes.add(node);
			} else {
				nodes.get(i).setSquare(square);
				lastPos = nodes.get(i).getPosition();
			}
			
			ScarfNode node = nodes.get(i);
			node.setLastPosition(node.getPosition());
			Vec3d prospectivePosition = node.getPosition().add(0, ScarvesClient.SCARF_GRAVITY, 0);
			BlockPos blockInThatPosition = new BlockPos(prospectivePosition.add(0,-ScarfNode.FABRIC_SQUARE_WIDTH,0));
			if (world!=null) {
				if (!world.isTopSolid(blockInThatPosition, entity)) {
					node.setPosition(prospectivePosition);
				}
			}
			
			Vec3d wind = ScarvesApiImpl.getInstance().getWind(world, node.getPosition());
			//cap wind
			if (wind.lengthSquared()>maxWindMagnitude*maxWindMagnitude) {
				wind = wind.normalize().multiply(maxWindMagnitude);
			}
			
			//move it
			prospectivePosition = node.getPosition().add(wind);
			blockInThatPosition = new BlockPos(prospectivePosition);
			if (world!=null) {
				if (!world.isTopSolid(blockInThatPosition, entity)) {
					node.setPosition(prospectivePosition);
				}
			}
		}
	}
	
	/**
	 * Only updates gravity on the scarf. Should be called each tick by an IScarfHaver if their scarf never changes color, length, or patterns based on item data.
	 */
	public static void updateGravityOnly(ScarfAttachment attachment, World world, Entity entity) {
		List<ScarfNode> nodes = attachment.nodes();
		
		for(int i=0; i<nodes.size(); i++) {
			ScarfNode node = nodes.get(i);
			Vec3d prospectivePosition = node.getPosition().add(0, ScarvesClient.SCARF_GRAVITY, 0);
			BlockPos blockInThatPosition = new BlockPos(prospectivePosition);
			if (world!=null) {
				if (!world.isTopSolid(blockInThatPosition, entity)) {
					node.setPosition(prospectivePosition);
				}
			}
			
			Vec3d wind = ScarvesApiImpl.getInstance().getWind(world, node.getPosition());
			//cap wind
			if (wind.lengthSquared()>maxWindMagnitude*maxWindMagnitude) {
				wind = wind.normalize().multiply(maxWindMagnitude);
			}
			
			//move it
			prospectivePosition = node.getPosition().add(wind);
			blockInThatPosition = new BlockPos(prospectivePosition);
			if (world!=null) {
				if (!world.isTopSolid(blockInThatPosition, entity)) {
					node.setPosition(prospectivePosition);
				}
			}
		}
	}
}
