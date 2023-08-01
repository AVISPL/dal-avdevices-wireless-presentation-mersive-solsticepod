/*
 *  Copyright (c) 2023 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.avdevices.wirelesspresentation.mersive.solsticepodgen3.dto;

/**
 * Represents a time zone with its ID, name, and offset.
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 6/10/2023
 * @since 1.0.0
 */
public class TimeZone {
	private String id;
	private String name;
	private int offset;

	/**
	 * Constructs a new TimeZone object with the specified ID, name, and offset.
	 *
	 * @param id the ID of the time zone
	 * @param name the name of the time zone
	 * @param offset the offset of the time zone from UTC in minutes
	 */
	public TimeZone(String id, String name, int offset) {
		this.id = id;
		this.name = name;
		this.offset = offset;
	}

	/**
	 * Retrieves {@link #id}
	 *
	 * @return value of {@link #id}
	 */
	public String getId() {
		return id;
	}

	/**
	 * Sets {@link #id} value
	 *
	 * @param id new value of {@link #id}
	 */
	public void setId(String id) {
		this.id = id;
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
	 * Sets {@link #name} value
	 *
	 * @param name new value of {@link #name}
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Retrieves {@link #offset}
	 *
	 * @return value of {@link #offset}
	 */
	public int getOffset() {
		return offset;
	}

	/**
	 * Sets {@link #offset} value
	 *
	 * @param offset new value of {@link #offset}
	 */
	public void setOffset(int offset) {
		this.offset = offset;
	}
}
