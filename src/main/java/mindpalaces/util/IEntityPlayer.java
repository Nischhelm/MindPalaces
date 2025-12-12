package mindpalaces.util;

public interface IEntityPlayer {
    /**
     * using this method sets a flag for the player to teleport to/from MP upon waking
     */
    void mp$setDimensionToTPTo(int originalDimension);
}
