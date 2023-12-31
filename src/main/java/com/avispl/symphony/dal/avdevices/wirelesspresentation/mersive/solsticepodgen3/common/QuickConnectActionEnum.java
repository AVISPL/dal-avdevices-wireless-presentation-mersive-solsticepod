/*
 *  Copyright (c) 2023 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.avdevices.wirelesspresentation.mersive.solsticepodgen3.common;

/**
 * Enumeration representing quick connect actions in Solstice.
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 6/12/2023
 * @since 1.0.0
 */
public enum QuickConnectActionEnum {
	AUTO_CONNECT("Launch Client and automatically connect to Display"),
	AUTO_SDS("Launch Client and automatically set SDS for Client"),
	LAUNCH_CLIENT("Launch Client"),
	;
	private final String name;

	/**
	 * Constructs a SolsticeQuickConnectActionEnum with the specified name.
	 *
	 * @param name the name of the quick connect action
	 */
	QuickConnectActionEnum(String name) {
		this.name = name;
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
	 * Returns the name of the quick connect action based on the provided values.
	 *
	 * @param autoConnectValue the value of autoConnect
	 * @param autoSDSValue the value of autoSDS
	 * @return the name of the quick connect action
	 */
	public static String getNameByValue(String autoConnectValue, String autoSDSValue) {
		if (SolsticeConstant.TRUE.equals(autoConnectValue)) {
			return AUTO_CONNECT.getName();
		} else if (SolsticeConstant.TRUE.equals(autoSDSValue)) {
			return AUTO_SDS.getName();
		} else {
			return LAUNCH_CLIENT.getName();
		}
	}
}
