/*
 *  Copyright (c) 2023 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.avdevices.wirelesspresentation.mersive.solsticepodgen3;

import java.util.List;

import com.avispl.symphony.api.dal.control.Controller;
import com.avispl.symphony.api.dal.dto.control.ControllableProperty;
import com.avispl.symphony.api.dal.dto.monitor.Statistics;
import com.avispl.symphony.api.dal.monitor.Monitorable;
import com.avispl.symphony.dal.communicator.RestCommunicator;

/**
 * SolsticePodGen3Communicator
 *
 * @author Kevin / Symphony Dev Team<br>
 * Created on 6/8/2023
 * @since 1.0.0
 */
public class SolsticePodGen3Communicator extends RestCommunicator implements Monitorable, Controller {
	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Statistics> getMultipleStatistics() {
		return null;
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
}
