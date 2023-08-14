/*
 *  Copyright (c) 2023 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.avdevices.wirelesspresentation.mersive.solsticepodgen3.common;

import java.util.Arrays;
import java.util.Optional;

/**
 * Enumeration of Solstice properties.
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 6/8/2023
 * @since 1.0.0
 */
public enum SolsticePropertiesList {
	DISPLAY_ID("DisplayId", SolsticeConstant.EMPTY, false, SolsticeConstant.EMPTY, "m_displayId"),
	IPV4("IP", SolsticeConstant.EMPTY, false, "m_displayInformation", "m_ipv4"),
	SERVER_VERSION("ServerVersion", SolsticeConstant.EMPTY, false, SolsticeConstant.EMPTY, "m_serverVersion"),
	PRODUCT_NAME("ProductName", SolsticeConstant.EMPTY, false, SolsticeConstant.EMPTY, "m_productName"),
	PRODUCT_VARIANT("ProductVariant", SolsticeConstant.EMPTY, false, SolsticeConstant.EMPTY, "m_productVariant"),
	PRODUCT_HARDWARE_VERSION("ProductHardwareVersion", SolsticeConstant.EMPTY, false, SolsticeConstant.EMPTY, "m_productHardwareVersion"),
	PORT("Port", SolsticeConstant.EMPTY, false, "m_displayInformation", "m_port"),
	DEVICE_ID("DeviceId", SolsticeConstant.EMPTY, false, "m_licenseCuration", "fulfillmentId"),
	SCHEDULED_DAILY_REBOOT("Active", SolsticeConstant.REBOOT_SCHEDULING_GROUP, true, "m_systemCuration", "scheduledRestartEnabled"),
	REBOOT_TIME_OF_DAY_HOUR("Hour", SolsticeConstant.REBOOT_SCHEDULING_GROUP, true, "m_systemCuration", "scheduledRestartTime"),
	REBOOT_TIME_OF_DAY_MINUTE("Minute", SolsticeConstant.REBOOT_SCHEDULING_GROUP, true, "m_systemCuration", "scheduledRestartTime"),
	DISPLAY_NAME("DisplayName", SolsticeConstant.NAMING_AND_DISCOVERY_GROUP, true, "m_displayInformation", "m_displayName"),
	SDS_HOST("SDSHost1", SolsticeConstant.NAMING_AND_DISCOVERY_GROUP, false, "m_networkCuration", "sdsHostName"),
	SDS_HOST2("SDSHost2", SolsticeConstant.NAMING_AND_DISCOVERY_GROUP, false, "m_networkCuration", "sdsHostName2"),
	DISPLAY_NAME_ON_MAIN_SCREEN("DisplayNameOnMainScreen", SolsticeConstant.NAMING_AND_DISCOVERY_GROUP, true, "m_networkCuration", "connectionShowFlags"),
	DISPLAY_NAME_ON_PRESENCE_BAR("DisplayNameOnPresenceBar", SolsticeConstant.NAMING_AND_DISCOVERY_GROUP, true, "m_networkCuration", "connectionShowFlags"),
	HOST_IP_ADDRESS_ON_MAIN_SCREEN("HostIPAddressOnMainScreen", SolsticeConstant.NAMING_AND_DISCOVERY_GROUP, true, "m_networkCuration", "connectionShowFlags"),
	HOST_IP_ADDRESS_ON_PRESENCE_BAR("HostIPAddressOnPresenceBar", SolsticeConstant.NAMING_AND_DISCOVERY_GROUP, true, "m_networkCuration", "connectionShowFlags"),
	SCREEN_KEY_ON_MAIN_SCREEN("ScreenKeyOnMainScreen", SolsticeConstant.NAMING_AND_DISCOVERY_GROUP, true, "m_networkCuration", "connectionShowFlags"),
	SCREEN_KEY_ON_PRESENCE_BAR("ScreenKeyOnPresenceBar", SolsticeConstant.NAMING_AND_DISCOVERY_GROUP, true, "m_networkCuration", "connectionShowFlags"),
	BROADCAST_DISPLAY_NAME("BroadcastDisplayName", SolsticeConstant.NAMING_AND_DISCOVERY_GROUP, true, "m_networkCuration", "discoveryBroadcastEnabled"),
	PUBLISH_DISPLAY_NAME("PublishDisplayName", SolsticeConstant.NAMING_AND_DISCOVERY_GROUP, true, "m_networkCuration", "publishToNameServer"),
	HDMI_OUTPUT_MODE("HDMIOutputMode", SolsticeConstant.APPEARANCE_GROUP, true, "m_generalCuration", "hdmiOutDisplayMode"),
	SCREEN_KEY("EnableScreenKey", SolsticeConstant.ACCESS_CONTROL_GROUP, true, "m_authenticationCuration", "screenKeyEnabled"),
	DISABLE_MODERATOR_APPROVAL("DisableModeratorApproval", SolsticeConstant.ACCESS_CONTROL_GROUP, true, "m_authenticationCuration", "moderatorApprovalDisabled"),
	BROWSER_LOOK_IN("BrowserLookIn", SolsticeConstant.ACCESS_CONTROL_GROUP, true, "m_networkCuration", "remoteViewMode"),
	DESKTOP_SCREEN_SHARING("DesktopScreenSharing", SolsticeConstant.RESOURCE_RESTRICTION_GROUP, true, "m_networkCuration", "postTypeDesktopSupported"),
	APPLICATION_WINDOW_SHARING("ApplicationWindowSharing", SolsticeConstant.RESOURCE_RESTRICTION_GROUP, true, "m_networkCuration", "postTypeApplicationWindowSupported"),
	ANDROID_MIRRORING("AndroidMirroring", SolsticeConstant.RESOURCE_RESTRICTION_GROUP, true, "m_networkCuration", "postTypeAndroidMirroringSupported"),
	IOS_MIRRORING("IOSMirroring", SolsticeConstant.RESOURCE_RESTRICTION_GROUP, true, "m_networkCuration", "postTypeAirPlaySupported"),
	AIRPLAY_DISCOVERY_PROXY("AirPlayDiscoveryProxy", SolsticeConstant.RESOURCE_RESTRICTION_GROUP, true, "m_networkCuration", "bonjourProxyEnabled"),
	VIDEO_FILES_AND_IMAGES("VideoFilesAndImagesSharing", SolsticeConstant.RESOURCE_RESTRICTION_GROUP, true, "m_networkCuration", "postTypeMediaFilesSupported"),
	CLIENT_QUICK_CONNECT_ACTION("ClientQuickConnectAction", SolsticeConstant.RESOURCE_RESTRICTION_GROUP, true, SolsticeConstant.EMPTY, SolsticeConstant.EMPTY),
	MAX_CONNECTIONS("MaximumConnections", SolsticeConstant.RESOURCE_RESTRICTION_GROUP, true, "m_networkCuration", "maximumConnections"),
	MAX_POSTS("MaximumPosts", SolsticeConstant.RESOURCE_RESTRICTION_GROUP, true, "m_networkCuration", "maximumPublished"),
	AUTOMATICALLY_RESIZE_IMAGES("AutomaticallyResizeImages(MPixels)", SolsticeConstant.RESOURCE_RESTRICTION_GROUP, true, "m_networkCuration", "maximumImageSize"),
	TIME_SERVER("TimeServer", SolsticeConstant.EMPTY, false, "m_systemCuration", "ntpServer"),
	DATE("Date", SolsticeConstant.EMPTY, false, "m_systemCuration", "dateTime"),
	TIME("Time", SolsticeConstant.EMPTY, false, "m_systemCuration", "dateTime"),
	HOST_NAME("HostName", SolsticeConstant.EMPTY, false, "m_displayInformation", "m_hostName"),
	LANGUAGE("Language", SolsticeConstant.EMPTY, true, "m_generalCuration", "language"),
	TIME_ZONE("TimeZone", SolsticeConstant.EMPTY, true, "m_systemCuration", "timeZone"),
	USE_24_HOUR_TIME_FORMAT("Use24HourTimeFormat", SolsticeConstant.EMPTY, true, "m_systemCuration", "l24HourTime"),
	LICENSE_STATUS("LicenseStatus", SolsticeConstant.LICENSE_GROUP, false, "m_licenseCuration", "licenseStatus"),
	EXPIRATION_DATE("ExpirationDate", SolsticeConstant.LICENSE_GROUP, false, "m_licenseCuration", "expirationDate"),
	NUM_DAYS_TO_EXPIRATION("NumDaysToExpiration", SolsticeConstant.LICENSE_GROUP, false, "m_licenseCuration", "numDaysToExpiration"),
	CURRENT_POST_COUNT("CurrentPostCount", SolsticeConstant.STATISTICS_GROUP, false, "m_statistics", "m_currentPostCount"),
	CURRENT_BANDWIDTH("CurrentBandwidth(Mbps)", SolsticeConstant.STATISTICS_GROUP, false, "m_statistics", "m_currentBandwidth"),
	CURRENT_LIVE_SOURCE_COUNT("CurrentLiveSourceCount", SolsticeConstant.STATISTICS_GROUP, false, "m_statistics", "m_currentLiveSourceCount"),
	CONNECTED_USERS("ConnectedUsers", SolsticeConstant.STATISTICS_GROUP, false, "m_statistics", "m_connectedUsers"),
	TIME_SINCE_LAST_CONNECTION_INITIALIZE("TimeSinceLastConnectionInitialize", SolsticeConstant.STATISTICS_GROUP, false, "m_statistics", "m_timeSinceLastConnectionInitialize"),
	SET_DEFAULT_BACKGROUND("DefaultBackground", SolsticeConstant.APPEARANCE_GROUP, true, SolsticeConstant.EMPTY, SolsticeConstant.EMPTY),
	;
	private final String name;
	private final String group;
	private boolean isControl;
	private final String apiGroupName;
	private final String apiPropertyName;

