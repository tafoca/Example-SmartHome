/*
 * Java
 *
 * Copyright 2016 IS2T. All rights reserved.
 * Use of this source code is subject to license terms.
 */
package com.microej.demo.smarthome.data.door;

import com.microej.demo.smarthome.data.Device;

/**
 *
 */
public interface Door extends Device<DoorEventListener> {

	boolean isOpen();

}