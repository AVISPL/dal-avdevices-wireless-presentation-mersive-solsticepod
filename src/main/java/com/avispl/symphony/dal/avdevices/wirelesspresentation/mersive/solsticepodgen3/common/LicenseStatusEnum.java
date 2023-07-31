/*
 *  Copyright (c) 2023 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.avdevices.wirelesspresentation.mersive.solsticepodgen3.common;

/**
 * Enumeration of Solstice license status.
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 6/11/2023
 * @since 1.0.0
 */
public enum LicenseStatusEnum {
	NO_LICENSE("No license", "0"),
	ERROR_READING_LICENSE("Error reading license", "1"),
	LICENSE_OK("License OK", "2"),
	LICENSE_EXPIRED("License Expired", "3"),
	;
	private final String name;
	private final String value;

	/**
	 * Constructs a SolsticeLicenseStatusEnum with the specified name and value.
	 *
	 * @param name the name of the license status
	 * @param value the value of the license status
	 */
	LicenseStatusEnum(String name, String value) {
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
