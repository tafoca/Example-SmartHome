/*
 * Java
 *
 * Copyright 2016 IS2T. All rights reserved.
 * Use of this source code is subject to license terms.
 */
package com.microej.demo.smarthome.widget.thermostat;

import com.microej.demo.smarthome.data.thermostat.Thermostat;
import com.microej.demo.smarthome.model.ThermostatBoundedRangeModel;
import com.microej.demo.smarthome.style.ClassSelectors;
import com.microej.demo.smarthome.util.Strings;
import com.microej.demo.smarthome.widget.LimitedButtonWrapper;
import com.microej.demo.smarthome.widget.OverlapComposite;

import ej.mwt.Widget;
import ej.widget.basic.Label;
import ej.widget.composed.ButtonWrapper;
import ej.widget.composed.Wrapper;
import ej.widget.container.Grid;
import ej.widget.container.List;
import ej.widget.listener.OnClickListener;
import ej.widget.listener.OnValueChangeListener;

/**
 * A thermostat widget.
 */
public class ThermostatWidget extends Grid {

	private final Thermostat thermostat;
	private final ThermostatBoundedRangeModel model;
	private final ButtonWrapper button;
	private TemperatureLabel desiredTemperature;
	private TemperatureLabel currentTemperature;
	private String lastClassSelector = null;
	private Label desiredLabel;
	private final OverlapComposite composite;

	/**
	 * Instantiates a ThermostatWidget.
	 *
	 * @param thermostat
	 *            the model.
	 */
	public ThermostatWidget(final Thermostat thermostat) {
		super(true, 3);
		this.thermostat = thermostat;
		model = new ThermostatBoundedRangeModel(thermostat);

		composite = new OverlapComposite();

		final ThermostatCircularProgress thermostatCircularProgress = new ThermostatCircularProgress(model);
		thermostatCircularProgress.addClassSelector(ClassSelectors.THERMOSTAT);
		composite.add(thermostatCircularProgress);
		final OnValueChangeListener onValueChangeListener = new ThermostatValueChangeListener(
				thermostatCircularProgress);
		thermostatCircularProgress.addOnTargetValueChangeListener(onValueChangeListener);
		thermostatCircularProgress.addOnValueChangeListener(onValueChangeListener);

		button = new LimitedButtonWrapper();
		button.setAdjustedToChild(false);
		final Label okLabel = new Label(Strings.OK);
		okLabel.addClassSelector(ClassSelectors.THERMOSTAT_VALIDATE);
		button.setWidget(okLabel);
		button.addOnClickListener(new OnClickListener() {

			@Override
			public void onClick() {
				thermostatCircularProgress.validateTagetValue();
				// Removes OK button.
				updateButton(0, 0);

			}
		});
		composite.add(button);
		button.setVisible(false);


		add(createCurrentLabel());
		add(composite);

		add(createDesiredLabel());
		updateClassSelectors(model.getValue(), model.getTargetValue());
	}

	private Widget createDesiredLabel() {
		desiredLabel = new Label(Strings.DESIRED);
		desiredLabel.addClassSelector(ClassSelectors.THERMOSTAT_TOP_LABEL);
		desiredTemperature = new TemperatureLabel(thermostat.getTargetTemperature(), thermostat.getMaxTemperature());
		desiredTemperature.addClassSelector(ClassSelectors.THERMOSTAT_BOTTOM_LABEL);
		return createLabel(desiredLabel, desiredTemperature);
	}

	private Widget createCurrentLabel() {
		final Label topLabel = new Label(Strings.CURRENT);
		topLabel.addClassSelector(ClassSelectors.THERMOSTAT_TOP_LABEL);
		topLabel.addClassSelector(ClassSelectors.THERMOSTAT_CURRENT);
		currentTemperature = new TemperatureLabel(model.getValue(), model.getMaximum());
		currentTemperature.addClassSelector(ClassSelectors.THERMOSTAT_BOTTOM_LABEL);

		return createLabel(topLabel, currentTemperature);
	}

	private Widget createLabel(final Widget top, final Widget bottom) {
		final Wrapper label = new Wrapper();
		final List list = new List(false);
		list.add(top);
		list.add(bottom);
		label.setWidget(list);
		label.setAdjustedToChild(false);

		return label;
	}

	@Override
	public void setBounds(final int x, final int y, final int width, final int height) {
		super.setBounds(x, y, width, height);
	}

	private void updateClassSelectors(final int current, final int target) {
		if (current == target) {
			setDesiredClassSelector(null);
		} else if (current > target) {
			setDesiredClassSelector(ClassSelectors.THERMOSTAT_DESIRED_COLD);
		} else /* current < target */ {
			setDesiredClassSelector(ClassSelectors.COLOR_CORAL);
		}
	}

	private void setDesiredClassSelector(final String classSelector) {
		if (lastClassSelector != classSelector) {
			if (lastClassSelector != null) {
				desiredTemperature.removeClassSelector(lastClassSelector);
				desiredLabel.removeClassSelector(lastClassSelector);
			}
			if (classSelector != null) {
				desiredTemperature.addClassSelector(classSelector);
				desiredLabel.addClassSelector(classSelector);
			}

			lastClassSelector = classSelector;
		}
	}

	private void updateButton(final int targetTemperature, final int targetValue) {
		if (targetTemperature == targetValue && button.isVisible()) {
			button.setVisible(false);
			if (isShown()) {
				composite.revalidateSubTree();
			}
		} else if (targetTemperature != targetValue && !button.isVisible()) {
			button.setVisible(true);
			if (isShown()) {
				composite.revalidateSubTree();
			}
		}
	}

	private class ThermostatValueChangeListener implements OnValueChangeListener
	{

		private final ThermostatCircularProgress thermostatCircularProgress;

		/**
		 * @param thermostatCircularProgress
		 */
		public ThermostatValueChangeListener(final ThermostatCircularProgress thermostatCircularProgress) {
			this.thermostatCircularProgress = thermostatCircularProgress;
		}

		@Override
		public void onValueChange(final int newValue) {
			desiredTemperature.setTemperature(thermostatCircularProgress.getTargetValue());
			currentTemperature.setTemperature(thermostat.getTemperature());
			final int localTarget = thermostatCircularProgress.getTargetValue() / 10;
			final int target = thermostat.getTargetTemperature() / 10;
			updateClassSelectors(thermostat.getTemperature() / 10, localTarget);
			updateButton(target, localTarget);
		}

		@Override
		public void onMinimumValueChange(final int newMinimum) {
			// Not used.

		}

		@Override
		public void onMaximumValueChange(final int newMaximum) {
			// Not used.

		}
	};
}