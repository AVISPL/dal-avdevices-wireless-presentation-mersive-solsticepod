/*
 *  Copyright (c) 2023 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.avdevices.wirelesspresentation.mersive.solsticepodgen3.common;

/**
 * Enumeration representing the HDMI output options in Solstice.
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 6/8/2023
 * @since 1.0.0
 */
public enum HDMIOutputEnum {
	MIRROR("Mirror", "1"),
	EXTEND("Extend", "3"),
	SEAMLESS_EXTEND("Seamless Extend", "2"),
	;
	private final String name;
	private final String value;

	/**
	 * Constructs a SolsticeHDMIOutPutEnum with the specified name and value.
	 *
	 * @param name the name of the HDMI output metric
	 * @param value the value associated with the HDMI output metric
	 */
	HDMIOutputEnum(String name, String value) {
		this.name = name;
		this.value = value;
	}

	/**
	 * Retrieves {@link #name}
	 *
	 * @return value of {@link #name}
	 */
	public String getName() {
		return name;
	}

	/**
	 * Retrieves {@link #value}
	 *
	 * @return value of {@link #value}
	 */
	public String getValue() {
		return value;
	}
}
