/*
 * Copyright (c) 2014, Araz Abishov
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its contributors
 * may be used to endorse or promote products derived from this software
 * without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

package org.dhis2.mobile.utils.date;

import org.dhis2.mobile.utils.date.iterators.BiMonthIterator;
import org.dhis2.mobile.utils.date.iterators.DayIterator;
import org.dhis2.mobile.utils.date.iterators.FinAprilYearIterator;
import org.dhis2.mobile.utils.date.iterators.FinJulyYearIterator;
import org.dhis2.mobile.utils.date.iterators.FinOctYearIterator;
import org.dhis2.mobile.utils.date.iterators.MonthIterator;
import org.dhis2.mobile.utils.date.iterators.QuarterYearIterator;
import org.dhis2.mobile.utils.date.iterators.SixMonthIterator;
import org.dhis2.mobile.utils.date.iterators.WeekIterator;
import org.dhis2.mobile.utils.date.iterators.YearIterator;

import java.util.ArrayList;


public class DateIteratorFactory {
    private static final String YEARLY = "Yearly";

    private static final String FINANCIAL_APRIL = "FinancialApril";
    private static final String FINANCIAL_JULY = "FinancialJuly";
    private static final String FINANCIAL_OCT = "FinancialOct";

    private static final String SIX_MONTHLY = "SixMonthly";
    private static final String QUARTERLY = "Quarterly";
    private static final String BIMONTHLY = "BiMonthly";

    private static final String MONTHLY = "Monthly";
    private static final String WEEKLY = "Weekly";
    private static final String DAILY = "Daily";

    // private static final String WRONG_ALLOW_FP_PARAM = "Wrong allowFuturePeriod parameter";
    // private static final String WRONG_PERIOD_TYPE = "Wrong periodType";

    public static final String TRUE = "true";
    public static final String FALSE = "false";

    public static CustomDateIterator<ArrayList<DateHolder>> getDateIterator(String periodType, int openFuturePeriods) {
        if (periodType != null) {
            if (periodType.equals(YEARLY)) {
                return (new YearIterator(openFuturePeriods));
            } else if (periodType.equals(FINANCIAL_APRIL)) {
                return (new FinAprilYearIterator(openFuturePeriods));
            } else if (periodType.equals(FINANCIAL_JULY)) {
                return (new FinJulyYearIterator(openFuturePeriods));
            } else if (periodType.equals(FINANCIAL_OCT)) {
                return (new FinOctYearIterator(openFuturePeriods));
            } else if (periodType.equals(SIX_MONTHLY)) {
                return (new SixMonthIterator(openFuturePeriods));
            } else if (periodType.equals(QUARTERLY)) {
                return (new QuarterYearIterator(openFuturePeriods));
            } else if (periodType.equals(BIMONTHLY)) {
                return (new BiMonthIterator(openFuturePeriods));
            } else if (periodType.equals(MONTHLY)) {
                return (new MonthIterator(openFuturePeriods));
            } else if (periodType.equals(WEEKLY)) {
                return (new WeekIterator(openFuturePeriods));
            } else if (periodType.equals(DAILY)) {
                return (new DayIterator(openFuturePeriods));
            }
        }
        throw new IllegalArgumentException("Unsupported period type");
    }
}

