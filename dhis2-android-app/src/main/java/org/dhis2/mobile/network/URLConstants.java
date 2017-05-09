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

package org.dhis2.mobile.network;

public class URLConstants {
	private URLConstants() { }
	
	public static final String API_USER_ACCOUNT_URL = "api/me/user-account";

	public static final String OPTION_SET_PARAM = "?fields=id,name,created,lastUpdated," +
			"externalAccess,version,options[id,name,code,created,lastUpdated]";
	public static final String OPTION_SET_URL = "api/optionSets";

	public static final String DATASETS_URL = "api/me/assignedDataSets";
	public static final String DATASET_UPLOAD_URL = "api/dataValueSets";
	public static final String DATASET_VALUES_URL = "api/dataSets";
	
	public static final String FORM_PARAM = "form?ou=";
	public static final String COMPULSORY_DATA_ELEMENTS_PARAM = "fields=compulsoryDataElementOperands[dataElement[id]]";
	public static final String PERIOD_PARAM = "&pe=";
	public static final String CATEGORY_OPTIONS_PARAM = "&categoryOptions=";
}
