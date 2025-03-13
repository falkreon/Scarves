package blue.endless.scarves.client;

import java.util.Collection;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.google.common.collect.ImmutableList;

import blue.endless.scarves.api.AnchoredSlot;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public interface IScarfHaver {
	/**
	 * Gets the list of AnchoredSlots which provide scarf designs and attachment points for this Block or Entity. When
	 * Entity Components are a thing, this will migrate to ScarfSlotConfiguration.
	 * @return The list of AnchoredSlots
	 */
	@Nullable
	public ImmutableList<AnchoredSlot> iScarfHaver_getAnchoredSlots();
	
	public void iScarfHaver_setAnchoredSlots(ImmutableList<AnchoredSlot> slots);
	
	@NotNull
	@Environment(EnvType.CLIENT)
	public Collection<SimpleScarfAttachment> iScarfHaver_getAttachments(float tickDelta);
	
	public float iScarfHaver_getBodyYaw(float tickDelta);
}
