/*
 *  Copyright (c) 2023 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.avdevices.wirelesspresentation.mersive.solsticepodgen3;

import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.avispl.symphony.api.dal.control.Controller;
import com.avispl.symphony.api.dal.dto.control.AdvancedControllableProperty;
import com.avispl.symphony.api.dal.dto.control.ControllableProperty;
import com.avispl.symphony.api.dal.dto.monitor.ExtendedStatistics;
import com.avispl.symphony.api.dal.dto.monitor.Statistics;
import com.avispl.symphony.api.dal.dto.monitor.aggregator.AggregatedDevice;
import com.avispl.symphony.api.dal.error.ResourceNotReachableException;
import com.avispl.symphony.api.dal.monitor.Monitorable;
import com.avispl.symphony.dal.aggregator.parser.AggregatedDeviceProcessor;
import com.avispl.symphony.dal.aggregator.parser.PropertiesMapping;
import com.avispl.symphony.dal.aggregator.parser.PropertiesMappingParser;
import com.avispl.symphony.dal.avdevices.wirelesspresentation.mersive.solsticepodgen3.common.BrowserLookInEnum;
import com.avispl.symphony.dal.avdevices.wirelesspresentation.mersive.solsticepodgen3.common.EnumTypeHandler;
import com.avispl.symphony.dal.avdevices.wirelesspresentation.mersive.solsticepodgen3.common.HDMIOutputEnum;
import com.avispl.symphony.dal.avdevices.wirelesspresentation.mersive.solsticepodgen3.common.LanguageEnum;
import com.avispl.symphony.dal.avdevices.wirelesspresentation.mersive.solsticepodgen3.common.LicenseStatusEnum;
import com.avispl.symphony.dal.avdevices.wirelesspresentation.mersive.solsticepodgen3.common.QuickConnectActionEnum;
import com.avispl.symphony.dal.avdevices.wirelesspresentation.mersive.solsticepodgen3.common.ScreenCustomizationEnum;
import com.avispl.symphony.dal.avdevices.wirelesspresentation.mersive.solsticepodgen3.common.SolsticeCommand;
import com.avispl.symphony.dal.avdevices.wirelesspresentation.mersive.solsticepodgen3.common.SolsticeConstant;
import com.avispl.symphony.dal.avdevices.wirelesspresentation.mersive.solsticepodgen3.common.SolsticePropertiesList;
import com.avispl.symphony.dal.avdevices.wirelesspresentation.mersive.solsticepodgen3.dto.TimeZone;
import com.avispl.symphony.dal.communicator.RestCommunicator;
import com.avispl.symphony.dal.util.StringUtils;

/**
 * SolsticePodGen3Communicator
 *
 * @author Kevin / Symphony Dev Team<br>
 * Created on 6/8/2023
 * @since 1.0.0
 */
public class SolsticePodGen3Communicator extends RestCommunicator implements Monitorable, Controller {
	/**
	 * ObjectMapper is a Jackson library object mapper used for JSON serialization and deserialization.
	 */
	ObjectMapper objectMapper = new ObjectMapper();

	/**
	 * localExtendedStatistics represents the extended statistics object.
	 */
	private ExtendedStatistics localExtendedStatistics;

	/**
	 * reentrantLock is a reentrant lock used for synchronization.
	 */
	private final ReentrantLock reentrantLock = new ReentrantLock();

	/**
	 * isEmergencyDelivery indicates whether it is an emergency delivery.
	 */
	private boolean isEmergencyDelivery;

	/**
	 * adminPassword represents the password set on the device.
	 */
	private String adminPassword;

	/**
	 * An instance of the AggregatedDeviceProcessor class used to process and aggregate device-related data.
	 */
	private final AggregatedDeviceProcessor aggregatedDeviceProcessor;

	/**
	 * configManagement imported from the user interface
	 */
	private String configManagement;

	/**
	 * configManagement in boolean value
	 */
	private boolean isConfigManagement;

	/**
	 * Local cache stores data after a period of time
	 */
	private final Map<String, String> localCacheMapOfPropertyNameAndValue = new HashMap<>();

	/**
	 * availableTimeZones represents a list of available time zones.
	 */
	private final List<TimeZone> availableTimeZones = new ArrayList<>();

	/**
	 * Retrieves {@code {@link #configManagement}}
	 *
	 * @return value of {@link #configManagement}
	 */
	public String getConfigManagement() {
		return configManagement;
	}

