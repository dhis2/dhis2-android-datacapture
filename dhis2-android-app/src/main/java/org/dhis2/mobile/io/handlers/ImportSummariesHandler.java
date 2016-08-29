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

package org.dhis2.mobile.io.handlers;

import org.dhis2.mobile.io.models.Field;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Import Summaries:
 * Returns whether the post or import into DHIS2 was a success
 * Returns the description of the response from DHIS2 in a formatted manner#
 */
public class ImportSummariesHandler {
    private static final String SUCCESS = "SUCCESS";
    private static final Pattern STATUS = Pattern.compile("\"(status)\":\"(\\w+)\"");
    private static final Pattern DESCRIPTION = Pattern.compile("\"(description)\":\"(.*?)\"");

    private ImportSummariesHandler() {
    }

    /**
     * Returns whether an import was successful or not.
     * @param source String. This would be the string response from DHIS2
     * @return Boolean This returns whether the import was successful or not.
     */

    public static boolean isSuccess(String source) {
        if (source == null || source.equals(Field.EMPTY_FIELD)) {
            return false;
        }
        Matcher matcher = STATUS.matcher(source);
        if (matcher.find()) {
            String status = matcher.group(2);
            return status != null && status.equals(SUCCESS);
        } else {
            return false;
        }
    }

    /**
     * Returns the import description
     * @param source String. This would be the string response from DHIS2
     * @param defaultValue String. This would be the string passed in as a default value and is returned if source is empty or null or has no description
     * @return This is either import description or the default value passed in depending on whether the conditions set were passed.
     */

    public static String getDescription(String source, String defaultValue) {
        if (source == null || source.equals(Field.EMPTY_FIELD)) {
            return defaultValue;
        }
        Matcher matcher = DESCRIPTION.matcher(source);
        if (matcher.find()) {
            String description = matcher.group(2);
            if (description != null && !description.equals(Field.EMPTY_FIELD)) {
                return description;
            } else {
                return defaultValue;
            }
        } else {
            return defaultValue;
        }
    }
}
