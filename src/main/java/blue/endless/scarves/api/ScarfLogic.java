package blue.endless.scarves.api;

import java.util.List;

import blue.endless.scarves.client.ScarfAttachment;
import blue.endless.scarves.client.ScarfNode;
import blue.endless.scarves.client.ScarvesClient;
import blue.endless.scarves.client.SimpleScarfAttachment;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class ScarfLogic {
	public static double maxWindMagnitude = 0.5;
	
	/**
	 * Updates the ScarfAttachment with new scarf data and applies gravity. Should be called each tick by an IScarfHaver.
	 */
	public static void updateScarfAttachment(SimpleScarfAttachment attachment, Entity entity) {
		World world = entity.getEntityWorld();
		List<ScarfNode> nodes = attachment.nodes();
		ScarfDesign design = attachment.getDesign();
		Vec3d anchorPosition = attachment.getLocation();
		
		if (design == null) {
			nodes.clear();
			return;
		}
		
		int designLength = design.getLength();
		
		while(nodes.size()>designLength) nodes.remove(nodes.size()-1);
		
		Vec3d lastPos = anchorPosition;
		for(int i=0; i<designLength; i++) {
			FabricSquare square = design.get(i);
			if (nodes.size()<=i) {
				ScarfNode node = new ScarfNode(lastPos, square);
				node.setLastPosition(lastPos);
				nodes.add(node);
			} else {
				ScarfNode node = nodes.get(i);
				node.setSquare(square);
				node.setLastPosition(node.getPosition());
				node.pullTowards(lastPos);
				
				lastPos = node.getPosition();
			}
			
			ScarfNode node = nodes.get(i);
			
			Vec3d prospectivePosition = node.getPosition().add(0, ScarvesClient.SCARF_GRAVITY, 0);
			BlockPos blockInThatPosition = new BlockPos(
					(int) prospectivePosition.x,
					(int) (prospectivePosition.y - ScarfNode.FABRIC_SQUARE_WIDTH),
					(int) prospectivePosition.z
					);
			if (world!=null) {
				if (!world.isTopSolid(blockInThatPosition, entity)) {
					node.setPosition(prospectivePosition);
				}
			}
			
			//Vec3d wind = Vec3d.ZERO; //ScarvesApiImpl.getInstance().getWind(world, node.getPosition());
			Vec3d wind = Wind.getWind(world, node.getPosition());
					//new Vec3d(1, 0, 0.25f).normalize().multiply(0.125);
			//cap wind
			if (wind.lengthSquared()>maxWindMagnitude*maxWindMagnitude) {
				wind = wind.normalize().multiply(maxWindMagnitude);
			}
			
			//move it
			prospectivePosition = node.getPosition().add(wind);
			blockInThatPosition = new BlockPos(
					(int) prospectivePosition.x,
					(int) prospectivePosition.y,
					(int) prospectivePosition.z
					);
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
		
		//System.out.println("Updating only gravity");
		
		Vec3d lastPos = attachment.getLocation();
		for(int i=0; i<nodes.size(); i++) {
			ScarfNode node = nodes.get(i);
			node.setLastPosition(node.getPosition());
			node.pullTowards(lastPos);
			
			Vec3d prospectivePosition = node.getPosition().add(0, ScarvesClient.SCARF_GRAVITY, 0);
			BlockPos blockInThatPosition = new BlockPos(
					(int) prospectivePosition.x,
					(int) prospectivePosition.y,
					(int) prospectivePosition.z
					);
			if (world!=null) {
				if (!world.isTopSolid(blockInThatPosition, entity)) {
					node.setPosition(prospectivePosition);
				}
			}
			
			Vec3d wind = Vec3d.ZERO;//.getInstance().getWind(world, node.getPosition());
			//cap wind
			if (wind.lengthSquared()>maxWindMagnitude*maxWindMagnitude) {
				wind = wind.normalize().multiply(maxWindMagnitude);
			}
			
			//move it
			prospectivePosition = node.getPosition().add(wind);
			blockInThatPosition = new BlockPos(
					(int) prospectivePosition.x,
					(int) prospectivePosition.y,
					(int) prospectivePosition.z
					);
			if (world!=null) {
				if (!world.isTopSolid(blockInThatPosition, entity)) {
					node.setPosition(prospectivePosition);
				}
			}
		}
	}
}