	/**
	 * Sets {@code configManagement}
	 *
	 * @param configManagement the {@code java.lang.String} field
	 */
	public void setConfigManagement(String configManagement) {
		this.configManagement = configManagement;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void internalInit() throws Exception {
		super.internalInit();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void internalDestroy() {
		if (localExtendedStatistics != null && localExtendedStatistics.getStatistics() != null && localExtendedStatistics.getControllableProperties() != null) {
			localExtendedStatistics.getStatistics().clear();
			localExtendedStatistics.getControllableProperties().clear();
		}
		if (!localCacheMapOfPropertyNameAndValue.isEmpty()) {
			localCacheMapOfPropertyNameAndValue.clear();
		}
		isConfigManagement = false;
		super.internalDestroy();
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 *
	 * Check for available devices before retrieving the value
	 * ping latency information to Symphony
	 */
	@Override
	public int ping() throws Exception {
		if (isInitialized()) {
			long pingResultTotal = 0L;

			for (int i = 0; i < this.getPingAttempts(); i++) {
				long startTime = System.currentTimeMillis();

				try (Socket puSocketConnection = new Socket(this.host, this.getPort())) {
					puSocketConnection.setSoTimeout(this.getPingTimeout());
					if (puSocketConnection.isConnected()) {
						long pingResult = System.currentTimeMillis() - startTime;
						pingResultTotal += pingResult;
						if (this.logger.isTraceEnabled()) {
							this.logger.trace(String.format("PING OK: Attempt #%s to connect to %s on port %s succeeded in %s ms", i + 1, host, this.getPort(), pingResult));
						}
					} else {
						if (this.logger.isDebugEnabled()) {
							this.logger.debug(String.format("PING DISCONNECTED: Connection to %s did not succeed within the timeout period of %sms", host, this.getPingTimeout()));
						}
						return this.getPingTimeout();
					}
				} catch (SocketTimeoutException | ConnectException tex) {
					if (this.logger.isDebugEnabled()) {
						this.logger.error(String.format("PING TIMEOUT: Connection to %s did not succeed within the timeout period of %sms", host, this.getPingTimeout()));
					}
					throw new SocketTimeoutException("Connection timed out");
				} catch (Exception e) {
					if (this.logger.isDebugEnabled()) {
						this.logger.error(String.format("PING TIMEOUT: Connection to %s did not succeed, UNKNOWN ERROR %s: ", host, e.getMessage()));
					}
					return this.getPingTimeout();
				}
			}
			return Math.max(1, Math.toIntExact(pingResultTotal / this.getPingAttempts()));
		} else {
			throw new IllegalStateException("Cannot use device class without calling init() first");
		}
	}

	/**
	 * Constructs a new SolsticePodGen3Communicator instance.
	 *
	 * @throws IOException if an I/O error occurs.
	 */
	public SolsticePodGen3Communicator() throws IOException {
		super();
		this.setPort(this.getPort());
		Map<String, PropertiesMapping> mapping = new PropertiesMappingParser().loadYML(SolsticeConstant.FILE_MAPPING, getClass());
		aggregatedDeviceProcessor = new AggregatedDeviceProcessor(mapping);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Statistics> getMultipleStatistics() {
		reentrantLock.lock();
		try {
			adminPassword = this.getPassword();
			ExtendedStatistics extendedStatistics = new ExtendedStatistics();
			Map<String, String> stats = new HashMap<>();
			Map<String, String> controlStats = new HashMap<>();
			List<AdvancedControllableProperty> advancedControllableProperties = new ArrayList<>();
			if (!isEmergencyDelivery) {
				convertConfigManagement();
				retrieveMonitoringAndControllingData();
				populateMonitoringAndControllingData(stats, controlStats, advancedControllableProperties);
				if (isConfigManagement) {
					extendedStatistics.setControllableProperties(advancedControllableProperties);
					stats.putAll(controlStats);
				}
				extendedStatistics.setStatistics(stats);
				localExtendedStatistics = extendedStatistics;
			}
			isEmergencyDelivery = false;
		} finally {
			reentrantLock.unlock();
		}
		return Collections.singletonList(localExtendedStatistics);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void controlProperty(ControllableProperty controllableProperty) {

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void controlProperties(List<ControllableProperty> controllableProperties) {

	}

	@Override
	protected void authenticate() {

	}

	/**
	 * Retrieves monitoring and controlling data by sending GET requests to the Solstice Pod.
	 * The method retrieves configuration and statistics responses, extracts device information,
	 * updates the local cache of property name-value pairs, and populates missing properties with default values.
	 * If any error occurs during the process, the local cache is cleared.
	 */
	private void retrieveMonitoringAndControllingData() {
		try {
			JsonNode configResponse = sendGetRequest(SolsticeCommand.CONFIG_COMMAND);
			JsonNode statsResponse = sendGetRequest(SolsticeCommand.STATS_COMMAND);
			getAllAvailableTimeZones(configResponse);
			JsonNode node = objectMapper.createArrayNode().add(configResponse).add(statsResponse);
			for (AggregatedDevice item : aggregatedDeviceProcessor.extractDevices(node)) {
				localCacheMapOfPropertyNameAndValue.keySet().removeAll(item.getProperties().keySet());
				localCacheMapOfPropertyNameAndValue.putAll(item.getProperties());
			}
			for (SolsticePropertiesList i : SolsticePropertiesList.values()) {
				if (StringUtils.isNullOrEmpty(localCacheMapOfPropertyNameAndValue.get(i.getName()))) {
					localCacheMapOfPropertyNameAndValue.put(i.getName(), SolsticeConstant.NONE);
				}
			}
		} catch (Exception e) {
			logger.debug("Error while populate data: " + e);
			localCacheMapOfPropertyNameAndValue.clear();
			throw new ResourceNotReachableException("Admin password is not correct");
		}
	}

	/**
	 * populate monitoring and controlling data
	 *
	 * @param stats the stats are list of Statistics
	 * @param controlStats the control stats are list of Statistics
	 * @param advancedControllableProperties the advancedControllableProperties are list AdvancedControllableProperty instance
	 */
	private void populateMonitoringAndControllingData(Map<String, String> stats, Map<String, String> controlStats, List<AdvancedControllableProperty> advancedControllableProperties) {
		String value;
		String propertyName;
		for (SolsticePropertiesList property : SolsticePropertiesList.values()) {
			value = localCacheMapOfPropertyNameAndValue.get(property.getName());
			propertyName = property.getGroup().concat(property.getName());
			if (StringUtils.isNotNullOrEmpty(value)) {
				switch (property) {
					case REBOOT_DEVICE:
						addAdvanceControlProperties(advancedControllableProperties, controlStats,
								createButton(propertyName, SolsticeConstant.REBOOT, SolsticeConstant.REBOOTING, SolsticeConstant.GRACE_PERIOD));
						break;
					case RESTART_DEVICE:
					case RESET_KEY:
						addAdvanceControlProperties(advancedControllableProperties, controlStats, createButton(propertyName, SolsticeConstant.RESTART, SolsticeConstant.RESTARTING, SolsticeConstant.GRACE_PERIOD));
						break;
					case SET_DEFAULT_BACKGROUND:
						addAdvanceControlProperties(advancedControllableProperties, controlStats, createButton(propertyName, SolsticeConstant.SET, SolsticeConstant.SETTING, SolsticeConstant.GRACE_PERIOD));
						break;
					case SCHEDULED_DAILY_REBOOT:
					case BROADCAST_DISPLAY_NAME:
					case PUBLISH_DISPLAY_NAME:
					case DESKTOP_SCREEN_SHARING:
					case APPLICATION_WINDOW_SHARING:
					case ANDROID_MIRRORING:
					case IOS_MIRRORING:
					case VIDEO_FILES_AND_IMAGES:
					case USE_24_HOUR_TIME_FORMAT:
					case AUTO_SET_DATETIME:
						addAdvanceControlProperties(advancedControllableProperties, controlStats, createSwitch(propertyName, convertBooleanToNumber(value), SolsticeConstant.OFF, SolsticeConstant.ON));
						break;
					case DISPLAY_NAME_ON_MAIN_SCREEN:
					case DISPLAY_NAME_ON_PRESENCE_BAR:
					case HOST_IP_ADDRESS_ON_MAIN_SCREEN:
					case HOST_IP_ADDRESS_ON_PRESENCE_BAR:
					case SCREEN_KEY_ON_MAIN_SCREEN:
					case SCREEN_KEY_ON_PRESENCE_BAR:
						int status = getScreenStatus(value, property.getName());
						if (status != SolsticeConstant.INVALID_SCREEN_STATUS) {
							addAdvanceControlProperties(advancedControllableProperties, controlStats, createSwitch(propertyName, status, SolsticeConstant.OFF, SolsticeConstant.ON));
						}
						break;
					case AIRPLAY_DISCOVERY_PROXY:
						if (SolsticeConstant.TRUE.equals(localCacheMapOfPropertyNameAndValue.get(SolsticeConstant.IOS_MIRRORING))) {
							addAdvanceControlProperties(advancedControllableProperties, controlStats, createSwitch(propertyName, convertBooleanToNumber(value), SolsticeConstant.OFF, SolsticeConstant.ON));
						}
						break;
					case SCREEN_KEY:
					case MODERATOR_APPROVAL:
						addAdvanceControlProperties(advancedControllableProperties, controlStats, createSwitch(propertyName, convertBooleanToNumber(value), SolsticeConstant.DISABLE, SolsticeConstant.ENABLE));
						break;
					case REBOOT_TIME_OF_DAY:
						if (SolsticeConstant.TRUE.equals(localCacheMapOfPropertyNameAndValue.get(SolsticeConstant.SCHEDULE_DAILY_REBOOT))) {
							addAdvanceControlProperties(advancedControllableProperties, controlStats, createText(propertyName, value));
						}
						break;
					case DISPLAY_NAME:
					case MAX_CONNECTIONS:
					case MAX_POSTS:
						addAdvanceControlProperties(advancedControllableProperties, controlStats, createText(propertyName, value));
						break;
					case HDMI_OUTPUT_MODE:
						addAdvanceControlProperties(advancedControllableProperties, controlStats,
								createDropdown(propertyName, EnumTypeHandler.getEnumNames(HDMIOutputEnum.class), EnumTypeHandler.getNameByValue(HDMIOutputEnum.class, value)));
						break;
					case BROWSER_LOOK_IN:
						addAdvanceControlProperties(advancedControllableProperties, controlStats,
								createDropdown(propertyName, EnumTypeHandler.getEnumNames(BrowserLookInEnum.class), EnumTypeHandler.getNameByValue(BrowserLookInEnum.class, value)));
						break;
					case LANGUAGE:
						addAdvanceControlProperties(advancedControllableProperties, controlStats,
								createDropdown(propertyName, EnumTypeHandler.getEnumNames(LanguageEnum.class), EnumTypeHandler.getNameByValue(LanguageEnum.class, value)));
						break;
					case CLIENT_QUICK_CONNECT_ACTION:
						addAdvanceControlProperties(advancedControllableProperties, controlStats,
								createDropdown(propertyName, QuickConnectActionEnum.getAllNames(),
										QuickConnectActionEnum.getNameByValue(localCacheMapOfPropertyNameAndValue.get(SolsticeConstant.LAUNCH_CLIENT_AND_AUTO_CONNECT),
												localCacheMapOfPropertyNameAndValue.get(SolsticeConstant.LAUNCH_CLIENT_AND_AUTOMATICALLY_SDS))));
						break;
					case TIME_ZONE:
						String[] timeZoneVales = availableTimeZones.stream()
								.map(TimeZone::getName)
								.toArray(String[]::new);
						addAdvanceControlProperties(advancedControllableProperties, controlStats, createDropdown(propertyName, timeZoneVales, getTimeZoneNameById(value)));
						break;
					case AUTOMATICALLY_RESIZE_IMAGES:
						addAdvanceControlProperties(advancedControllableProperties, controlStats, createText(propertyName, calculateMPixels(value)));
						break;
					case LICENSE_STATUS:
						stats.put(propertyName, EnumTypeHandler.getNameByValue(LicenseStatusEnum.class, value));
						break;
					case DATE:
						String zone = getTimeZoneNameById(localCacheMapOfPropertyNameAndValue.get(SolsticeConstant.TIME_ZONE)).split(SolsticeConstant.COMMA)[0];
						stats.put(propertyName, getDateTimeFromFormattedString(value, SolsticeConstant.DATE_FORMAT, zone));
						break;
					case TIME:
						zone = getTimeZoneNameById(localCacheMapOfPropertyNameAndValue.get(SolsticeConstant.TIME_ZONE)).split(SolsticeConstant.COMMA)[0];
						String timeFormat = SolsticeConstant.TIME_12H_FORMAT;
						if (SolsticeConstant.TRUE.equals(localCacheMapOfPropertyNameAndValue.get(SolsticeConstant.USE_24_HOUR_TIME_FORMAT))) {
							timeFormat = SolsticeConstant.TIME_24H_FORMAT;
						}
						stats.put(propertyName, getDateTimeFromFormattedString(value, timeFormat, zone));
						break;
					case TIME_SINCE_LAST_CONNECTION_INITIALIZE:
						zone = getTimeZoneNameById(localCacheMapOfPropertyNameAndValue.get(SolsticeConstant.TIME_ZONE)).split(SolsticeConstant.COMMA)[0];
						stats.put(propertyName, getDateTimeFromFormattedString(value, SolsticeConstant.DATE_TIME_FORMAT, zone));
						break;
					default:
						stats.put(propertyName, value);
				}
			} else {
				if (!property.isControl()) {
					stats.put(propertyName, SolsticeConstant.NONE);
				}
			}
		}
	}

	/**
	 * Sends a GET request to the Solstice Pod with the specified command.
	 * If an admin password is set, it appends the password as a query parameter.
	 *
	 * @param command the command to be sent in the GET request
	 * @return the response received from the Solstice Pod as a JSON node
	 * @throws Exception if an error occurs during the GET request
	 */
	private JsonNode sendGetRequest(String command) throws Exception {
		String request = command;
		if (adminPassword != null) {
			request = command + "?password=" + adminPassword;
		}
		return this.doGet(request, JsonNode.class);
	}

	/**
	 * Retrieves and populates the list of available time zones based on the provided config response.
	 *
	 * @param configResponse the JSON response containing the configuration data
	 */
	private void getAllAvailableTimeZones(JsonNode configResponse) {
		availableTimeZones.clear();
		TimeZone timeZone;
		JsonNode timeZonesNode = configResponse.get("m_systemCuration").get("timeZones");
		for (JsonNode timeZoneNode : timeZonesNode) {
			timeZone = new TimeZone(timeZoneNode.get(SolsticeConstant.ID).asText(), timeZoneNode.get(SolsticeConstant.NAME).asText(), timeZoneNode.get(SolsticeConstant.OFFSET).asInt());
			availableTimeZones.add(timeZone);
		}
	}

	/**
	 * Retrieves the name of the time zone based on the provided time zone ID.
	 *
	 * @param id the ID of the time zone
	 * @return the name of the time zone corresponding to the given ID, or a default value if not found
	 */
	private String getTimeZoneNameById(String id) {
		return availableTimeZones.stream()
				.filter(tz -> tz.getId().equals(id))
				.findFirst().map(TimeZone::getName).orElse(SolsticeConstant.DEFAULT_TIMEZONE_NAME);
	}

	/**
	 * Converts a formatted string representation of a date and time to a different format and time zone.
	 *
	 * @param millis the string representation of the date and time in milliseconds
	 * @param format the desired format of the date and time
	 * @param zone the desired time zone for the conversion
	 * @return the converted date and time string in the specified format and time zone, or a default value if an error occurs
	 */
	private String getDateTimeFromFormattedString(String millis, String format, String zone) {
		try {
			Date date = new Date(Long.parseLong(millis));
			SimpleDateFormat dateFormat = new SimpleDateFormat(format);
			java.util.TimeZone timeZone = java.util.TimeZone.getTimeZone(zone);
			dateFormat.setTimeZone(timeZone);
			return dateFormat.format(date);
		} catch (Exception e) {
			logger.debug("Error while populate data: " + e);
			return SolsticeConstant.NONE;
		}
	}

	/**
	 * Calculates the number of megapixels from a given value.
	 *
	 * @param value the value representing the number of pixels
	 * @return the calculated number of megapixels as a string, or a default value if an error occurs
	 */
	private String calculateMPixels(String value) {
		try {
			return String.valueOf(Integer.parseInt(value) / SolsticeConstant.M_PIXELS);
		} catch (Exception e) {
			return SolsticeConstant.NONE;
		}
	}

	/**
	 * Gets the screen status based on the decimal value and the properties metric.
	 *
	 * @param valueDecimal the decimal value representing the screen status
	 * @param propertyName the property name indicating the screen status type
	 * @return the screen status as an integer: 1 for active, 0 for inactive
	 */
	private int getScreenStatus(String valueDecimal, String propertyName) {
		try {
			String binary = Long.toBinaryString(Long.parseLong(valueDecimal));
			int bit = Integer.parseInt(EnumTypeHandler.getValueByName(ScreenCustomizationEnum.class, propertyName));
			char firstBit = binary.charAt(binary.length() - bit);
			return Character.getNumericValue(firstBit);
		} catch (Exception e) {
			logger.debug("Error when convert decimal to binary " + e);
			return SolsticeConstant.INVALID_SCREEN_STATUS;
		}
	}

	/**
	 * Converts a boolean value represented as a string to a number.
	 *
	 * @param value the boolean value represented as a string
	 * @return 1 if the value is equal to "true", 0 if the value is equal to "false"
	 */
	private int convertBooleanToNumber(String value) {
		return SolsticeConstant.TRUE.equals(value) ? 1 : 0;
	}

	/**
	 * This method is used to validate input config management from user
	 */
	private void convertConfigManagement() {
		isConfigManagement = StringUtils.isNotNullOrEmpty(this.configManagement) && this.configManagement.equalsIgnoreCase("true");
	}

	/**
	 * Add advancedControllableProperties if advancedControllableProperties different empty
	 *
	 * @param advancedControllableProperties advancedControllableProperties is the list that store all controllable properties
	 * @param stats store all statistics
	 * @param property the property is item advancedControllableProperties
	 * @return String response
	 * @throws IllegalStateException when exception occur
	 */
	private void addAdvanceControlProperties(List<AdvancedControllableProperty> advancedControllableProperties, Map<String, String> stats, AdvancedControllableProperty property) {
		if (property != null) {
			for (AdvancedControllableProperty controllableProperty : advancedControllableProperties) {
				if (controllableProperty.getName().equals(property.getName())) {
					advancedControllableProperties.remove(controllableProperty);
					break;
				}
			}
			stats.put(property.getName(), SolsticeConstant.EMPTY);
			advancedControllableProperties.add(property);
		}
	}

	/**
	 * Create switch is control property for metric
	 *
	 * @param name the name of property
	 * @param status initial status (0|1)
	 * @return AdvancedControllableProperty switch instance
	 */
	private AdvancedControllableProperty createSwitch(String name, int status, String labelOff, String labelOn) {
		AdvancedControllableProperty.Switch toggle = new AdvancedControllableProperty.Switch();
		toggle.setLabelOff(labelOff);
		toggle.setLabelOn(labelOn);

		AdvancedControllableProperty advancedControllableProperty = new AdvancedControllableProperty();
		advancedControllableProperty.setName(name);
		advancedControllableProperty.setValue(status);
		advancedControllableProperty.setType(toggle);
		advancedControllableProperty.setTimestamp(new Date());

		return advancedControllableProperty;
	}

	/**
	 * Create a button.
	 *
	 * @param name name of the button
	 * @param label label of the button
	 * @param labelPressed label of the button after pressing it
	 * @param gracePeriod grace period of button
	 * @return This returns the instance of {@link AdvancedControllableProperty} type Button.
	 */
	private AdvancedControllableProperty createButton(String name, String label, String labelPressed, long gracePeriod) {
		AdvancedControllableProperty.Button button = new AdvancedControllableProperty.Button();
		button.setLabel(label);
		button.setLabelPressed(labelPressed);
		button.setGracePeriod(gracePeriod);
		return new AdvancedControllableProperty(name, new Date(), button, "");
	}

	/***
	 * Create dropdown advanced controllable property
	 *
	 * @param name the name of the control
	 * @param initialValue initial value of the control
	 * @return AdvancedControllableProperty dropdown instance
	 */
	private AdvancedControllableProperty createDropdown(String name, String[] values, String initialValue) {
		AdvancedControllableProperty.DropDown dropDown = new AdvancedControllableProperty.DropDown();
		dropDown.setOptions(values);
		dropDown.setLabels(values);

		return new AdvancedControllableProperty(name, new Date(), dropDown, initialValue);
	}

	/**
	 * Create text is control property for metric
	 *
	 * @param name the name of the property
	 * @param stringValue character string
	 * @return AdvancedControllableProperty Text instance
	 */
	private AdvancedControllableProperty createText(String name, String stringValue) {
		AdvancedControllableProperty.Text text = new AdvancedControllableProperty.Text();
		return new AdvancedControllableProperty(name, new Date(), text, stringValue);
	}
}
