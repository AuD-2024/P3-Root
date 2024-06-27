package p3.gui;

/**
 * An interface for animations of binary trees that can be visualized and stopped.
 * The implementations of this interface are responsible for calling {@link Object#wait()} to wait for the next step
 * of the animation being triggered and update the visualization before waiting.
 * To avoid blocking the JavaFX thread, the animation should be performed in a new thread.
 * To continue the animation, {@link Object#notify()} should be called on the animation from the JavaFX thread.
 * For example:
 * <blockquote><pre>
 * synchronized (animation) {
 *     animation.notify();
 * }
 * </pre></blockquote>
 */
public abstract class Animation<N> {

    protected GraphAnimationScene<N> animationScene;

    protected boolean animate = false;
    protected boolean finishWithNextStep = false;

    public Animation(GraphAnimationScene<N> animationScene) {
        this.animationScene = animationScene;
    }

    public abstract void start();

    /**
     * Turns on the animation, i.e., the thread should wait for the next step of the animation to be triggered once
     * when reaching an appropriate point.
     */
    public void turnOnAnimation() {
        animate = true;
    }

    /**
     * Turns off the animation, i.e., the thread should not wait for the next step of the animation to be triggered
     * and simply finish.
     */
    public void turnOffAnimation() {
        animate = false;
    }

    /**
     * @return {@code true} if the animation is currently running, i.e., the thread will wait for the next step of the
     * animation to be triggered when reaching an appropriate point, {@code false} otherwise.
     */
    public boolean isAnimating() {
        return animate && !finishWithNextStep;
    }

    /**
     * Tells the animation to finish gracefully independent of the current state.
     * This is used when the animation needs to be stopped independent of the current state of {@link #isAnimating()},
     * e.g., when the user closes the application or the next animation is started before the current one is finished.
     */
    public void finishWithNextStep() {
        finishWithNextStep = true;
    }

    /**
     * @return {@code true} if the animation should finish with the next step, {@code false} otherwise
     * @see #finishWithNextStep()
     */
    public boolean isFinishingWithNextStep() {
        return finishWithNextStep;
    }

    /**
     * Causes the current thread to wait until the next step of the animation is triggered using {@link Object#notify()}.
     */
    public void waitUntilNextStep() {
        if (!isFinishingWithNextStep()) {
            synchronized (this) {
                try {
                    this.wait();
                } catch (InterruptedException ignored) {
                }
            }
        }
    }

    public abstract AnimationState getAnimationState();

}