	/**
	 * Constructs a SolsticePropertiesList with the specified values.
	 *
	 * @param name the name of the property
	 * @param group the group of the property
	 * @param isControl whether the property is a control property
	 * @param apiGroupName the API group name of the property
	 * @param apiPropertyName the API property name of the property
	 */
	SolsticePropertiesList(String name, String group, boolean isControl, String apiGroupName, String apiPropertyName) {
		this.name = name;
		this.group = group;
		this.isControl = isControl;
		this.apiGroupName = apiGroupName;
		this.apiPropertyName = apiPropertyName;
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
	 * Retrieves {@link #group}
	 *
	 * @return value of {@link #group}
	 */
	public String getGroup() {
		return group;
	}

	/**
	 * Retrieves {@link #isControl}
	 *
	 * @return value of {@link #isControl}
	 */
	public boolean isControl() {
		return isControl;
	}

	/**
	 * Sets {@link #isControl} value
	 *
	 * @param control new value of {@link #isControl}
	 */
	public void setControl(boolean control) {
		isControl = control;
	}

	/**
	 * Retrieves {@link #apiGroupName}
	 *
	 * @return value of {@link #apiGroupName}
	 */
	public String getApiGroupName() {
		return apiGroupName;
	}

	/**
	 * Retrieves {@link #apiPropertyName}
	 *
	 * @return value of {@link #apiPropertyName}
	 */
	public String getApiPropertyName() {
		return apiPropertyName;
	}

	/**
	 * This method is used to get device metric group by name
	 *
	 * @param name is the name of device metric group that want to get
	 * @return SolsticePropertiesMetric is the device metric group that want to get
	 */
	public static SolsticePropertiesList getByName(String name) {
		Optional<SolsticePropertiesList> property = Arrays.stream(SolsticePropertiesList.values()).filter(group -> group.getName().equals(name)).findFirst();
		if (property.isPresent()) {
			return property.get();
		} else {
			throw new IllegalStateException(String.format("control group %s is not supported.", name));
		}
	}
}
