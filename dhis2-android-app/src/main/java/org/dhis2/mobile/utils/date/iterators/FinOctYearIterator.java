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

import org.dhis2.mobile.utils.date.DateHolder;
import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.Collections;

public class FinOctYearIterator extends YearIterator {
    private static final String OCTOBER = "Oct";

    public FinOctYearIterator(int openFP) {
        super(openFP);
        openFuturePeriods = openFP;
        cPeriod = new LocalDate(currentDate.getYear(), OCT, 31);
        checkDate = new LocalDate(cPeriod);
        maxDate = new LocalDate(currentDate.getYear(), SEP, 1);
        for (int i = 0; i < openFuturePeriods; i++) {
            maxDate = maxDate.plusYears(1);
        }
    }

    @Override
    protected boolean hasNext(LocalDate date) {
        if (openFuturePeriods > 0) {
            return checkDate.isBefore(maxDate);
        } else {
            LocalDate sep = new LocalDate(date.getYear(), SEP, 30);
            return currentDate.isAfter(sep);
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
    protected ArrayList<DateHolder> generatePeriod() {
        ArrayList<DateHolder> dates = new ArrayList<DateHolder>();
        int counter = 0;
        checkDate = new LocalDate(cPeriod);
        LocalDate sep = new LocalDate(checkDate.getYear(), SEP, 30);

        while ((openFuturePeriods > 0 || currentDate.isAfter(sep)) && counter < 10) {
            String dateStr = checkDate.minusYears(1).year().getAsString();
            String label = String.format(FIN_DATE_LABEL_FORMAT, OCT_STR, dateStr, SEP_STR, checkDate.year().getAsString());
            String date = dateStr + OCTOBER;

            if(checkDate.isBefore(maxDate)) {
                DateHolder dateHolder = new DateHolder(date, checkDate.toString(), label);
                dates.add(dateHolder);
            }

            checkDate = checkDate.plusYears(1);
            counter++;
        }

        Collections.reverse(dates);
        return dates;
    }
}
