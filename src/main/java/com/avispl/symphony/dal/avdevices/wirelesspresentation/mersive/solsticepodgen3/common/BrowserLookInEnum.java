/*
 *  Copyright (c) 2023 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.avdevices.wirelesspresentation.mersive.solsticepodgen3.common;

/**
 * SolsticeBrowserLookInEnum is enum representing the look-in metric options for the Solstice browser.
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 6/10/2023
 * @since 1.0.0
 */
public enum BrowserLookInEnum {
	ENABLED("Enabled", "1"),
	DISABLED("Disabled", "0"),
	RUNTIME("Determine at Runtime", "2"),
	;
	private final String name;
	private final String value;

	/**
	 * Constructs a SolsticeBrowserLookInEnum enum with the given name and value.
	 *
	 * @param name the display name of the metric
	 * @param value the corresponding value of the metric
	 */
	BrowserLookInEnum(String name, String value) {
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
