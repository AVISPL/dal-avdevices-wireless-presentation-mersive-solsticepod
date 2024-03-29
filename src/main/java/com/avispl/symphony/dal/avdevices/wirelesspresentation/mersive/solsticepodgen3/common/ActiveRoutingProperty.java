/*
 *  Copyright (c) 2024 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.avdevices.wirelesspresentation.mersive.solsticepodgen3.common;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ActiveRoutingProperty
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 3/22/2024
 * @since 1.1.0
 */
public enum ActiveRoutingProperty {
	STATE("Status", "state", SolsticeConstant.SESSION_DATA),
	CONNECTION("NumberOfConnections", "connections", SolsticeConstant.SESSION_DATA),
	FRAMES_PER_SECOND("FramesPerSecond", "framesPerSecond", SolsticeConstant.SESSION_DATA),
	BYTE_PER_SECOND("BytesPerSecond", "bytesPerSecond", SolsticeConstant.SESSION_DATA),
	TRIAL("Trial", "trial", SolsticeConstant.LICENSING),
	SUBSCRIPTION("Subscription", "subscription", SolsticeConstant.LICENSING),
	LICENSED("Licensed", "licensed", SolsticeConstant.LICENSING),
	;
	private final String name;
	private final String value;
	private final String type;

	/**
	 * Constructs a ActiveRoutingProperty with the specified values.
	 *
	 * @param name the name of the property
	 * @param value the group of the property
	 * @param type whether the property is a control property
	 */
	ActiveRoutingProperty(String name, String value, String type) {
		this.name = name;
		this.value = value;
		this.type = type;
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
	 * Retrieves {@link #type}
	 *
	 * @return value of {@link #type}
	 */
	public String getType() {
		return type;
	}

	/**
	 * Retrieves a list of ActiveRoutingProperty enums based on the provided type.
	 *
	 * @param type The type of ActiveRoutingProperty enums to filter by.
	 * @return A list of ActiveRoutingProperty enums matching the specified type.
	 */
	public static List<ActiveRoutingProperty> getListByType(String type) {
		return Arrays.stream(ActiveRoutingProperty.values()).filter(item -> item.getType().equals(type))
				.collect(Collectors.toList());
	}
}
