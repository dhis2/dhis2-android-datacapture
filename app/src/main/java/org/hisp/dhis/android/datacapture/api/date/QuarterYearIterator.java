/*
 * Copyright (c) 2015, University of Oslo
 *
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.hisp.dhis.android.datacapture.api.date;

import org.hisp.dhis.android.datacapture.api.models.DateHolder;
import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class QuarterYearIterator extends CustomDateIteratorClass<List<DateHolder>> {
    private static final String DATE_LABEL_FORMAT = "%s - %s %s";

    private static final String Q1 = "Q1";
    private static final String Q2 = "Q2";
    private static final String Q3 = "Q3";
    private static final String Q4 = "Q4";

    private LocalDate mPeriod;
    private LocalDate mCheckDate;
    private boolean mAllowFP;

    public QuarterYearIterator(boolean allowFuturePeriod) {
        mAllowFP = allowFuturePeriod;
        mPeriod = new LocalDate(currentDate.getYear(), JAN, 1);
        mCheckDate = new LocalDate(mPeriod);
    }

    @Override
    public List<DateHolder> current() {
        if (!hasNext()) {
            return previous();
        } else {
            return generatePeriod();
        }
    }

    @Override
    public boolean hasNext() {
        return hasNext(mCheckDate);
    }

    private boolean hasNext(LocalDate date) {
        if (mAllowFP) {
            return true;
        } else {
            return currentDate.isAfter(date.plusMonths(3));
        }
    }

    @Override
    public List<DateHolder> next() {
        mPeriod = mPeriod.plusYears(1);
        return generatePeriod();
    }

    @Override
    public List<DateHolder> previous() {
        mPeriod = mPeriod.minusYears(1);
        return generatePeriod();
    }

    @Override
    protected List<DateHolder> generatePeriod() {
        List<DateHolder> dates = new ArrayList<>();
        mCheckDate = new LocalDate(mPeriod);
        int counter = 0;

        while (hasNext(mCheckDate) && counter < 4) {
            String label;
            String date;

            int cMonth = mCheckDate.getMonthOfYear();
            String cYearStr = mCheckDate.year().getAsString();

            if (cMonth < MAR) {
                date = cYearStr + Q1;
                label = String.format(DATE_LABEL_FORMAT, JAN_STR, MAR_STR, cYearStr);
            } else if ((cMonth >= MAR) && (cMonth < JUN)) {
                date = cYearStr + Q2;
                label = String.format(DATE_LABEL_FORMAT, APR_STR, JUN_STR, cYearStr);
            } else if ((cMonth >= JUN) && (cMonth < SEP)) {
                date = cYearStr + Q3;
                label = String.format(DATE_LABEL_FORMAT, JUL_STR, SEP_STR, cYearStr);
            } else {
                date = cYearStr + Q4;
                label = String.format(DATE_LABEL_FORMAT, OCT_STR, DEC_STR, cYearStr);
            }

            DateHolder dateHolder = new DateHolder(date, label);
            dates.add(dateHolder);

            mCheckDate = mCheckDate.plusMonths(3);
            counter++;
        }

        Collections.reverse(dates);
        return dates;
    }
}
