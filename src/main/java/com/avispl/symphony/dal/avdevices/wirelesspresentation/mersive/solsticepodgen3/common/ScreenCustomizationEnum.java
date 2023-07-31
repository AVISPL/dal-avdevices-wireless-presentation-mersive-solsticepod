/*
 *  Copyright (c) 2023 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.avdevices.wirelesspresentation.mersive.solsticepodgen3.common;

import java.util.Arrays;

/**
 * Enumeration representing screen customization options for Solstice.
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 6/15/2023
 * @since 1.0.0
 */
public enum ScreenCustomizationEnum {
	DISPLAY_NAME_ON_MAIN_SCREEN("DisplayNameOnMainScreen", "1"),
	DISPLAY_NAME_ON_PRESENCE_BAR("DisplayNameOnPresenceBar", "4"),
	HOST_IP_ADDRESS_ON_MAIN_SCREEN("HostIPAddressOnMainScreen", "3"),
	HOST_IP_ADDRESS_ON_PRESENCE_BAR("HostIPAddressOnPresenceBar", "5"),
	SCREEN_KEY_ON_MAIN_SCREEN("ScreenKeyOnMainScreen", "6"),
	SCREEN_KEY_ON_PRESENCE_BAR("ScreenKeyOnPresenceBar", "7"),
	;
	private final String name;
	private final String value;

	/**
	 * Constructs a SolsticeScreenCustomizationEnum with the specified name and value.
	 *
	 * @param name the name of the screen name
	 * @param value the value associated with the HDMI output metric
	 */
	ScreenCustomizationEnum(String name, String value) {
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

	/**
	 * Returns the ScreenCustomizationEnum value corresponding to the given name.
	 *
	 * @param name The name of the screen customization option to retrieve.
	 * @return The ScreenCustomizationEnum value matching the given name, or
	 * DISPLAY_NAME_ON_MAIN_SCREEN if no matching value is found.
	 */
	public static ScreenCustomizationEnum getEnumByName(String name) {
		return Arrays.stream(ScreenCustomizationEnum.values())
				.filter(customizationEnum -> customizationEnum.name.equals(name))
				.findFirst()
				.orElse(DISPLAY_NAME_ON_MAIN_SCREEN);
	}
}
