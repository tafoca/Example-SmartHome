/*
 * Java
 *
 * Copyright 2016 IS2T. All rights reserved.
 * Use of this source code is subject to license terms.
 */
package com.microej.demo.smarthome.widget.dashboard;

import ej.animation.Animation;
import ej.animation.Animator;
import ej.components.dependencyinjection.ServiceLoaderFactory;
import ej.microui.display.GraphicsContext;
import ej.microui.display.shape.AntiAliasedShapes;
import ej.motion.Motion;
import ej.motion.linear.LinearMotion;
import ej.style.Style;
import ej.style.container.Rectangle;
import ej.style.util.StyleHelper;
import ej.widget.basic.BoundedRange;

/**
 * A bar displaying the current power consumption.
 */
public class InstantPowerBar extends BoundedRange {

	protected static final int MOTION_DURATION = 500;
	private int target;
	private Animation animation;
	private final Object sync = new Object();
	private Motion motion;

	/**
	 * Intantiates an InstantPowerBar.
	 */
	public InstantPowerBar(final int min, final int max, final int initialValue) {
		super(min, max, initialValue);
		target = initialValue;
	}

	@Override
	public void showNotify() {
		super.showNotify();
	}

	@Override
	public void hideNotify() {
		super.hideNotify();
		stopAnimation();
	}


	@Override
	public void setValue(final int value) {
		if (value != target) {
			target = value;
			startAnimation();
		}
	}

	@Override
	public void renderContent(final GraphicsContext g, final Style style, final Rectangle bounds) {
		g.setColor(style.getBackgroundColor());
		final int y = (bounds.getY() + bounds.getHeight()) >> 1;
		final int x = bounds.getX();
		final int endX = x + bounds.getWidth();
		g.drawLine(x, y, endX, y);

		g.setColor(style.getForegroundColor());
		AntiAliasedShapes.Singleton.setFade(1);
		AntiAliasedShapes.Singleton.setThickness(4);
		final float complete = getPercentComplete();
		AntiAliasedShapes.Singleton.drawLine(g, x, y, (int) (endX * complete), y);

	}

	@Override
	public Rectangle validateContent(final Style style, final Rectangle availableSize) {
		final int size = StyleHelper.getFont(style).getHeight() << 1;
		return new Rectangle(0, 0, size, size);
	}

	private void startAnimation() {
		synchronized (sync) {
			if (animation == null) {
				motion = new LinearMotion(getValue(), target, MOTION_DURATION);

				motion.start();
				final Animator animator = ServiceLoaderFactory.getServiceLoader().getService(Animator.class);
				animator.startAnimation(new Animation() {

					@Override
					public boolean tick(final long currentTimeMillis) {
						final int dif = target - getValue();
						if (dif == 0) {
							animation = null;
							return false;
						}
						setModelValue(motion.getCurrentValue());
						return true;
					}
				});
			}
		}
	}

	private void setModelValue(final int i) {
		super.setValue(i);

	}

	private void stopAnimation() {
		synchronized (sync) {
			if (animation != null) {
				final Animator animator = ServiceLoaderFactory.getServiceLoader().getService(Animator.class);
				animator.stopAnimation(animation);
				animation = null;
			}
		}
	}
}