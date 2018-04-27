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

package org.dhis2.mobile.utils.date.iterators;

import org.dhis2.mobile.utils.date.CustomDateIteratorClass;
import org.dhis2.mobile.utils.date.DateHolder;
import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.Collections;

public class BiMonthIterator extends CustomDateIteratorClass<ArrayList<DateHolder>> {
    private static final String B1 = "01B";
    private static final String B2 = "02B";
    private static final String B3 = "03B";
    private static final String B4 = "04B";
    private static final String B5 = "05B";
    private static final String B6 = "06B";

    private static final String DATE_LABEL_FORMAT = "%s - %s %s";

    private int openFuturePeriods;
    private LocalDate checkDate;
    private LocalDate cPeriod;
    private LocalDate maxDate;

    public BiMonthIterator(int openFP, String[] dataInputPeriods) {
        super(dataInputPeriods);
        openFuturePeriods = openFP;
        cPeriod = new LocalDate(currentDate.getYear(), JAN, 1);
        checkDate = new LocalDate(cPeriod);
        maxDate = new LocalDate(currentDate.getYear(), currentDate.getMonthOfYear(), 1);
        for (int i = 0; i < openFuturePeriods; i++) {
            maxDate = maxDate.plusMonths(2);
        }
    }

    @Override
    public ArrayList<DateHolder> current() {
        if (!hasNext()) {
            return previous();
        } else {
            return generatePeriod();
        }
    }

    @Override
    public boolean hasNext() {
        return hasNext(checkDate);
    }

    private boolean hasNext(LocalDate date) {
        if (openFuturePeriods > 0) {
            return checkDate.isBefore(maxDate);
        } else {
            return currentDate.isAfter(date.plusMonths(2));
        }
    }

    @Override
    public ArrayList<DateHolder> next() {
        cPeriod = cPeriod.plusYears(1);
        return generatePeriod();
    }

    @Override
    public ArrayList<DateHolder> previous() {
        cPeriod = cPeriod.minusYears(1);
        return generatePeriod();
    }

    @Override
    protected ArrayList<DateHolder> generatePeriod() {
        int counter = 0;
        ArrayList<DateHolder> dates = new ArrayList<DateHolder>();
        checkDate = new LocalDate(cPeriod);

        while ((openFuturePeriods > 0 || currentDate.isAfter(checkDate.plusMonths(2))) && counter < 6) {
            int cMonth = checkDate.getMonthOfYear();
            String year = checkDate.year().getAsString();

            String date;
            String label;

            if (cMonth < FEB) {
                date = year + B1;
                label = String.format(DATE_LABEL_FORMAT, JAN_STR, FEB_STR, year);
            } else if ((cMonth >= FEB) && (cMonth < APR)) {
                date = year + B2;
                label = String.format(DATE_LABEL_FORMAT, MAR_STR, APR_STR, year);
            } else if ((cMonth >= APR) && (cMonth < JUN)) {
                date = year + B3;
                label = String.format(DATE_LABEL_FORMAT, MAY_STR, JUN_STR, year);
            } else if ((cMonth >= JUN) && (cMonth < AUG)) {
                date = year + B4;
                label = String.format(DATE_LABEL_FORMAT, JUL_STR, AUG_STR, year);
            } else if ((cMonth >= AUG) && (cMonth < OCT)) {
                date = year + B5;
                label = String.format(DATE_LABEL_FORMAT, SEP_STR, OCT_STR, year);
            } else {
                date = year + B6;
                label = String.format(DATE_LABEL_FORMAT, NOV_STR, DEC_STR, year);
            }


            if (checkDate.isBefore(maxDate) && isInInputPeriods(date)) {
                DateHolder dateHolder = new DateHolder(date, checkDate.toString(), label);
                dates.add(dateHolder);
            }

            counter++;
            checkDate = checkDate.plusMonths(2);
        }

        Collections.reverse(dates);
        return dates;
    }

}
