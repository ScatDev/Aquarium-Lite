package dev.scat.aquarium.util.time;

/**
 * Represents a tick time tracker utility.
 */
public class TickTimer {
    /**
     * The existed tick count.
     */
    private int ticks;

    /**
     * Initialize tick timer with an initial value.
     *
     * @param ticks initial ticks
     */
    public TickTimer(int ticks) {
        this.ticks = ticks;
    }

    /**
     * Initialize tick timer.
     */
    public TickTimer() {
    }

    /**
     * Increment the tick count.
     *
     * @return incremented count
     */
    public int tick() {
        return ++ticks;
    }

    public void tick(final boolean shouldTick) {
        if (!shouldTick)
            tick();
        else
            reset();
    }

    /**
     * Reset the tick count.
     */
    public void reset() {
        ticks = 0;
    }

    /**
     * Check if an amount of ticks has passed.
     *
     * @param ticks tick count minimum limit
     * @return timer has already exceeded the limit
     */
    public boolean passed(int ticks) {
        return this.ticks > ticks;
    }

    /**
     * Check if the timer hasn't passed the given tick count.
     *
     * @param ticks tick count maximum limit
     * @return timer hasn't exceeded the limit
     */
    public boolean occurred(int ticks) {
        return this.ticks <= ticks;
    }

    public int getTicks() {
        return ticks;
    }
}
