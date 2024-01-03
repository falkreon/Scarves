package blue.endless.scarves.api;

/**
 * This interface can be registered as an entrypoint with the "scarves" id, in order to do mod interop things, such as
 * making your blocks/items work in the Scarf Stapler, or letting Scarves know which way the wind
 * is blowing.
 */
public interface ScarvesIntegration {
	/**
	 * Allows you to call methods on ScarvesApi without worrying about a hard dependency!
	 */
	default void integrateWithScarves(ScarvesApi api) {}
}
