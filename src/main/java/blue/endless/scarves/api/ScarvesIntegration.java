package blue.endless.scarves.api;

public interface ScarvesIntegration {
	/**
	 * The main thing you can do here is safely call {@link FabricSquareRegistry#register(net.minecraft.block.Block, net.minecraft.util.Identifier)}
	 * 
	 * <p>Set this class aside and don't touch it, and when this method is called register your blocks as fabric squares!
	 */
	void integrateWithScarves();
}
