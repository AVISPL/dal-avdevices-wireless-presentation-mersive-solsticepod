/*
 *  Copyright (c) 2023 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.avdevices.wirelesspresentation.mersive.solsticepodgen3;

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

public class SolsticePodGen3CommunicatorTest {
	private ExtendedStatistics extendedStatistic;
	private SolsticePodGen3Communicator solsticePodGen3Communicator;

	@BeforeEach
	void setUp() throws Exception {
		solsticePodGen3Communicator = new SolsticePodGen3Communicator();
		solsticePodGen3Communicator.setHost("10.25.55.100");
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

	@Test
	void testCommunicatorGetStatistic() throws Exception {
		extendedStatistic = (ExtendedStatistics) solsticePodGen3Communicator.getMultipleStatistics().get(0);
		List<AdvancedControllableProperty> advancedControllablePropertyList = extendedStatistic.getControllableProperties();
		Map<String, String> statistics = extendedStatistic.getStatistics();
		Assert.assertEquals(57, statistics.size());
	}

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

	@Test
	void testTextControl() throws Exception {
		solsticePodGen3Communicator.setConfigManagement("true");
		extendedStatistic = (ExtendedStatistics) solsticePodGen3Communicator.getMultipleStatistics().get(0);
		Map<String, String> statistics = extendedStatistic.getStatistics();
		List<AdvancedControllableProperty> advancedControllablePropertyList = extendedStatistic.getControllableProperties();

		String property = "RebootTimeCurrent";
		String value = "0";
		ControllableProperty controllableProperty = new ControllableProperty();
		controllableProperty.setProperty(property);
		controllableProperty.setValue(value);
		solsticePodGen3Communicator.controlProperty(controllableProperty);

		extendedStatistic = (ExtendedStatistics) solsticePodGen3Communicator.getMultipleStatistics().get(0);
		statistics = extendedStatistic.getStatistics();
		advancedControllablePropertyList = extendedStatistic.getControllableProperties();
	}

	@Test
	void testDropdownControl() throws Exception {
		solsticePodGen3Communicator.setConfigManagement("true");
		extendedStatistic = (ExtendedStatistics) solsticePodGen3Communicator.getMultipleStatistics().get(0);
		Map<String, String> statistics = extendedStatistic.getStatistics();

		String property = "TimeZone";
		String value = "GMT+7:00, Bangkok";
		ControllableProperty controllableProperty = new ControllableProperty();
		controllableProperty.setProperty(property);
		controllableProperty.setValue(value);
		solsticePodGen3Communicator.controlProperty(controllableProperty);

		extendedStatistic = (ExtendedStatistics) solsticePodGen3Communicator.getMultipleStatistics().get(0);
		statistics = extendedStatistic.getStatistics();
		Assertions.assertEquals(value, statistics.get(property));
	}
}
