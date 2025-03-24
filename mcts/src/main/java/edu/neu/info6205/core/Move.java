/*
 * Copyright (c) 2024. Robin Hillyard
 */

package edu.neu.info6205.core;

/**
 * This interface defines the behavior of a Move in a Game.
 *
 * @param <G>
 */
@SuppressWarnings("rawtypes")
public interface Move<G extends Game> {
    /**
     * The player of this Move.
     *
     * @return the player.
     */
    int player();
}