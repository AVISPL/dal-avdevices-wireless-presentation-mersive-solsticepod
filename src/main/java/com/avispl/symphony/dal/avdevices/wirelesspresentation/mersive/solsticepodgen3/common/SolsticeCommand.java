/*
 *  Copyright (c) 2023 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.avdevices.wirelesspresentation.mersive.solsticepodgen3.common;

/**
 * SolsticeCommand containing constants for Solstice commands.
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 6/8/2023
 * @since 1.0.0
 */
public class SolsticeCommand {
	public static final String CONFIG_COMMAND = "https://%s/api/config";
	public static final String STATS_COMMAND = "https://%s/api/stats";
	public static final String RESET_KEY = "https://%s/api/control/resetkey";
	public static final String SET_DEFAULT_BACKGROUND = "https://%s/api/config/splashbackground";
	public static final String AUTHENTICATION_COMMAND = "v2/token";
	public static final String GET_CURRENT_SESSION_COMMAND = "v2/content/activerouting";
	public static final String GET_LICENSING_COMMAND = "v2/content/activerouting/licensing";
	public static final String GET_CONNECTIONS_COMMAND = "v2/content/activerouting/connections";
}
