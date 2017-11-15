package org.dhis2.mobile.utils.date.expiryday;

import org.dhis2.mobile.utils.date.DateConstants;

public class ExpiryDayValidatorFactory {

    public static ExpiryDayValidator getExpiryDay(String periodType, int expiryDays,
            String period) {
        switch (periodType) {
            case DateConstants.PERIOD_FINANCIAL_APRIL:
                return new FinancialYearAprilExpiryDayValidator(expiryDays, period);
            case DateConstants.PERIOD_FINANCIAL_JULY:
                return new FinancialYearJulyExpiryDayValidator(expiryDays, period);
            case DateConstants.PERIOD_FINANCIAL_OCT:
                return new FinancialYearOctExpiryDayValidator(expiryDays, period);
            case DateConstants.PERIOD_YEARLY:
                return new YearlyExpiryDayValidator(expiryDays, period);
            case DateConstants.PERIOD_QUARTERLY:
                return new QuarterlyExpiryDayValidator(expiryDays, period);
            case DateConstants.PERIOD_SIX_MONTHLY_APRIL:
                return new SixMonthlyAprilExpiryDayValidator(expiryDays, period);
            case DateConstants.PERIOD_SIX_MONTHLY:
                return new SixMonthlyExpiryDayValidator(expiryDays, period);
            case DateConstants.PERIOD_BIMONTHLY:
                return new BimonthlyExpiryDayValidator(expiryDays, period);
            case DateConstants.PERIOD_MONTHLY:
                return new MonthlyExpiryDayValidator(expiryDays, period);
            case DateConstants.PERIOD_WEEKLY_WEDNESDAY:
                return new WeeklyExpiryDayValidator(expiryDays, period);
            case DateConstants.PERIOD_WEEKLY_THURSDAY:
                return new WeeklyThursdayExpiryDayValidator(expiryDays, period);
            case DateConstants.PERIOD_WEEKLY_SATURDAY:
                return new WeeklySaturdayExpiryDayValidator(expiryDays, period);
            case DateConstants.PERIOD_WEEKLY_SUNDAY:
                return new WeeklySundayExpiryDayValidator(expiryDays, period);
            case DateConstants.PERIOD_WEEKLY:
                return new WeeklyExpiryDayValidator(expiryDays, period);
            default:
                return new ExpiryDayValidator(expiryDays, period);
        }
    }
}
