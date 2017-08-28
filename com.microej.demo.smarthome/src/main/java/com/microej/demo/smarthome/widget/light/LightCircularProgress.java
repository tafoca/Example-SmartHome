/*
 * Java
 *
 * Copyright 2016 IS2T. All rights reserved.
 * Use of this source code is subject to license terms.
 */
package com.microej.demo.smarthome.widget.light;

import com.microej.demo.smarthome.data.light.Light;
import com.microej.demo.smarthome.data.light.LightEventListener;
import com.microej.demo.smarthome.widget.CircularProgressWidget;

import ej.microui.display.GraphicsContext;
import ej.microui.display.shape.AntiAliasedShapes;
import ej.style.Style;
import ej.style.container.Rectangle;
import ej.widget.model.BoundedRangeModel;

/**
 * A CircularProgressWidget using a light as a model.
 */
public class LightCircularProgress extends CircularProgressWidget implements LightEventListener {

	private static final int ANIMATION_DURATION = 300;
	private final Light light;

	/**
	 * Instantiates a LightCircularProgress.
	 *
	 * @param model
	 *            the BoundedRangeModel.
	 * @param light
	 *            the light.
	 */
	public LightCircularProgress(final BoundedRangeModel model, final Light light) {
		super(model, ANIMATION_DURATION);
		this.light = light;


		onBrightnessChange(light.getBrightness());
		onStateChange(light.isOn());
		resetAnimation();
	}

	/**
	 * Renders the widget
	 */
	@Override
	public void renderContent(final GraphicsContext g, final Style style, final Rectangle bounds) {
		// render parent
		super.renderContent(g, style, bounds);

		// set style
		if (isEnabled()) {
			// fill circle
			final int innerD = getDiameter() / 2;
			final int innerX = getCircleX() + (getDiameter() - innerD) / 2;
			final int innerY = getCircleY() + (getDiameter() - innerD) / 2;
			g.setColor(light.getColor());
			g.removeBackgroundColor();
			g.fillCircle(innerX, innerY, innerD);

			// draw anti aliased circle
			final AntiAliasedShapes antiAliasedShapes = AntiAliasedShapes.Singleton;
			antiAliasedShapes.setThickness(2);
			antiAliasedShapes.setFade(1);
			antiAliasedShapes.drawCircle(g, innerX, innerY, innerD);

			// draw border
			g.setColor(style.getBackgroundColor());
			antiAliasedShapes.setThickness(1);
			antiAliasedShapes.drawCircle(g, innerX-1, innerY-1, innerD+2);
		}
	}

	@Override
	protected int getColor(final Style style) {
		return light.getColor();
	}

	@Override
	public void onColorChange(final int color) {
		// Do nothing.
	}

	@Override
	public void onBrightnessChange(final float brightness) {
		final int min = getMinimum();
		final int max = getMaximum();
		final float value = (max - min) * brightness + min;
		setValue((int) value);
	}

	@Override
	public void onStateChange(final boolean on) {
		setEnabled(on);
	}

	@Override
	public void showNotify() {
		super.showNotify();

		light.addListener(this);
		onBrightnessChange(light.getBrightness());
		onStateChange(light.isOn());
	}

	@Override
	public void hideNotify() {
		super.hideNotify();
		light.removeListener(this);
	}
}