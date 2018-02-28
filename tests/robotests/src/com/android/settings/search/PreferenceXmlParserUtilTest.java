/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.android.settings.search;

import static com.google.common.truth.Truth.assertThat;

import android.content.Context;
import android.content.res.XmlResourceParser;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Xml;

import com.android.settings.R;
import com.android.settings.TestConfig;
import com.android.settings.core.PreferenceXmlParserUtils;
import com.android.settings.testutils.SettingsRobolectricTestRunner;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.List;

/**
 * These tests use a series of preferences that have specific attributes which are sometimes
 * uncommon (such as summaryOn).
 *
 * If changing a preference file breaks a test in this test file, please replace its reference
 * with another preference with a matchin replacement attribute.
 */
@RunWith(SettingsRobolectricTestRunner.class)
@Config(manifest = TestConfig.MANIFEST_PATH, sdk = TestConfig.SDK_VERSION)
public class PreferenceXmlParserUtilTest {

    private Context mContext;

    @Before
    public void setUp() {
        mContext = RuntimeEnvironment.application;
    }

    @Test
    public void testDataTitleValid_ReturnsPreferenceTitle() {
        XmlResourceParser parser = getChildByType(R.xml.display_settings,
                "com.android.settings.TimeoutListPreference");
        final AttributeSet attrs = Xml.asAttributeSet(parser);
        String title = PreferenceXmlParserUtils.getDataTitle(mContext, attrs);
        String expTitle = mContext.getString(R.string.screen_timeout);
        assertThat(title).isEqualTo(expTitle);
    }

    @Test
    public void testDataKeywordsValid_ReturnsPreferenceKeywords() {
        XmlResourceParser parser = getParentPrimedParser(R.xml.display_settings);
        final AttributeSet attrs = Xml.asAttributeSet(parser);
        String keywords = PreferenceXmlParserUtils.getDataKeywords(mContext, attrs);
        String expKeywords = mContext.getString(R.string.keywords_display);
        assertThat(keywords).isEqualTo(expKeywords);
    }

    @Test
    public void testDataKeyValid_ReturnsPreferenceKey() {
        XmlResourceParser parser = getChildByType(R.xml.display_settings,
                "com.android.settings.TimeoutListPreference");
        final AttributeSet attrs = Xml.asAttributeSet(parser);
        String key = PreferenceXmlParserUtils.getDataKey(mContext, attrs);
        String expKey = "screen_timeout";
        assertThat(key).isEqualTo(expKey);
    }

    @Test
    public void testDataSummaryValid_ReturnsPreferenceSummary() {
        XmlResourceParser parser = getChildByType(R.xml.display_settings,
                "com.android.settings.TimeoutListPreference");
        final AttributeSet attrs = Xml.asAttributeSet(parser);
        String summary = PreferenceXmlParserUtils.getDataSummary(mContext, attrs);
        String expSummary = mContext.getString(R.string.summary_placeholder);
        assertThat(summary).isEqualTo(expSummary);
    }

    @Test
    @Config(qualifiers = "mcc999")
    public void testDataSummaryOnOffValid_ReturnsPreferenceSummaryOnOff() {
        XmlResourceParser parser = getChildByType(R.xml.display_settings, "CheckBoxPreference");
        final AttributeSet attrs = Xml.asAttributeSet(parser);

        assertThat(PreferenceXmlParserUtils.getDataSummaryOn(mContext, attrs))
                .isEqualTo("summary_on");
        assertThat(PreferenceXmlParserUtils.getDataSummaryOff(mContext, attrs))
                .isEqualTo("summary_off");
    }

    @Test
    @Config(qualifiers = "mcc999")
    public void testDataEntriesValid_ReturnsPreferenceEntries() {
        XmlResourceParser parser = getChildByType(R.xml.display_settings, "ListPreference");
        final AttributeSet attrs = Xml.asAttributeSet(parser);
        String entries = PreferenceXmlParserUtils.getDataEntries(mContext, attrs);
        String[] expEntries = mContext.getResources()
                .getStringArray(R.array.app_install_location_entries);
        for (int i = 0; i < expEntries.length; i++) {
            assertThat(entries).contains(expEntries[i]);
        }
    }

    // Null checks
    @Test
    @Config(qualifiers = "mcc999")
    public void testDataKeyInvalid_ReturnsNull() {
        XmlResourceParser parser = getParentPrimedParser(R.xml.display_settings);
        final AttributeSet attrs = Xml.asAttributeSet(parser);
        String key = PreferenceXmlParserUtils.getDataKey(mContext, attrs);
        assertThat(key).isNull();
    }

    @Test
    @Config(qualifiers = "mcc999")
    public void testControllerAttribute_returnsValidData() {
        XmlResourceParser parser = getChildByType(R.xml.about_legal, "Preference");
        final AttributeSet attrs = Xml.asAttributeSet(parser);

        String controller = PreferenceXmlParserUtils.getController(mContext, attrs);
        assertThat(controller).isEqualTo("mind_flayer");
    }

    @Test
    public void testDataSummaryInvalid_ReturnsNull() {
        XmlResourceParser parser = getParentPrimedParser(R.xml.display_settings);
        final AttributeSet attrs = Xml.asAttributeSet(parser);
        String summary = PreferenceXmlParserUtils.getDataSummary(mContext, attrs);
        assertThat(summary).isNull();
    }

    @Test
    public void testDataSummaryOffInvalid_ReturnsNull() {
        XmlResourceParser parser = getParentPrimedParser(R.xml.display_settings);
        final AttributeSet attrs = Xml.asAttributeSet(parser);
        String summaryOff = PreferenceXmlParserUtils.getDataSummaryOff(mContext, attrs);
        assertThat(summaryOff).isNull();
    }

    @Test
    public void testDataEntriesInvalid_ReturnsNull() {
        XmlResourceParser parser = getParentPrimedParser(R.xml.display_settings);
        final AttributeSet attrs = Xml.asAttributeSet(parser);
        String entries = PreferenceXmlParserUtils.getDataEntries(mContext, attrs);
        assertThat(entries).isNull();
    }

    @Test
    @Config(qualifiers = "mcc999")
    public void extractMetadata_shouldContainKeyAndControllerName()
            throws IOException, XmlPullParserException {
        final List<Bundle> metadata = PreferenceXmlParserUtils.extractMetadata(mContext,
                R.xml.location_settings);

        assertThat(metadata).isNotEmpty();
        for (Bundle bundle : metadata) {
            assertThat(bundle.getString(PreferenceXmlParserUtils.METADATA_KEY)).isNotNull();
            assertThat(bundle.getString(PreferenceXmlParserUtils.METADATA_CONTROLLER)).isNotNull();
        }
    }

    /**
     * @param resId the ID for the XML preference
     * @return an XML resource parser that points to the start tag
     */
    private XmlResourceParser getParentPrimedParser(int resId) {
        XmlResourceParser parser = null;
        try {
            parser = mContext.getResources().getXml(resId);

            int type;
            while ((type = parser.next()) != XmlPullParser.END_DOCUMENT
                    && type != XmlPullParser.START_TAG) {
            }
        } catch (Exception e) {

        }
        return parser;
    }

    private XmlResourceParser getChildByType(int resId, String xmlType) {
        XmlResourceParser parser = null;
        try {
            parser = mContext.getResources().getXml(resId);

            int type;
            while ((type = parser.next()) != XmlPullParser.END_DOCUMENT
                    && type != XmlPullParser.START_TAG) {
            }
            while (parser.getName() != xmlType && parser.next() != XmlPullParser.END_DOCUMENT) {
            }
        } catch (Exception e) {

        }
        return parser;
    }
}