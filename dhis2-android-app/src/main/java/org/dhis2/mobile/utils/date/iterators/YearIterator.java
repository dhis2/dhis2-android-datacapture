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

public class YearIterator extends CustomDateIteratorClass<ArrayList<DateHolder>> {
    public static final int DECADE = 10;
    protected static final String FIN_DATE_LABEL_FORMAT = "%s %s - %s %s";

    protected int openFuturePeriods;
    protected LocalDate cPeriod;
    protected LocalDate checkDate;

    public YearIterator(int openFP) {
        openFuturePeriods = openFP;
        cPeriod = new LocalDate(currentDate.getYear(), JAN, 1);
        checkDate = new LocalDate(cPeriod);
    }

    @Override
    public boolean hasNext() {
        return hasNext(checkDate);
    }

    protected boolean hasNext(LocalDate date) {
        if (openFuturePeriods > 0) {
            return true;
        } else {
            return currentDate.isAfter(date.plusYears(1));
        }
    }

    @Override
    public ArrayList<DateHolder> next() {
        cPeriod = cPeriod.plusYears(DECADE);
        return generatePeriod();
    }

    @Override
    public ArrayList<DateHolder> previous() {
        cPeriod = cPeriod.minusYears(DECADE);
        return generatePeriod();
    }

    @Override
    public ArrayList<DateHolder> current() {
        return previous();
    }

    @Override
    protected ArrayList<DateHolder> generatePeriod() {
        ArrayList<DateHolder> dates = new ArrayList<DateHolder>();
        int counter = 0;
        checkDate = new LocalDate(cPeriod);

        while (hasNext(checkDate) && counter < 10) {
            String dateStr = checkDate.year().getAsString();
            DateHolder dateHolder = new DateHolder(dateStr, checkDate.toString(), dateStr);
            dates.add(dateHolder);

            checkDate = checkDate.plusYears(1);
            counter++;
        }

        Collections.reverse(dates);
        return dates;
    }
}
