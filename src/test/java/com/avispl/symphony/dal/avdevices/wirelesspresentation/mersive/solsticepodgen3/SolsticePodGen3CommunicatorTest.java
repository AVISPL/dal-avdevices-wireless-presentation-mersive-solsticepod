/*
 *  Copyright (c) 2023 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.avdevices.wirelesspresentation.mersive.solsticepodgen3;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.avispl.symphony.api.dal.dto.control.AdvancedControllableProperty;
import com.avispl.symphony.api.dal.dto.control.ControllableProperty;
import com.avispl.symphony.api.dal.dto.monitor.ExtendedStatistics;
import com.avispl.symphony.api.dal.error.ResourceNotReachableException;
import com.avispl.symphony.dal.avdevices.wirelesspresentation.mersive.solsticepodgen3.common.SolsticeConstant;

/**
 * SolsticePodGen3CommunicatorTest
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 6/8/2023
 * @since 1.0.0
 */
public class SolsticePodGen3CommunicatorTest {
	public ExtendedStatistics extendedStatistic;
	static SolsticePodGen3Communicator solsticePodGen3Communicator;

	@BeforeEach
	void setUp() throws Exception {
		solsticePodGen3Communicator = new SolsticePodGen3Communicator();
		solsticePodGen3Communicator.setHost("10.34.30.100");
		solsticePodGen3Communicator.setPassword("");
		solsticePodGen3Communicator.setPort(80);
		solsticePodGen3Communicator.init();
		solsticePodGen3Communicator.connect();
		solsticePodGen3Communicator.setConfigManagement("true");
	}

	@AfterEach
	void destroy() throws Exception {
		solsticePodGen3Communicator.disconnect();
	}

	/**
	 * Unit test for the `getStatistic()` method of the `solsticePodGen3Communicator` object.
	 * It verifies that the method returns an `ExtendedStatistics` object and checks the number
	 * of statistics returned from the `extendedStatistic`.
	 *
	 * @throws Exception If an exception occurs during the test execution.
	 */
	@Test
	void testCommunicatorGetStatistic() throws Exception {
		extendedStatistic = (ExtendedStatistics) solsticePodGen3Communicator.getMultipleStatistics().get(0);
		List<AdvancedControllableProperty> advancedControllablePropertyList = extendedStatistic.getControllableProperties();
		Map<String, String> statistics = extendedStatistic.getStatistics();
		Assert.assertEquals(54, statistics.size());
	}

	/**
	 * Test getMultipleStatistics with login failed password
	 *
	 * Expect failed login successfully
	 */
	@Test
	void testCommunicatorGetStatisticIsFailedLogin() throws Exception {
		solsticePodGen3Communicator.destroy();
		solsticePodGen3Communicator.setHost("10.34.30.100");
		solsticePodGen3Communicator.setPassword("aaa");
		solsticePodGen3Communicator.setPort(80);
		solsticePodGen3Communicator.init();
		solsticePodGen3Communicator.connect();
		solsticePodGen3Communicator.setConfigManagement("true");
		assertThrows(ResourceNotReachableException.class, () -> solsticePodGen3Communicator.getMultipleStatistics(), "Failed to login, please check the password");
	}

	/**
	 * Unit test for the `controlProperty` method of the `solsticePodGen3Communicator` object.
	 * It verifies that the `controlProperty` method sets the specified property with the given value,
	 * and the updated statistics are correctly reflected in the retrieved statistics.
	 *
	 * @throws Exception If an exception occurs during the test execution.
	 */
	@Test
	void testSwitchControl() throws Exception {
		solsticePodGen3Communicator.setConfigManagement("true");
		extendedStatistic = (ExtendedStatistics) solsticePodGen3Communicator.getMultipleStatistics().get(0);
		Map<String, String> statistics = extendedStatistic.getStatistics();

		String property = "Reboot";
		String value = "0";
		ControllableProperty controllableProperty = new ControllableProperty();
		controllableProperty.setProperty(property);
		controllableProperty.setValue(value);
		solsticePodGen3Communicator.controlProperty(controllableProperty);

		extendedStatistic = (ExtendedStatistics) solsticePodGen3Communicator.getMultipleStatistics().get(0);
		statistics = extendedStatistic.getStatistics();
		Assertions.assertEquals(value, statistics.get(property));
	}

	/**
	 * Unit test for the `controlProperty` method of the `solsticePodGen3Communicator` object, specifically for text-based properties.
	 * It verifies that the `controlProperty` method successfully sets the specified text-based property with the given value,
	 * and ensures that the updated statistics and controllable property list reflect the changes made.
	 *
	 * @throws Exception If an exception occurs during the test execution.
	 */
	@Test
	void testTextControl() throws Exception {
		solsticePodGen3Communicator.setConfigManagement("true");
		extendedStatistic = (ExtendedStatistics) solsticePodGen3Communicator.getMultipleStatistics().get(0);
		Map<String, String> statistics = extendedStatistic.getStatistics();
		List<AdvancedControllableProperty> advancedControllablePropertyList = extendedStatistic.getControllableProperties();

		String property = SolsticeConstant.RESOURCE_RESTRICTION_GROUP.concat("IOSMirroring");
		String value = "1";
		ControllableProperty controllableProperty = new ControllableProperty();
		controllableProperty.setProperty(property);
		controllableProperty.setValue(value);
		solsticePodGen3Communicator.controlProperty(controllableProperty);

		extendedStatistic = (ExtendedStatistics) solsticePodGen3Communicator.getMultipleStatistics().get(0);
		statistics = extendedStatistic.getStatistics();
		advancedControllablePropertyList = extendedStatistic.getControllableProperties();
		Assertions.assertEquals(value, statistics.get(property));
	}

	/**
	 * Unit test for the `controlProperty` method of the `solsticePodGen3Communicator` object, specifically for dropdown-based properties.
	 * It verifies that the `controlProperty` method successfully sets the specified dropdown-based property with the given value,
	 * and ensures that the updated statistics reflect the changes made.
	 *
	 * @throws Exception If an exception occurs during the test execution.
	 */
	@Test
	void testDropdownControl() throws Exception {
		solsticePodGen3Communicator.setConfigManagement("true");
		extendedStatistic = (ExtendedStatistics) solsticePodGen3Communicator.getMultipleStatistics().get(0);
		Map<String, String> statistics = extendedStatistic.getStatistics();
		List<AdvancedControllableProperty> advancedControllablePropertyList = extendedStatistic.getControllableProperties();

		String property = "Language";
		String value = "Japanese";
		ControllableProperty controllableProperty = new ControllableProperty();
		controllableProperty.setProperty(property);
		controllableProperty.setValue(value);
		solsticePodGen3Communicator.controlProperty(controllableProperty);

		extendedStatistic = (ExtendedStatistics) solsticePodGen3Communicator.getMultipleStatistics().get(0);
		statistics = extendedStatistic.getStatistics();
		Assertions.assertEquals(value, statistics.get(property));
	}
}
