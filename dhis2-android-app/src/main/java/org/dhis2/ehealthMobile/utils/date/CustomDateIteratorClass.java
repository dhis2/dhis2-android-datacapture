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

package org.dhis2.ehealthMobile.utils.date;

import org.joda.time.LocalDate;

public abstract class CustomDateIteratorClass<T> implements CustomDateIterator<T> {    
    protected static final int JAN = 1;
    protected static final int FEB = 2;
    protected static final int MAR = 3;
    protected static final int APR = 4;
    protected static final int MAY = 5;
    protected static final int JUN = 6;
    protected static final int JUL = 7;
    protected static final int AUG = 8;
    protected static final int SEP = 9;
    protected static final int OCT = 10;
    protected static final int NOV = 11;
    protected static final int DEC = 12;
    
    protected static final String JAN_STR;
    protected static final String FEB_STR;
    protected static final String MAR_STR;
    protected static final String APR_STR;
    protected static final String MAY_STR;
    protected static final String JUN_STR;
    protected static final String JUL_STR;
    protected static final String AUG_STR;
    protected static final String SEP_STR;
    protected static final String OCT_STR;
    protected static final String NOV_STR;
    protected static final String DEC_STR;
    
    static {
        LocalDate lDate = new LocalDate();
        
        JAN_STR = lDate.withMonthOfYear(JAN).monthOfYear().getAsShortText();
        FEB_STR = lDate.withMonthOfYear(FEB).monthOfYear().getAsShortText();
        MAR_STR = lDate.withMonthOfYear(MAR).monthOfYear().getAsShortText();
        APR_STR = lDate.withMonthOfYear(APR).monthOfYear().getAsShortText();
        MAY_STR = lDate.withMonthOfYear(MAY).monthOfYear().getAsShortText();
        JUN_STR = lDate.withMonthOfYear(JUN).monthOfYear().getAsShortText();
        JUL_STR = lDate.withMonthOfYear(JUL).monthOfYear().getAsShortText();
        AUG_STR = lDate.withMonthOfYear(AUG).monthOfYear().getAsShortText();
        SEP_STR = lDate.withMonthOfYear(SEP).monthOfYear().getAsShortText();
        OCT_STR = lDate.withMonthOfYear(OCT).monthOfYear().getAsShortText();
        NOV_STR = lDate.withMonthOfYear(NOV).monthOfYear().getAsShortText();
        DEC_STR = lDate.withMonthOfYear(DEC).monthOfYear().getAsShortText();
    }
    
    protected LocalDate currentDate;
    
    public CustomDateIteratorClass() {
        currentDate = new LocalDate();
    }
    
    public boolean hasPrevious() {
        return true;
    }
    
    public abstract T current();    
    public abstract boolean hasNext();
    public abstract T next();
    public abstract T previous();    
    protected abstract T generatePeriod();
}
