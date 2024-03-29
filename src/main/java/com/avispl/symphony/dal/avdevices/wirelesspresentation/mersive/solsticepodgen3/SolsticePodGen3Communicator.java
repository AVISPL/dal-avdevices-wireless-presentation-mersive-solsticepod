/*
 *  Copyright (c) 2023 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.avdevices.wirelesspresentation.mersive.solsticepodgen3;

import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.IntStream;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import javax.security.auth.login.FailedLoginException;

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
import com.avispl.symphony.dal.avdevices.wirelesspresentation.mersive.solsticepodgen3.common.ActiveRoutingProperty;
import com.avispl.symphony.dal.avdevices.wirelesspresentation.mersive.solsticepodgen3.common.BrowserLookInEnum;
import com.avispl.symphony.dal.avdevices.wirelesspresentation.mersive.solsticepodgen3.common.EnumTypeHandler;
import com.avispl.symphony.dal.avdevices.wirelesspresentation.mersive.solsticepodgen3.common.HDMIOutputEnum;
import com.avispl.symphony.dal.avdevices.wirelesspresentation.mersive.solsticepodgen3.common.LanguageEnum;
import com.avispl.symphony.dal.avdevices.wirelesspresentation.mersive.solsticepodgen3.common.LicenseStatusEnum;
import com.avispl.symphony.dal.avdevices.wirelesspresentation.mersive.solsticepodgen3.common.PingMode;
import com.avispl.symphony.dal.avdevices.wirelesspresentation.mersive.solsticepodgen3.common.QuickConnectActionEnum;
import com.avispl.symphony.dal.avdevices.wirelesspresentation.mersive.solsticepodgen3.common.ScreenCustomizationEnum;
import com.avispl.symphony.dal.avdevices.wirelesspresentation.mersive.solsticepodgen3.common.SolsticeCommand;
import com.avispl.symphony.dal.avdevices.wirelesspresentation.mersive.solsticepodgen3.common.SolsticeConstant;
import com.avispl.symphony.dal.avdevices.wirelesspresentation.mersive.solsticepodgen3.common.SolsticePropertiesList;
import com.avispl.symphony.dal.avdevices.wirelesspresentation.mersive.solsticepodgen3.dto.TimeZone;
import com.avispl.symphony.dal.communicator.RestCommunicator;
import com.avispl.symphony.dal.util.StringUtils;

/**
 * Mersive Solstice Pod
 * Monitoring Aggregated Device:
 * <ul>
 * <li> - Date</li>
 * <li> - DeviceId</li>
 * <li> - DisplayId</li>
 * <li> - HostName</li>
 * <li> - IP</li>
 * <li> - Port</li>
 * <li> - ProductHardwareVersion</li>
 * <li> - ProductName</li>
 * <li> - ProductVariant</li>
 * <li> - ServerVersion</li>
 * <li> - Time</li>
 * <li> - TimeServer</li>
 * <li> - SessionKey</li>
 * <li> - ExpirationDate</li>
 * <li> - LicenseStatus</li>
 * <li> - NumDaysToExpiration</li>
 * <li> - SDSHost1</li>
 * <li> - SDSHost2</li>
 * <li> - ConnectedUsers</li>
 * <li> - CurrentBandwidth(Mbps)</li>
 * <li> - CurrentLiveSourceCount</li>
 * <li> - CurrentPostCount</li>
 * <li> - TimeSinceLastConnectionInitialize</li>
 * </ul>
 *
 * Controlling Aggregated Device:
 * <ul>
 * <li> - Language</li>
 * <li> - TimeZone</li>
 * <li> - Use24HourTimeFormat</li>
 * <li> - BrowserLookIn</li>
 * <li> - ModeratorApproval</li>
 * <li> - ResetKey</li>
 * <li> - ScreenKey</li>
 * <li> - BroadcastDisplayName</li>
 * <li> - DisplayName</li>
 * <li> - DisplayNameOnMainScreen</li>
 * <li> - DisplayNameOnPresenceBar</li>
 * <li> - HostIPAddressOnMainScreen</li>
 * <li> - HostIPAddressOnPresenceBar</li>
 * <li> - PublishDisplayName</li>
 * <li> - ScreenKeyOnMainScreen</li>
 * <li> - ScreenKeyOnPresenceBar</li>
 * <li> - Active</li>
 * <li> - Hour</li>
 * <li> - Minute</li>
 * <li> - AirPlayDiscoveryProxy</li>
 * <li> - AndroidMirroring</li>
 * <li> - ApplicationWindowSharing</li>
 * <li> - AutomaticallyResizeImages(MPixels)</li>
 * <li> - ClientQuickConnectAction</li>
 * <li> - DesktopScreenSharing</li>
 * <li> - IOSMirroring</li>
 * <li> - MaximumConnections</li>
 * <li> - MaximumPosts</li>
 * <li> - VideoFilesAndImages</li>
 * </ul>
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 6/8/2023
 * @since 1.0.0
 */
public class SolsticePodGen3Communicator extends RestCommunicator implements Monitorable, Controller {
	/**
	 * reentrantLock is a reentrant lock used for synchronization.
	 */
	private final ReentrantLock reentrantLock = new ReentrantLock();

	/**
	 * An instance of the AggregatedDeviceProcessor class used to process and aggregate device-related data.
	 */
	private final AggregatedDeviceProcessor aggregatedDeviceProcessor;

	/**
	 * Local cache stores data after a period of time
	 */
	private final Map<String, String> localCacheMapOfPropertyNameAndValue = new HashMap<>();

	/**
	 * availableTimeZones represents a list of available time zones.
	 */
	private final List<TimeZone> availableTimeZones = new ArrayList<>();

	/**
	 * ObjectMapper is a Jackson library object mapper used for JSON serialization and deserialization.
	 */
	private final ObjectMapper objectMapper = new ObjectMapper();

	/**
	 * Holds the JSON response for configuration data.
	 */
	private JsonNode configResponse;

	/**
	 * Holds the JSON response for statistical data.
	 */
	private JsonNode statisticResponse;

	/**
	 * include hour value array.
	 */
	private String[] hoursValueArray;

	/**
	 * include minute value array.
	 */
	private String[] minutesValueArray;

	/**
	 * count the failed command
	 */
	private final Map<String, String> failedMonitor = new HashMap<>();

	/**
	 * localExtendedStatistics represents the extended statistics object.
	 */
	private ExtendedStatistics localExtendedStatistics;

	/**
	 * isEmergencyDelivery indicates whether it is an emergency delivery.
	 */
	private boolean isEmergencyDelivery;

	/**
	 * configManagement imported from the user interface
	 */
	private String configManagement;

	/**
	 * configManagement in boolean value
	 */
	private boolean isConfigManagement;

	/**
	 * This name clearly indicates that the variable is determining whether the request should be URL encoded or not.
	 */
	private boolean isFormUrlEncodedRequest;

	/**
	 * token for each Active Routing request
	 */
	private String token;

	/**
	 * save time get token
	 */
	private Long tokenExpire;

	/**
	 * time the token expires
	 */
	private Long expiresIn = 3000L * 1000;

	/**
	 * ping Mode for the adapter
	 */
	private PingMode pingMode = PingMode.ICMP;

