/*
 * Java
 *
 * Copyright 2016 IS2T. All rights reserved.
 * Use of this source code is subject to license terms.
 */
package com.microej.demo.smarthome.data.power;

import com.microej.demo.smarthome.data.Device;

/**
 * A Power Meter.
 */
public interface PowerMeter extends Device<PowerEventListener> {

	/**
	 * Gets the current power consumption.
	 *
	 * @return the current power consumption.
	 */
	InstantPower getInstantPowerConsumption();

	/**
	 * Gets the history of power consumptions.
	 *
	 * @return the history of power consumptions.
	 */
	InstantPower[] getPowerConsumptions();

	/**
	 * Gets the maximum power consumption.
	 *
	 * @return the maximum power consumption.
	 */
	int getMaxPowerConsumption();

}
