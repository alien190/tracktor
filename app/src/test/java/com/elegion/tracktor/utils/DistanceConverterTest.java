package com.elegion.tracktor.utils;

import com.elegion.tracktor.common.CurrentPreferences;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.MockitoRule;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DistanceConverterTest {

    @Rule
    public MockitoRule mMockitoRule = MockitoJUnit.rule();

    private List<String> distanceUnitsSi = new ArrayList<String>() {{
        add("м");
        add("км");
    }};

    private List<String> speedUnitsSi = new ArrayList<String>() {{
        add("км/ч");
    }};

    private List<String> distanceUnitsEng = new ArrayList<String>() {{
        add("ft");
        add("ml");
    }};

    private List<String> speedUnitsEng = new ArrayList<String>() {{
        add("mph");
    }};

    @Mock
    private DistanceConverter.ICurrentPreferences mCurrentPreferencesSi;
    @Mock
    private DistanceConverter.ICurrentPreferences mCurrentPreferencesEng;

    private IDistanceConverter mDistanceConverterSi;
    private IDistanceConverter mDistanceConverterEng;
    private IDistanceConverter mDistanceConverterException;

    @Before
    public void setUp() throws Exception {
        when(mCurrentPreferencesSi.getUnit()).thenReturn(CurrentPreferences.UNITS_SI);
        when(mCurrentPreferencesSi.getDistanceUnitSymbol()).thenReturn(distanceUnitsSi);
        when(mCurrentPreferencesSi.getSpeedUnitSymbol()).thenReturn(speedUnitsSi);

        when(mCurrentPreferencesEng.getUnit()).thenReturn(CurrentPreferences.UNITS_ENG);
        when(mCurrentPreferencesEng.getDistanceUnitSymbol()).thenReturn(distanceUnitsEng);
        when(mCurrentPreferencesEng.getSpeedUnitSymbol()).thenReturn(speedUnitsEng);

        mDistanceConverterSi = new DistanceConverter(mCurrentPreferencesSi);
        mDistanceConverterEng = new DistanceConverter(mCurrentPreferencesEng);
        mDistanceConverterException = new DistanceConverter(null);
    }

    @Test
    public void testConvertDistanceSi() throws Exception {
        assertEquals("0.0 м", mDistanceConverterSi.convertDistance(-1d));
        assertEquals("0.0 м", mDistanceConverterSi.convertDistance(0d));
        assertEquals("100.0 м", mDistanceConverterSi.convertDistance(100d));
        assertEquals("100.0 м", mDistanceConverterSi.convertDistance(100.0001d));
        assertEquals("1.0 км", mDistanceConverterSi.convertDistance(1000d));
        assertEquals("1.5 км", mDistanceConverterSi.convertDistance(1526d));
    }

    @Test
    public void testConvertSpeedSi() throws Exception {
        assertEquals("0.0 км/ч", mDistanceConverterSi.convertSpeed(-1d));
        assertEquals("0.0 км/ч", mDistanceConverterSi.convertSpeed(0d));
        assertEquals("72.0 км/ч", mDistanceConverterSi.convertSpeed(20d));
        assertEquals("72.8 км/ч", mDistanceConverterSi.convertSpeed(20.222d));
    }

    @Test
    public void testConvertDistanceEng() throws Exception {
        assertEquals("0.0 ft", mDistanceConverterEng.convertDistance(-1d));
        assertEquals("0.0 ft", mDistanceConverterEng.convertDistance(0d));
        assertEquals("328.1 ft", mDistanceConverterEng.convertDistance(100d));
        assertEquals("328.1 ft", mDistanceConverterEng.convertDistance(100d));
        assertEquals("328.1 ft", mDistanceConverterEng.convertDistance(100.0001d));
        assertEquals("1.2 ml", mDistanceConverterEng.convertDistance(2000d));
        assertEquals("1.6 ml", mDistanceConverterEng.convertDistance(2526d));
    }

    @Test
    public void testConvertSpeedEn() throws Exception {
        assertEquals("0.0 mph", mDistanceConverterEng.convertSpeed(-1d));
        assertEquals("0.0 mph", mDistanceConverterEng.convertSpeed(0d));
        assertEquals("44.7 mph", mDistanceConverterEng.convertSpeed(20d));
        assertEquals("45.2 mph", mDistanceConverterEng.convertSpeed(20.222d));
        assertEquals("721.0 mph", mDistanceConverterEng.convertSpeed(322.334d));
    }

    @Test
    public void testConvertException() {
        try {
            mDistanceConverterException.convertSpeed(-1d);
            fail();
        } catch (Throwable throwable) {
            assertTrue(true);
        }
    }


    @After
    public void tearDown() throws Exception {
        mDistanceConverterEng = null;
        mDistanceConverterSi = null;
    }
}