	/**
	 * Constructs a new SolsticePodGen3Communicator instance.
	 *
	 * @throws IOException if an I/O error occurs.
	 */
	public SolsticePodGen3Communicator() throws IOException {
		super();
		Map<String, PropertiesMapping> mapping = new PropertiesMappingParser().loadYML(SolsticeConstant.FILE_MAPPING, getClass());
		aggregatedDeviceProcessor = new AggregatedDeviceProcessor(mapping);
		this.setTrustAllCertificates(true);
	}

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
	 * Retrieves {@link #pingMode}
	 *
	 * @return value of {@link #pingMode}
	 */
	public String getPingMode() {
		return pingMode.name();
	}

	/**
	 * Sets {@link #pingMode} value
	 *
	 * @param pingMode new value of {@link #pingMode}
	 */
	public void setPingMode(String pingMode) {
		this.pingMode = PingMode.ofString(pingMode);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Statistics> getMultipleStatistics() throws Exception {
		reentrantLock.lock();
		try {
			ExtendedStatistics extendedStatistics = new ExtendedStatistics();
			Map<String, String> stats = new HashMap<>();
			Map<String, String> controlStats = new HashMap<>();
			List<AdvancedControllableProperty> advancedControllableProperties = new ArrayList<>();
			if (!isEmergencyDelivery) {
				convertConfigManagement();
				failedMonitor.clear();
				isFormUrlEncodedRequest = false;
				retrieveStatisticsCommand();
				retrieveConfigurationCommand();
				if (failedMonitor.size() == SolsticeConstant.NO_OF_MONITORING_COMMAND) {
					StringBuilder stringBuilder = new StringBuilder();
					stringBuilder.append(failedMonitor.get(SolsticeCommand.STATS_COMMAND)).append(failedMonitor.get(SolsticeCommand.CONFIG_COMMAND));
					throw new ResourceNotReachableException("Get monitoring data failed: " + stringBuilder);
				}
				retrieveAndPopulateActiveRouting(stats);
				updateLocalCaching();
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
	public void controlProperty(ControllableProperty controllableProperty) throws Exception {
		reentrantLock.lock();
		try {
			if (localExtendedStatistics == null) {
				return;
			}
			isEmergencyDelivery = true;
			Map<String, String> stats = this.localExtendedStatistics.getStatistics();
			List<AdvancedControllableProperty> advancedControllableProperties = this.localExtendedStatistics.getControllableProperties();
			String value = String.valueOf(controllableProperty.getValue());
			String property = controllableProperty.getProperty();

			String[] propertyList = property.split(SolsticeConstant.HASH);
			String propertyKey = property;
			if (property.contains(SolsticeConstant.HASH)) {
				propertyKey = propertyList[1];
			}
			SolsticePropertiesList propertyItem = SolsticePropertiesList.getByName(propertyKey);
			switch (propertyItem) {
				case SET_DEFAULT_BACKGROUND:
					sendCommandSetDefaultBackground();
					break;
				case RESET_KEY:
					sendCommandResetKey();
					stats.put(SolsticeConstant.ACCESS_CONTROL_GROUP + SolsticePropertiesList.KEY.getName(), getScreenKey());
					break;
				case USE_24_HOUR_TIME_FORMAT:
					boolean status = convertNumberToBoolean(value);
					sendPostRequest(String.format(SolsticeCommand.CONFIG_COMMAND, this.getHost()), propertyItem, status);
					updateCachedDeviceData(localCacheMapOfPropertyNameAndValue, propertyKey, String.valueOf(status));

					String zone = getTimeZoneNameById(localCacheMapOfPropertyNameAndValue.get(SolsticeConstant.TIME_ZONE)).split(SolsticeConstant.COMMA)[0];
					String timeFormat = SolsticeConstant.TIME_12H_FORMAT;
					if (status) {
						timeFormat = SolsticeConstant.TIME_24H_FORMAT;
					}
					stats.put(SolsticeConstant.TIME, getDateTimeFromFormattedString(localCacheMapOfPropertyNameAndValue.get(SolsticeConstant.TIME), timeFormat, zone));
					break;
				case IOS_MIRRORING:
					status = convertNumberToBoolean(value);
					sendPostRequest(String.format(SolsticeCommand.CONFIG_COMMAND, this.getHost()), propertyItem, status);
					updateCachedDeviceData(localCacheMapOfPropertyNameAndValue, propertyKey, String.valueOf(status));
					String name = SolsticeConstant.RESOURCE_RESTRICTION_GROUP.concat(SolsticeConstant.AIR_PLAY_DISCOVERY_PROXY);
					if (status) {
						addAdvanceControlProperties(advancedControllableProperties, stats,
								createSwitch(name, convertBooleanToNumber(localCacheMapOfPropertyNameAndValue.get(SolsticeConstant.AIR_PLAY_DISCOVERY_PROXY)),
										SolsticeConstant.OFF, SolsticeConstant.ON), value);
					} else {
						removeValueForTheControllableProperty(name, stats, advancedControllableProperties);
					}
					break;
				case SCHEDULED_DAILY_REBOOT:
					status = convertNumberToBoolean(value);
					sendPostRequest(String.format(SolsticeCommand.CONFIG_COMMAND, this.getHost()), propertyItem, status);
					updateCachedDeviceData(localCacheMapOfPropertyNameAndValue, propertyKey, String.valueOf(status));
					if (status) {
						if (minutesValueArray == null) {
							minutesValueArray = createArrayNumber(0, 59);
						}
						if (hoursValueArray == null) {
							hoursValueArray = createArrayNumber(0, 23);
						}
						addAdvanceControlProperties(advancedControllableProperties, stats,
								createDropdown(SolsticeConstant.REBOOT_SCHEDULING_GROUP.concat(SolsticeConstant.MINUTE), minutesValueArray, localCacheMapOfPropertyNameAndValue.get(SolsticeConstant.MINUTE)),
								value);
						addAdvanceControlProperties(advancedControllableProperties, stats,
								createDropdown(SolsticeConstant.REBOOT_SCHEDULING_GROUP.concat(SolsticeConstant.HOUR), hoursValueArray, localCacheMapOfPropertyNameAndValue.get(SolsticeConstant.HOUR)),
								value);
					} else {
						removeValueForTheControllableProperty(SolsticeConstant.REBOOT_SCHEDULING_GROUP.concat(SolsticeConstant.MINUTE), stats, advancedControllableProperties);
						removeValueForTheControllableProperty(SolsticeConstant.REBOOT_SCHEDULING_GROUP.concat(SolsticeConstant.HOUR), stats, advancedControllableProperties);
					}
					break;
				case SCREEN_KEY:
					status = convertNumberToBoolean(value);
					sendPostRequest(String.format(SolsticeCommand.CONFIG_COMMAND, this.getHost()), propertyItem, status);
					updateCachedDeviceData(localCacheMapOfPropertyNameAndValue, propertyKey, String.valueOf(status));
					if (SolsticeConstant.NUMBER_ONE.equals(value)) {
						stats.put(SolsticeConstant.ACCESS_CONTROL_GROUP + SolsticePropertiesList.KEY.getName(), getScreenKey());
						addAdvanceControlProperties(advancedControllableProperties, stats,
								createButton(SolsticeConstant.ACCESS_CONTROL_GROUP.concat(SolsticePropertiesList.RESET_KEY.getName()), SolsticeConstant.RESET, SolsticeConstant.RESETTING,
										SolsticeConstant.GRACE_PERIOD),
								value);
					} else {
						stats.remove(SolsticeConstant.ACCESS_CONTROL_GROUP + SolsticePropertiesList.KEY.getName());
						removeValueForTheControllableProperty(SolsticeConstant.ACCESS_CONTROL_GROUP.concat(SolsticePropertiesList.RESET_KEY.getName()), stats, advancedControllableProperties);
					}
					break;
				case DISABLE_MODERATOR_APPROVAL:
				case DESKTOP_SCREEN_SHARING:
				case APPLICATION_WINDOW_SHARING:
				case ANDROID_MIRRORING:
				case AIRPLAY_DISCOVERY_PROXY:
				case VIDEO_FILES_AND_IMAGES:
				case PUBLISH_DISPLAY_NAME:
				case BROADCAST_DISPLAY_NAME:
					status = convertNumberToBoolean(value);
					sendPostRequest(String.format(SolsticeCommand.CONFIG_COMMAND, this.getHost()), propertyItem, status);
					updateCachedDeviceData(localCacheMapOfPropertyNameAndValue, propertyKey, String.valueOf(status));
					break;
				case SCREEN_KEY_ON_MAIN_SCREEN:
				case SCREEN_KEY_ON_PRESENCE_BAR:
				case DISPLAY_NAME_ON_MAIN_SCREEN:
				case DISPLAY_NAME_ON_PRESENCE_BAR:
				case HOST_IP_ADDRESS_ON_MAIN_SCREEN:
				case HOST_IP_ADDRESS_ON_PRESENCE_BAR:
					status = convertNumberToBoolean(value);
					ScreenCustomizationEnum customizationEnum = ScreenCustomizationEnum.getEnumByName(propertyKey);
					long valueRequest = changeBit(Long.parseLong(localCacheMapOfPropertyNameAndValue.get(SolsticeConstant.SCREEN_CUSTOMIZATION)), status, Integer.parseInt(customizationEnum.getValue()));
					sendPostRequest(String.format(SolsticeCommand.CONFIG_COMMAND, this.getHost()), propertyItem, valueRequest);
					updateCachedDeviceData(localCacheMapOfPropertyNameAndValue, SolsticeConstant.SCREEN_CUSTOMIZATION, String.valueOf(valueRequest));
					break;
				case REBOOT_TIME_OF_DAY_HOUR:
					String minute = localCacheMapOfPropertyNameAndValue.get(SolsticeConstant.MINUTE);
					sendPostRequest(String.format(SolsticeCommand.CONFIG_COMMAND, this.getHost()), propertyItem, convertTo24hFormat(value, minute));
					updateCachedDeviceData(localCacheMapOfPropertyNameAndValue, propertyKey, value);
					break;
				case REBOOT_TIME_OF_DAY_MINUTE:
					String hour = localCacheMapOfPropertyNameAndValue.get(SolsticeConstant.HOUR);
					sendPostRequest(String.format(SolsticeCommand.CONFIG_COMMAND, this.getHost()), propertyItem, convertTo24hFormat(hour, value));
					updateCachedDeviceData(localCacheMapOfPropertyNameAndValue, propertyKey, value);
					break;
				case TIME_ZONE:
					String zoneId = getIdByTimeZoneName(value);
					sendPostRequest(String.format(SolsticeCommand.CONFIG_COMMAND, this.getHost()), propertyItem, zoneId);
					updateCachedDeviceData(localCacheMapOfPropertyNameAndValue, propertyKey, zoneId);

					zone = getTimeZoneNameById(zoneId).split(SolsticeConstant.COMMA)[0];
					timeFormat = SolsticeConstant.TIME_12H_FORMAT;
					if (SolsticeConstant.TRUE.equals(localCacheMapOfPropertyNameAndValue.get(SolsticeConstant.USE_24_HOUR_TIME_FORMAT))) {
						timeFormat = SolsticeConstant.TIME_24H_FORMAT;
					}
					stats.put(SolsticeConstant.TIME, getDateTimeFromFormattedString(localCacheMapOfPropertyNameAndValue.get(SolsticeConstant.TIME), timeFormat, zone));
					stats.put(SolsticeConstant.DATE, getDateTimeFromFormattedString(localCacheMapOfPropertyNameAndValue.get(SolsticeConstant.DATE), timeFormat, zone));
					break;
				case LANGUAGE:
					String languageId = EnumTypeHandler.getValueByName(LanguageEnum.class, value);
					sendPostRequest(String.format(SolsticeCommand.CONFIG_COMMAND, this.getHost()), propertyItem, languageId);
					updateCachedDeviceData(localCacheMapOfPropertyNameAndValue, propertyKey, languageId);

					retrieveConfigurationCommand();
					getAllAvailableTimeZones();
					String[] timeZoneVales = availableTimeZones.stream().map(TimeZone::getName).toArray(String[]::new);
					removeValueForTheControllableProperty(SolsticeConstant.TIME_ZONE, stats, advancedControllableProperties);
					addAdvanceControlProperties(advancedControllableProperties, stats,
							createDropdown(SolsticeConstant.TIME_ZONE, timeZoneVales, getTimeZoneNameById(localCacheMapOfPropertyNameAndValue.get(SolsticeConstant.TIME_ZONE))), value);
					break;
				case HDMI_OUTPUT_MODE:
					String number = EnumTypeHandler.getValueByName(HDMIOutputEnum.class, value);
					sendPostRequest(String.format(SolsticeCommand.CONFIG_COMMAND, this.getHost()), propertyItem, Integer.parseInt(number));
					updateCachedDeviceData(localCacheMapOfPropertyNameAndValue, propertyKey, number);
					break;
				case BROWSER_LOOK_IN:
					number = EnumTypeHandler.getValueByName(BrowserLookInEnum.class, value);
					sendPostRequest(String.format(SolsticeCommand.CONFIG_COMMAND, this.getHost()), propertyItem, Integer.parseInt(number));
					updateCachedDeviceData(localCacheMapOfPropertyNameAndValue, propertyKey, number);
					break;
				case MAX_CONNECTIONS:
					long newValue = checkValidInput(SolsticeConstant.MIN_CONNECTIONS, SolsticeConstant.MAX_CONNECTIONS, value);
					value = String.valueOf(newValue);
					sendPostRequest(String.format(SolsticeCommand.CONFIG_COMMAND, this.getHost()), propertyItem, newValue);
					updateCachedDeviceData(localCacheMapOfPropertyNameAndValue, propertyKey, value);
					break;
				case MAX_POSTS:
					newValue = checkValidInput(SolsticeConstant.MIN_POSTS, SolsticeConstant.MAX_POSTS, value);
					value = String.valueOf(newValue);
					sendPostRequest(String.format(SolsticeCommand.CONFIG_COMMAND, this.getHost()), propertyItem, newValue);
					updateCachedDeviceData(localCacheMapOfPropertyNameAndValue, propertyKey, value);
					break;
				case AUTOMATICALLY_RESIZE_IMAGES:
					long bytesValue = convertMPixelsToByte(value);
					value = String.valueOf(bytesValue);
					sendPostRequest(String.format(SolsticeCommand.CONFIG_COMMAND, this.getHost()), propertyItem, bytesValue);
					updateCachedDeviceData(localCacheMapOfPropertyNameAndValue, propertyKey, value);
					value = calculateMPixels(value);
					break;
				case DISPLAY_NAME:
					sendPostRequest(String.format(SolsticeCommand.CONFIG_COMMAND, this.getHost()), propertyItem, value);
					updateCachedDeviceData(localCacheMapOfPropertyNameAndValue, propertyKey, value);
					break;
				case CLIENT_QUICK_CONNECT_ACTION:
					boolean autoConnectOnClientLaunch = false;
					boolean autoSDSOnClientLaunch = false;
					ObjectNode valueNode = objectMapper.createObjectNode();
					ObjectNode rootNode = objectMapper.createObjectNode();
					if (this.getPassword() != null) {
						rootNode.put(SolsticeConstant.PASSWORD, this.getPassword());
					}

					if (SolsticeConstant.AUTO_CONNECT.equals(value)) {
						autoConnectOnClientLaunch = true;
					} else if (SolsticeConstant.AUTO_SDS.equals(value)) {
						autoSDSOnClientLaunch = true;
					}

					valueNode.put(SolsticeConstant.AUTO_CONNECT_ON_CLIENT_LAUNCH, autoConnectOnClientLaunch);
					valueNode.put(SolsticeConstant.AUTO_SDS_ON_CLIENT_LAUNCH, autoSDSOnClientLaunch);
					rootNode.put(SolsticeConstant.GENERAL_CURATION, valueNode);
					sendCommandClientQuickConnectAction(rootNode);
					updateCachedDeviceData(localCacheMapOfPropertyNameAndValue, SolsticeConstant.LAUNCH_CLIENT_AND_AUTO_CONNECT, String.valueOf(autoConnectOnClientLaunch));
					updateCachedDeviceData(localCacheMapOfPropertyNameAndValue, SolsticeConstant.LAUNCH_CLIENT_AND_AUTOMATICALLY_SDS, String.valueOf(autoSDSOnClientLaunch));
					break;
				default:
					logger.debug(String.format("Property name %s doesn't support", propertyKey));
			}
			updateValueForTheControllableProperty(property, value, stats, advancedControllableProperties);
		} finally {
			reentrantLock.unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void controlProperties(List<ControllableProperty> controllableProperties) throws Exception {
		if (CollectionUtils.isEmpty(controllableProperties)) {
			throw new IllegalArgumentException("ControllableProperties can not be null or empty");
		}
		for (ControllableProperty p : controllableProperties) {
			try {
				controlProperty(p);
			} catch (Exception e) {
				logger.error(String.format("Error when control property %s", p.getProperty()), e);
			}
		}
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
		if (this.pingMode == PingMode.ICMP) {
			return super.ping();
		} else if (this.pingMode == PingMode.TCP) {
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
						throw new RuntimeException("Socket connection timed out", tex);
					} catch (UnknownHostException ex) {
						throw new UnknownHostException(String.format("Connection timed out, UNKNOWN host %s", host));
					} catch (Exception e) {
						if (this.logger.isWarnEnabled()) {
							this.logger.warn(String.format("PING TIMEOUT: Connection to %s did not succeed, UNKNOWN ERROR %s: ", host, e.getMessage()));
						}
						return this.getPingTimeout();
					}
				}
				return Math.max(1, Math.toIntExact(pingResultTotal / this.getPingAttempts()));
			} else {
				throw new IllegalStateException("Cannot use device class without calling init() first");
			}
		} else {
			throw new IllegalArgumentException("Unknown PING Mode: " + pingMode);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void authenticate() {
		//Solstice Pod doesn't require API token.
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected HttpHeaders putExtraRequestHeaders(HttpMethod httpMethod, String uri, HttpHeaders headers) throws Exception {
		if (isFormUrlEncodedRequest) {
			headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
			headers.setBearerAuth(token);
		}
		return headers;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void internalInit() throws Exception {
		if (logger.isDebugEnabled()) {
			logger.debug("Internal init is called.");
		}
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
			availableTimeZones.clear();
		}
		isEmergencyDelivery = false;
		isConfigManagement = false;
		failedMonitor.clear();
		super.internalDestroy();
	}

	/**
	 * Retrieves monitoring and controlling data by sending GET requests to the Solstice Pod.
	 * The method retrieves configuration and statistics responses, extracts device information,
	 * updates the local cache of property name-value pairs, and populates missing properties with default values.
	 * If any error occurs during the process, the local cache is cleared.
	 */
	private void updateLocalCaching() {
		try {
			getAllAvailableTimeZones();
			JsonNode node = objectMapper.createArrayNode().add(configResponse).add(statisticResponse);
			localCacheMapOfPropertyNameAndValue.clear();
			for (AggregatedDevice item : aggregatedDeviceProcessor.extractDevices(node)) {
				localCacheMapOfPropertyNameAndValue.putAll(item.getProperties());
			}
			String value;
			for (SolsticePropertiesList property : SolsticePropertiesList.values()) {
				value = localCacheMapOfPropertyNameAndValue.get(property.getName());
				if (StringUtils.isNullOrEmpty(value)) {
					localCacheMapOfPropertyNameAndValue.put(property.getName(), SolsticeConstant.NONE);
					value = SolsticeConstant.NONE;
				}
				switch (property) {
					case REBOOT_TIME_OF_DAY_HOUR:
						localCacheMapOfPropertyNameAndValue.put(property.getName(), getHour(value));
						break;
					case REBOOT_TIME_OF_DAY_MINUTE:
						localCacheMapOfPropertyNameAndValue.put(property.getName(), getMinutes(value));
						break;
					default:
						localCacheMapOfPropertyNameAndValue.put(property.getName(), value);
				}
			}
		} catch (Exception e) {
			logger.error("Error while retrieve data", e);
			localCacheMapOfPropertyNameAndValue.clear();
		}
	}

	/**
	 * Retrieves the configuration command from the API and stores the JSON response in the 'configResponse' field.
	 * Throws Exception if login fails and logs other exceptions.
	 */
	private void retrieveConfigurationCommand() {
		try {
			String request = String.format(SolsticeCommand.CONFIG_COMMAND, this.getHost()) + (StringUtils.isNotNullOrEmpty(this.getPassword()) ? SolsticeConstant.PASSWORD_REQUEST_PARAM + this.getPassword()
					: SolsticeConstant.EMPTY);
			configResponse = doGet(request, JsonNode.class);
		} catch (Exception e) {
			failedMonitor.put(SolsticeCommand.CONFIG_COMMAND, e.getMessage());
			logger.error("Error when retrieve configuration command", e);
		}
	}

	/**
	 * Retrieves the statistics command from the API and stores the JSON response in the 'statisticResponse' field.
	 * Throws Exception if login fails and logs other exceptions.
	 */
	private void retrieveStatisticsCommand() {
		try {
			String request = String.format(SolsticeCommand.STATS_COMMAND, this.getHost()) + (StringUtils.isNotNullOrEmpty(this.getPassword()) ? SolsticeConstant.PASSWORD_REQUEST_PARAM + this.getPassword()
					: SolsticeConstant.EMPTY);
			statisticResponse = doGet(request, JsonNode.class);
		} catch (FailedLoginException e) {
			throw new ResourceNotReachableException("Failed to login, please check the password", e);
		} catch (Exception e) {
			failedMonitor.put(SolsticeCommand.STATS_COMMAND, e.getMessage());
			logger.error("Error when retrieve statistics command", e);
		}
	}

	/**
	 * Retrieves the session key from configuration and returns it as a string.
	 *
	 * @return The session key retrieved from configuration, or {@link SolsticeConstant#NONE} if it cannot be retrieved or encountered an error.
	 */
	private String getScreenKey() {
		try {
			retrieveConfigurationCommand();
			if (configResponse.has(SolsticeConstant.AUTHENTICATION_CURATION) && configResponse.get(SolsticeConstant.AUTHENTICATION_CURATION).has(SolsticeConstant.SESSION_KEY)) {
				return configResponse.get(SolsticeConstant.AUTHENTICATION_CURATION).get(SolsticeConstant.SESSION_KEY).asText();
			}
		} catch (Exception e) {
			logger.error("Error when retrieve configuration command", e);
		}
		return SolsticeConstant.NONE;
	}

	/**
	 * Retrieves and populates active routing information based on the provided statistics map.
	 *
	 * @param stats A map containing statistics to be populated with active routing information.
	 * @throws Exception If there's an error during the retrieval or population process.
	 */
	private void retrieveAndPopulateActiveRouting(Map<String, String> stats) throws Exception {
		isFormUrlEncodedRequest = true;
		if (!checkValidApiToken()) {
			throw new FailedLoginException("API Token cannot be null or empty, please enter valid password and username field.");
		}
		JsonNode currentSessionResponse = retrieveActiveRoutingAPI(SolsticeCommand.GET_CURRENT_SESSION_COMMAND);
		if (currentSessionResponse != null) {
			for (ActiveRoutingProperty item : ActiveRoutingProperty.getListByType(SolsticeConstant.SESSION_DATA)) {
				if (currentSessionResponse.has(item.getValue())) {
					stats.put(SolsticeConstant.ACTIVE_ROUTING_GROUP + item.getName(), getDefaultValueForNullData(currentSessionResponse.get(item.getValue()).asText()));
				}
			}
		}
		JsonNode licensingInfoResponse = retrieveActiveRoutingAPI(SolsticeCommand.GET_LICENSING_COMMAND);
		if (licensingInfoResponse != null) {
			for (ActiveRoutingProperty item : ActiveRoutingProperty.getListByType(SolsticeConstant.LICENSING)) {
				if (licensingInfoResponse.has(item.getValue())) {
					stats.put(SolsticeConstant.ACTIVE_ROUTING_GROUP + item.getName(), getDefaultValueForNullData(licensingInfoResponse.get(item.getValue()).asText()));
				}
			}
		}
		JsonNode connectionsResponse = retrieveActiveRoutingAPI(SolsticeCommand.GET_CONNECTIONS_COMMAND);
		if (connectionsResponse != null && connectionsResponse.has("connections")) {
			JsonNode connectionsNode = connectionsResponse.get("connections");
			if (connectionsNode != null && connectionsNode.isObject()) {
				int index = 1;
				for (JsonNode entry : connectionsNode) {
					String value = entry.get("name").asText() + " (" + entry.get("ip").asText() + ")";
					stats.put(SolsticeConstant.ACTIVE_ROUTING_GROUP + "Connection" + index, value);
					index++;
				}
			}
		}
		isFormUrlEncodedRequest = false;
	}

	/**
	 * Retrieves active routing information from an API endpoint.
	 *
	 * @param request The request string representing the API endpoint.
	 * @return A JsonNode object containing the active routing information retrieved from the API,
	 * or null if an error occurs during the retrieval process.
	 */
	private JsonNode retrieveActiveRoutingAPI(String request) {
		try {
			return this.doGet(request, JsonNode.class);
		} catch (Exception e) {
			logger.error("Error when retrieve Active Routing info with request " + request, e);
			return null;
		}
	}

	/**
	 * Check API token validation
	 * If the token expires, we send a request to get a new token
	 *
	 * @return boolean
	 */
	private boolean checkValidApiToken() throws Exception {
		if (StringUtils.isNullOrEmpty(token) || System.currentTimeMillis() - tokenExpire >= expiresIn) {
			token = getToken();
		}
		return StringUtils.isNotNullOrEmpty(token);
	}

	/**
	 * Retrieves a token using the provided username and password
	 *
	 * @return the token string
	 */
	private String getToken() throws Exception {
		String accessToken = SolsticeConstant.EMPTY;
		tokenExpire = System.currentTimeMillis();

		MultiValueMap<String, String> valueMap = new LinkedMultiValueMap<>();
		valueMap.add("grant_type", "password");
		valueMap.add("username", SolsticeConstant.EMPTY);
		valueMap.add("password", StringUtils.isNotNullOrEmpty(this.getPassword()) ? this.getPassword() : SolsticeConstant.EMPTY);
		try {
			JsonNode response = this.doPost(SolsticeCommand.AUTHENTICATION_COMMAND, valueMap, JsonNode.class);
			if (response != null && response.has(SolsticeConstant.ACCESS_TOKEN)) {
				accessToken = response.get(SolsticeConstant.ACCESS_TOKEN).asText();
			}
		} catch (Exception e) {
			throw new FailedLoginException("Failed to retrieve an access token for account with from username and password. Please username id and password");
		}
		return accessToken;
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
					case SET_DEFAULT_BACKGROUND:
						addAdvanceControlProperties(advancedControllableProperties, controlStats, createButton(propertyName, SolsticeConstant.RESET, SolsticeConstant.RESETTING, SolsticeConstant.GRACE_PERIOD),
								value);
						break;
					case RESET_KEY:
						if (SolsticeConstant.TRUE.equals(localCacheMapOfPropertyNameAndValue.get(SolsticePropertiesList.SCREEN_KEY.getName()))) {
							addAdvanceControlProperties(advancedControllableProperties, controlStats, createButton(propertyName, SolsticeConstant.RESET, SolsticeConstant.RESETTING, SolsticeConstant.GRACE_PERIOD),
									value);
						}
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
					case DISABLE_MODERATOR_APPROVAL:
					case SCREEN_KEY:
						addAdvanceControlProperties(advancedControllableProperties, controlStats, createSwitch(propertyName, convertBooleanToNumber(value), SolsticeConstant.OFF, SolsticeConstant.ON), value);
						break;
					case DISPLAY_NAME_ON_MAIN_SCREEN:
					case DISPLAY_NAME_ON_PRESENCE_BAR:
					case HOST_IP_ADDRESS_ON_MAIN_SCREEN:
					case HOST_IP_ADDRESS_ON_PRESENCE_BAR:
					case SCREEN_KEY_ON_MAIN_SCREEN:
					case SCREEN_KEY_ON_PRESENCE_BAR:
						value = localCacheMapOfPropertyNameAndValue.get(SolsticeConstant.SCREEN_CUSTOMIZATION);
						int status = getScreenStatus(value, property.getName());
						if (status != SolsticeConstant.INVALID_SCREEN_STATUS) {
							addAdvanceControlProperties(advancedControllableProperties, controlStats, createSwitch(propertyName, status, SolsticeConstant.OFF, SolsticeConstant.ON), value);
						}
						break;
					case AIRPLAY_DISCOVERY_PROXY:
						if (SolsticeConstant.TRUE.equals(localCacheMapOfPropertyNameAndValue.get(SolsticeConstant.IOS_MIRRORING))) {
							addAdvanceControlProperties(advancedControllableProperties, controlStats, createSwitch(propertyName, convertBooleanToNumber(value), SolsticeConstant.OFF, SolsticeConstant.ON), value);
						}
						break;
					case REBOOT_TIME_OF_DAY_MINUTE:
						if (SolsticeConstant.TRUE.equals(localCacheMapOfPropertyNameAndValue.get(SolsticeConstant.ACTIVE))) {
							if (minutesValueArray == null) {
								minutesValueArray = createArrayNumber(0, 59);
							}
							addAdvanceControlProperties(advancedControllableProperties, controlStats, createDropdown(propertyName, minutesValueArray, value), value);
						}
						break;
					case REBOOT_TIME_OF_DAY_HOUR:
						if (SolsticeConstant.TRUE.equals(localCacheMapOfPropertyNameAndValue.get(SolsticeConstant.ACTIVE))) {
							if (hoursValueArray == null) {
								hoursValueArray = createArrayNumber(0, 23);
							}
							addAdvanceControlProperties(advancedControllableProperties, controlStats, createDropdown(propertyName, hoursValueArray, value), value);
						}
						break;
					case DISPLAY_NAME:
						addAdvanceControlProperties(advancedControllableProperties, controlStats, createText(propertyName, value), value);
						break;
					case MAX_CONNECTIONS:
					case MAX_POSTS:
						addAdvanceControlProperties(advancedControllableProperties, controlStats, createNumeric(propertyName, value), value);
						break;
					case HDMI_OUTPUT_MODE:
						addAdvanceControlProperties(advancedControllableProperties, controlStats,
								createDropdown(propertyName, EnumTypeHandler.getEnumNames(HDMIOutputEnum.class), EnumTypeHandler.getNameByValue(HDMIOutputEnum.class, value)), value);
						break;
					case BROWSER_LOOK_IN:
						addAdvanceControlProperties(advancedControllableProperties, controlStats,
								createDropdown(propertyName, EnumTypeHandler.getEnumNames(BrowserLookInEnum.class), EnumTypeHandler.getNameByValue(BrowserLookInEnum.class, value)), value);
						break;
					case LANGUAGE:
						addAdvanceControlProperties(advancedControllableProperties, controlStats,
								createDropdown(propertyName, EnumTypeHandler.getEnumNames(LanguageEnum.class), EnumTypeHandler.getNameByValue(LanguageEnum.class, value)), value);
						break;
					case CLIENT_QUICK_CONNECT_ACTION:
						addAdvanceControlProperties(advancedControllableProperties, controlStats,
								createDropdown(propertyName, EnumTypeHandler.getEnumNames(QuickConnectActionEnum.class),
										QuickConnectActionEnum.getNameByValue(localCacheMapOfPropertyNameAndValue.get(SolsticeConstant.LAUNCH_CLIENT_AND_AUTO_CONNECT),
												localCacheMapOfPropertyNameAndValue.get(SolsticeConstant.LAUNCH_CLIENT_AND_AUTOMATICALLY_SDS))), value);
						break;
					case TIME_ZONE:
						String[] timeZoneVales = availableTimeZones.stream().map(TimeZone::getName).toArray(String[]::new);
						addAdvanceControlProperties(advancedControllableProperties, controlStats, createDropdown(propertyName, timeZoneVales, getTimeZoneNameById(value)), value);
						break;
					case AUTOMATICALLY_RESIZE_IMAGES:
						addAdvanceControlProperties(advancedControllableProperties, controlStats, createNumeric(propertyName, calculateMPixels(value)), calculateMPixels(value));
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
						stats.put(propertyName, SolsticeConstant.NONE);
						if (Math.abs(System.currentTimeMillis() - Long.parseLong(value)) > SolsticeConstant.NUM_OF_MILLISECONDS_IN_HOUR) {
							stats.put(propertyName, convertTime(value));
						}
						break;
					case SERVER_VERSION:
						stats.put(propertyName, cutStringBeforeSecondDot(value));
						break;
					case KEY:
						if (SolsticeConstant.TRUE.equals(localCacheMapOfPropertyNameAndValue.get(SolsticePropertiesList.SCREEN_KEY.getName()))) {
							stats.put(propertyName, value);
						}
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
	 * Sends a POST request to the Solstice Pod with the specified command, property, and value.
	 *
	 * @param command the command to be sent in the POST request
	 * @param property the Solstice property associated with the value
	 * @param value the value to be sent in the request body
	 * @return the response received from the Solstice Pod as a JSON node
	 * @throws Exception if an error occurs during the POST request
	 */
	private void sendPostRequest(String command, SolsticePropertiesList property, Object value) {
		try {
			JsonNode response = this.doPost(command, createBodyRequest(property.getApiGroupName(), property.getApiPropertyName(), value), JsonNode.class);
			if (response.has(SolsticeConstant.ERROR)) {
				throw new IllegalArgumentException(
						String.format("Can't control property %s with value %s. The device has responded with an error: %s", property.name(), value, response.get(SolsticeConstant.ERROR).asText()));
			}
		} catch (Exception e) {
			throw new IllegalArgumentException(String.format("Can't control property %s with value %s. The device has responded with an error.", property.name(), value), e);
		}
	}

	/**
	 * Sends a command to set the default background to the device.
	 * If an admin password is provided, it includes the password in the request.
	 *
	 * @throws IllegalArgumentException if the device responds with an error or if there is an issue with the request.
	 */
	private void sendCommandSetDefaultBackground() {
		try {
			Map<String, String> params = new HashMap<>();
			params.put(SolsticeConstant.FILE, SolsticeConstant.RESET_DEFAULT);
			if (StringUtils.isNotNullOrEmpty(this.getPassword())) {
				params.put(SolsticeConstant.PASSWORD, this.getPassword());
			}
			JsonNode response = this.doPost(String.format(SolsticeCommand.SET_DEFAULT_BACKGROUND, this.getHost()), params, JsonNode.class);
			if (!isSuccessResponse(response)) {
				throw new IllegalArgumentException("Can't control property SetDefaultBackground. The device has responded with an error.");
			}
		} catch (Exception e) {
			throw new IllegalArgumentException("Can't control property SetDefaultBackground. The device has responded with an error.", e);
		}
	}

	/**
	 * Sends a command to reset the key to the device.
	 * If an admin password is provided, it includes the password in the request.
	 *
	 * @throws IllegalArgumentException if the device responds with an error or if there is an issue with the request.
	 */
	private void sendCommandResetKey() {
		try {
			String request = String.format(SolsticeCommand.RESET_KEY, this.getHost()) + (this.getPassword() != null ? "?password=" + this.getPassword() : SolsticeConstant.EMPTY);
			String response = this.doGet(request);
			if (!SolsticeConstant.COMMAND_SUCCESSFUL.equals(response)) {
				throw new IllegalArgumentException("The device has responded with an error.");
			}
		} catch (Exception e) {
			throw new IllegalArgumentException("Can't control property ResetKey %s" + e.getMessage(), e);
		}
	}

	/**
	 * Sends a quick connect action command to the client using the provided 'rootNode'.
	 * This method sends a POST request with the JSON 'rootNode' to the API endpoint representing the quick connect action command.
	 *
	 * @param rootNode The JSON 'rootNode' containing the quick connect action command details.
	 * @throws IllegalArgumentException If the device responds with an error message.
	 */
	private void sendCommandClientQuickConnectAction(JsonNode rootNode) {
		try {
			JsonNode response = this.doPost(String.format(SolsticeCommand.CONFIG_COMMAND, this.getHost()), rootNode, JsonNode.class);
			if (response.has(SolsticeConstant.ERROR)) {
				throw new IllegalArgumentException(
						String.format("The device has responded with an error: %s", response.get(SolsticeConstant.ERROR).asText()));
			}
		} catch (Exception e) {
			throw new IllegalArgumentException(String.format("Error when control property Client Quick Connect Action: %s", e.getMessage()), e);
		}
	}

	/**
	 * Checks if the provided JSON response represents a successful response.
	 *
	 * @param response The JSON response to be checked.
	 * @return true if the response is successful, false otherwise.
	 */
	private boolean isSuccessResponse(JsonNode response) {
		return response.has(SolsticeConstant.RESULT) && SolsticeConstant.SUCCESS.equals(response.get(SolsticeConstant.RESULT).asText());
	}

	/**
	 * Creates the request body for a POST request based on the specified group name, property name, and value.
	 *
	 * @param groupName the name of the group associated with the property
	 * @param propertyName the name of the property
	 * @param value the value of the property
	 * @return the JSON string representing the request body
	 */
	private String createBodyRequest(String groupName, String propertyName, Object value) {
		ObjectNode valueNode = objectMapper.createObjectNode();
		switch (value.getClass().getSimpleName()) {
			case SolsticeConstant.LONG:
				valueNode.put(propertyName, Long.parseLong(value.toString()));
				break;
			case SolsticeConstant.INTEGER:
				valueNode.put(propertyName, Integer.parseInt(value.toString()));
				break;
			case SolsticeConstant.BOOLEAN:
				valueNode.put(propertyName, Boolean.valueOf(value.toString()));
				break;
			default:
				valueNode.put(propertyName, value.toString());
				break;
		}
		ObjectNode rootNode = objectMapper.createObjectNode();
		if (this.getPassword() != null) {
			rootNode.put(SolsticeConstant.PASSWORD, this.getPassword());
		}
		rootNode.put(groupName, valueNode);
		return rootNode.toString();
	}

	/**
	 * Retrieves and populates the list of available time zones based on the provided config response.
	 */
	private void getAllAvailableTimeZones() {
		availableTimeZones.clear();
		TimeZone timeZone;
		JsonNode timeZonesNode = configResponse.get(SolsticeConstant.SYSTEM_CURATION).get(SolsticeConstant.TIME_ZONES);
		if (configResponse.has(SolsticeConstant.SYSTEM_CURATION) && configResponse.get(SolsticeConstant.SYSTEM_CURATION).has(SolsticeConstant.TIME_ZONES)) {
			for (JsonNode timeZoneNode : timeZonesNode) {
				if (timeZoneNode.has(SolsticeConstant.ID) && timeZoneNode.has(SolsticeConstant.NAME) && timeZoneNode.has(SolsticeConstant.OFFSET)) {
					timeZone = new TimeZone(timeZoneNode.get(SolsticeConstant.ID).asText(), timeZoneNode.get(SolsticeConstant.NAME).asText(), timeZoneNode.get(SolsticeConstant.OFFSET).asInt());
					availableTimeZones.add(timeZone);
				}
			}
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
	 * Retrieves the ID of the time zone based on the provided time zone name.
	 *
	 * @param name the name of the time zone
	 * @return the ID of the time zone corresponding to the given name, or a default value if not found
	 */
	private String getIdByTimeZoneName(String name) {
		return availableTimeZones.stream()
				.filter(tz -> tz.getName().equals(name))
				.findFirst().map(TimeZone::getId).orElse(SolsticeConstant.DEFAULT_TIMEZONE_ID);
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
		} catch (NumberFormatException e) {
			logger.error("Error while parsing millis: " + millis, e);
			return SolsticeConstant.NONE;
		} catch (IllegalArgumentException e) {
			logger.error("Error while formatting date: " + e.getMessage(), e);
			return SolsticeConstant.NONE;
		}
	}

	/**
	 * Converts a time value from milliseconds to a human-readable format.
	 *
	 * @param value The time value in milliseconds.
	 * @return A string representing the time value in the format: ? day(s) ? hour(s) ? minute(s) ? second(s).
	 */
	private String convertTime(String value) {
		try {
			long milliseconds = Long.parseLong(value);
			long seconds = milliseconds / 1000;
			long minutes = seconds / 60;
			long hours = minutes / 60;
			long days = hours / 24;
			seconds %= 60;
			minutes %= 60;
			hours %= 24;

			StringBuilder result = new StringBuilder();

			if (days > 0) {
				result.append(days).append(" day(s) ");
			}
			if (hours > 0) {
				result.append(hours).append(" hour(s) ");
			}
			if (minutes > 0) {
				result.append(minutes).append(" minute(s) ");
			}
			if (seconds > 0) {
				result.append(seconds).append(" second(s)");
			}

			return result.length() == 0 ? "0 seconds" : result.toString();
		} catch (Exception e) {
			logger.error("Error while formatting date: " + e.getMessage(), e);
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
	 * Converts a measurement value in "mPixels" unit to "byte".
	 *
	 * @param value The measurement value in "mPixels" unit to be converted.
	 * @return The converted value in "byte" unit.
	 * @throws IllegalArgumentException if the input value is invalid.
	 */
	private long convertMPixelsToByte(String value) {
		if (value.contains(SolsticeConstant.DOT)) {
			value = value.split(SolsticeConstant.DOT_REGEX)[0];
		}
		long initial = SolsticeConstant.MIN_RESIZE_IMAGES;
		try {
			long valueCompare = Long.parseLong(value);
			if (SolsticeConstant.MIN_RESIZE_IMAGES <= valueCompare && valueCompare <= SolsticeConstant.MAX_RESIZE_IMAGES) {
				return valueCompare * SolsticeConstant.M_PIXELS;
			}
			if (valueCompare > SolsticeConstant.MAX_RESIZE_IMAGES) {
				initial = SolsticeConstant.MAX_RESIZE_IMAGES;
			}
		} catch (Exception e) {
			if (!value.contains(SolsticeConstant.DASH)) {
				initial = SolsticeConstant.MAX_RESIZE_IMAGES;
			}
		}
		return initial * SolsticeConstant.M_PIXELS;
	}

	/**
	 * Checks if the input value is valid and converts it to an integer.
	 *
	 * @param value The input value to be checked and converted to an integer.
	 * @param min is the minimum value
	 * @param max is the maximum value
	 * @return The converted integer value if the input is valid.
	 * @throws IllegalArgumentException if the input value is not a valid integer.
	 */
	private long checkValidInput(int min, int max, String value) {
		if (value.contains(SolsticeConstant.DOT)) {
			value = value.split(SolsticeConstant.DOT_REGEX)[0];
		}
		long initial = min;
		try {
			long valueCompare = Long.parseLong(value);
			if (min <= valueCompare && valueCompare <= max) {
				return valueCompare;
			}
			if (valueCompare > max) {
				initial = max;
			}
		} catch (Exception e) {
			if (!value.contains(SolsticeConstant.DASH)) {
				initial = max;
			}
		}
		return initial;
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
			logger.error("Error when convert decimal to binary.", e);
			return SolsticeConstant.INVALID_SCREEN_STATUS;
		}
	}

	/**
	 * Changes the status of a specific bit in a given number.
	 *
	 * @param number The original number in which the bit will be changed.
	 * @param status The new status for the bit (true for 1, false for 0).
	 * @param bitIndex The index of the bit to be changed (1-based index from the right).
	 * @return The updated number with the specified bit changed according to the given status.
	 */
	private long changeBit(long number, boolean status, int bitIndex) {
		int mask = 1 << (bitIndex - 1);
		if (status) {
			// Set the bit at the specified index to 1
			return number | mask;
		} else {
			// Set the bit at the specified index to 0
			return number & ~mask;
		}
	}

	/**
	 * Creates an array of formatted numbers within the specified range.
	 *
	 * This method generates an array of strings representing numbers within the given range [min, max].
	 * The numbers in the range are formatted using the specified format defined by `SolsticeConstant.NUMBER_FORMAT`.
	 *
	 * @param min The minimum value of the range (inclusive).
	 * @param max The maximum value of the range (inclusive).
	 * @return An array of strings containing formatted numbers within the specified range.
	 */
	private String[] createArrayNumber(int min, int max) {
		return IntStream.rangeClosed(min, max)
				.mapToObj(minute -> String.format(SolsticeConstant.NUMBER_FORMAT, minute))
				.toArray(String[]::new);
	}

	/**
	 * Cuts the string before the second dot (.) occurrence.
	 *
	 * @param inputString The input string.
	 * @return The portion of the input string before the second dot, or the original string if no second dot is found.
	 */
	private String cutStringBeforeSecondDot(String inputString) {
		int firstDotIndex = inputString.indexOf(SolsticeConstant.DOT);
		if (firstDotIndex != -1) {
			int secondDotIndex = inputString.indexOf(SolsticeConstant.DOT, firstDotIndex + 1);
			if (secondDotIndex != -1) {
				return inputString.substring(0, secondDotIndex);
			}
		}
		return inputString;
	}

	/**
	 * Extracts the hour part from a time string in 24-hour format.
	 *
	 * @param timeString The time string in 24-hour format (e.g., "13:45").
	 * @return The extracted hour as a formatted string with two digits (e.g., "13").
	 */
	private String getHour(String timeString) {
		LocalTime localTime = LocalTime.parse(timeString, DateTimeFormatter.ofPattern(SolsticeConstant.TIME_24H_FORMAT));
		return String.format(SolsticeConstant.NUMBER_FORMAT, localTime.getHour());
	}

	/**
	 * Extracts the minutes part from a time string in 24-hour format.
	 *
	 * @param timeString The time string in 24-hour format (e.g., "13:45").
	 * @return The extracted minutes as a formatted string with two digits (e.g., "45").
	 */
	private String getMinutes(String timeString) {
		LocalTime localTime = LocalTime.parse(timeString, DateTimeFormatter.ofPattern(SolsticeConstant.TIME_24H_FORMAT));
		return String.format(SolsticeConstant.NUMBER_FORMAT, localTime.getMinute());
	}

	/**
	 * Converts hour and minutes values to a time string in 24-hour format.
	 *
	 * @param hourValue The hour value as a string (e.g., "13").
	 * @param minutesValue The minutes value as a string (e.g., "45").
	 * @return The time string in 24-hour format (e.g., "13:45").
	 * @throws Exception if the input hour or minutes value is not a valid integer.
	 */
	private String convertTo24hFormat(String hourValue, String minutesValue) {
		try {
			int hour = Integer.parseInt(hourValue);
			int minutes = Integer.parseInt(minutesValue);

			LocalTime localTime = LocalTime.of(hour, minutes);
			return localTime.format(DateTimeFormatter.ofPattern(SolsticeConstant.TIME_24H_FORMAT));
		} catch (Exception e) {
			throw new IllegalArgumentException("Can't control property. The value is invalid.", e);
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
	 * Converts a number represented as a string to a boolean value.
	 *
	 * @param value the number represented as a string
	 * @return true if the value is equal to "1", false otherwise
	 */
	private boolean convertNumberToBoolean(String value) {
		return SolsticeConstant.NUMBER_ONE.equals(value);
	}

	/**
	 * check value is null or empty
	 *
	 * @param value input value
	 * @return value after checking
	 */
	private String getDefaultValueForNullData(String value) {
		return StringUtils.isNotNullOrEmpty(value) ? uppercaseFirstCharacter(value) : SolsticeConstant.NONE;
	}

	/**
	 * capitalize the first character of the string
	 *
	 * @param input input string
	 * @return string after fix
	 */
	private String uppercaseFirstCharacter(String input) {
		char firstChar = input.charAt(0);
		return Character.toUpperCase(firstChar) + input.substring(1);
	}

	/**
	 * This method is used to validate input config management from user
	 */
	private void convertConfigManagement() {
		isConfigManagement = StringUtils.isNotNullOrEmpty(this.configManagement) && this.configManagement.equalsIgnoreCase(SolsticeConstant.TRUE);
	}

	/**
	 * Update cache device data
	 *
	 * @param cacheMapOfPropertyNameAndValue the cacheMapOfPropertyNameAndValue are map key and value of it
	 * @param property the key is property name
	 * @param value the value is String value
	 */
	private void updateCachedDeviceData(Map<String, String> cacheMapOfPropertyNameAndValue, String property, String value) {
		cacheMapOfPropertyNameAndValue.remove(property);
		cacheMapOfPropertyNameAndValue.put(property, value);
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
	private void addAdvanceControlProperties(List<AdvancedControllableProperty> advancedControllableProperties, Map<String, String> stats, AdvancedControllableProperty property, String value) {
		if (property != null) {
			for (AdvancedControllableProperty controllableProperty : advancedControllableProperties) {
				if (controllableProperty.getName().equals(property.getName())) {
					advancedControllableProperties.remove(controllableProperty);
					break;
				}
			}
			if (StringUtils.isNotNullOrEmpty(value)) {
				stats.put(property.getName(), value);
			} else {
				stats.put(property.getName(), SolsticeConstant.EMPTY);
			}
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

	/**
	 * Create numeric is control property for metric
	 *
	 * @param name the name of the property
	 * @param stringValue character string
	 * @return AdvancedControllableProperty Text instance
	 */
	private AdvancedControllableProperty createNumeric(String name, String stringValue) {
		AdvancedControllableProperty.Numeric text = new AdvancedControllableProperty.Numeric();
		return new AdvancedControllableProperty(name, new Date(), text, stringValue);
	}

	/**
	 * Update the value for the control metric
	 *
	 * @param property is name of the metric
	 * @param value the value is value of properties
	 * @param extendedStatistics list statistics property
	 * @param advancedControllableProperties the advancedControllableProperties is list AdvancedControllableProperties
	 */
	private void updateValueForTheControllableProperty(String property, String value, Map<String, String> extendedStatistics, List<AdvancedControllableProperty> advancedControllableProperties) {
		if (!advancedControllableProperties.isEmpty()) {
			for (AdvancedControllableProperty advancedControllableProperty : advancedControllableProperties) {
				if (advancedControllableProperty.getName().equals(property)) {
					extendedStatistics.remove(property);
					extendedStatistics.put(property, value);
					advancedControllableProperty.setValue(value);
					break;
				}
			}
		}
	}

	/**
	 * Remove the value for the control metric
	 *
	 * @param property is name of the metric
	 * @param extendedStatistics list statistics property
	 * @param advancedControllableProperties the advancedControllableProperties is list AdvancedControllableProperties
	 */
	private void removeValueForTheControllableProperty(String property, Map<String, String> extendedStatistics, List<AdvancedControllableProperty> advancedControllableProperties) {
		if (!advancedControllableProperties.isEmpty()) {
			for (AdvancedControllableProperty advancedControllableProperty : advancedControllableProperties) {
				if (advancedControllableProperty.getName().equals(property)) {
					extendedStatistics.remove(property);
					advancedControllableProperties.remove(advancedControllableProperty);
					break;
				}
			}
		}
	}
}
