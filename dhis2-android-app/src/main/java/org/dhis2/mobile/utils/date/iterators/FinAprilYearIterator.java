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

public class FinAprilYearIterator extends YearIterator {
    private static final String APRIL = "April";

    public FinAprilYearIterator(int openFP, String[] dataInputPeriods) {
        super(openFP, dataInputPeriods);
        openFuturePeriods = openFP;
        if (currentDate.getMonthOfYear() >= 4) {
            cPeriod = new LocalDate(currentDate.getYear(), APR, 1);
        } else {
            cPeriod = new LocalDate(currentDate.getYear() - 1, APR, 1);
        }
        checkDate = new LocalDate(cPeriod);
        if (currentDate.getMonthOfYear() >= 4) {
            maxDate = new LocalDate(currentDate.getYear() + 1, MAR, 31);
        } else {
            maxDate = new LocalDate(currentDate.getYear(), MAR, 31);
        }
        if(openFuturePeriods>0) {
            for (int i = 0; i < openFuturePeriods; i++) {
                maxDate = maxDate.plusYears(1);
            }
        }
    }
    @Override
    protected boolean hasNext(LocalDate date) {
        if (openFuturePeriods > 0) {
            return checkDate.isBefore(maxDate);
        } else {
            LocalDate march;
            if(date.getMonthOfYear()>=4) {
                march = new LocalDate(date.getYear()+1, MAR, 31);
            }
            else{
                march = new LocalDate(date.getYear(), MAR, 31);
            }
            return currentDate.isAfter(march);
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
        LocalDate march;
        if(checkDate.getMonthOfYear()>=4) {
            march = new LocalDate(checkDate.getYear()+1, MAR, 31);
        }
        else{
            march = new LocalDate(checkDate.getYear(), MAR, 31);
        }

        while ((openFuturePeriods > 0 || currentDate.isAfter(march))  && counter < 10) {
            String label = String.format(FIN_DATE_LABEL_FORMAT, APR_STR, checkDate.year().getAsString(), MAR_STR, checkDate.plusYears(1).year().getAsString());
            String date = checkDate.year().getAsString() + APRIL;

            if (checkDate.isBefore(maxDate) && isInInputPeriods(date)) {
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
