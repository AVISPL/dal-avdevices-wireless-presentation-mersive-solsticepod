/*
 *  Copyright (c) 2023 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.avdevices.wirelesspresentation.mersive.solsticepodgen3.common;

/**
 * Enumeration of supported languages in the SolsticeLanguageEnum.
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 6/13/2023
 * @since 1.0.0
 */
public enum LanguageEnum {
	ARABIC("Arabic", "ar_SA"),
	SIMPLIFIED_CHINESE("Simplified Chinese", "zh_CN"),
	TRADITIONAL_CHINESE("Traditional Chinese", "zh_TW"),
	DANISH("Danish", "da_DK"),
	ENGLISH("English", "en_US"),
	FRENCH("French", "fr_FR"),
	GERMAN("German", "de_DE"),
	ITALIA("Italian", "it_IT"),
	JAPANESE("Japanese", "ja_JP"),
	KOREAN("Korean", "ko_KR"),
	NORWEGIAN("Norwegian", "nb_NO"),
	POLISH("Polish", "pl_PL"),
	PORTUGUESE("Portuguese", "pt_PT"),
	RUSSIAN("Russian", "ru_RU"),
	SPANISH("Spanish", "es_ES"),
	SWEDISH("Swedish", "sv_SE"),
	TURKISH("Turkish", "tr_TR"),
	WELSH("Welsh", "cy_GB"),
	;
	private final String name;
	private final String value;

	/**
	 * Constructs a SolsticeLanguageEnum with the specified name and value.
	 *
	 * @param name the name of the language
	 * @param value the value of the language
	 */
	LanguageEnum(String name, String value) {
		this.name = name;
		this.value = value;
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
}
