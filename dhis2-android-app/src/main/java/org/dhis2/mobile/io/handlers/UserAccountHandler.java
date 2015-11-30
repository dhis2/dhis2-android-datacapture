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

import java.util.ArrayList;

import org.dhis2.mobile.R;

import org.dhis2.mobile.ui.adapters.dataEntry.rows.RowTypes;
import org.dhis2.mobile.io.models.Field;

import android.content.Context;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

public class UserAccountHandler {
	private static final String FIRST_NAME = "firstName";
	private static final String SURNAME = "surname";
	private static final String EMAIL = "email";
	private static final String PHONE_NUMBER = "phoneNumber";
	private static final String INTRODUCTION = "introduction";
	private static final String JOB_TITLE = "jobTitle";
	private static final String GENDER = "gender";
	private static final String BIRTHDAY = "birthday";
	private static final String NATIONALITY = "nationality";
	private static final String EMPLOYER = "employer";
	private static final String EDUCATION = "education";
	private static final String INTERESTS = "interests";
	private static final String LANGUAGES = "languages";	

    // ADD CHECK FOR VALIDNESS OF JSON BEFORE ACTUAL PARSING
    @Deprecated
	public static ArrayList<Field> toFields(Context context, String source) {
        JsonObject info = null;
        if (source != null) {
            info = (new JsonParser()).parse(source).getAsJsonObject();
        }

        if (info == null) {
            return null;
        }
		
		String firstName = getString(info.getAsJsonPrimitive(FIRST_NAME));
		Field mFirstName = toField(context, R.string.first_name, RowTypes.TEXT, FIRST_NAME, firstName);
		
		String surname = getString(info.getAsJsonPrimitive(SURNAME));
		Field mSurname = toField(context, R.string.surname, RowTypes.TEXT, SURNAME, surname);
		
		String email = getString(info.getAsJsonPrimitive(EMAIL));
		Field mEmail = toField(context, R.string.email, RowTypes.TEXT, EMAIL, email);
		
		String phoneNumber = getString(info.getAsJsonPrimitive(PHONE_NUMBER));
		Field mPhoneNumber = toField(context, R.string.mobile_phone_number, RowTypes.NUMBER, PHONE_NUMBER, phoneNumber);
		
		String introduction = getString(info.getAsJsonPrimitive(INTRODUCTION));
		Field mIntroduction = toField(context, R.string.introduction, RowTypes.TEXT, INTRODUCTION, introduction);
		
		String jobTitle = getString(info.getAsJsonPrimitive(JOB_TITLE));
		Field mJobTitle = toField(context, R.string.job_title, RowTypes.TEXT, JOB_TITLE, jobTitle);
		
		String gender = getString(info.getAsJsonPrimitive(GENDER));
		Field mGender = toField(context, R.string.gender, RowTypes.GENDER, GENDER, gender);
		
		String birthday = getString(info.getAsJsonPrimitive(BIRTHDAY));
		Field mBirthday = toField(context, R.string.birthday, RowTypes.DATE, BIRTHDAY, birthday);
		
		String nationality = getString(info.getAsJsonPrimitive(NATIONALITY));
		Field mNationality = toField(context, R.string.nationality, RowTypes.TEXT, NATIONALITY, nationality);
		
		String employer = getString(info.getAsJsonPrimitive(EMPLOYER));
		Field mEmployer = toField(context, R.string.employer, RowTypes.TEXT, EMPLOYER, employer);
		
		String education = getString(info.getAsJsonPrimitive(EDUCATION));
		Field mEducation = toField(context, R.string.education, RowTypes.TEXT, EDUCATION, education);
		
		String interests = getString(info.getAsJsonPrimitive(INTERESTS));
		Field mInterests = toField(context, R.string.interests, RowTypes.TEXT, INTERESTS, interests);
		
		String languages = getString(info.getAsJsonPrimitive(LANGUAGES));
		Field mLanguages = toField(context, R.string.languages, RowTypes.TEXT, LANGUAGES, languages);
		
		ArrayList<Field> fields = addAll(mFirstName, mSurname, mEmail, mPhoneNumber,
					  	mIntroduction, mJobTitle, mGender, mBirthday,
					  	mNationality, mEmployer, mEducation, mInterests, mLanguages);
		return fields;
	}
	
	private static String getString(JsonPrimitive primitive) {
		return primitive != null ? primitive.getAsString() : Field.EMPTY_FIELD;
	}
	
	public static String fromFields(ArrayList<Field> fields) {		
		JsonObject jInfo = new JsonObject();
		for (Field field : fields) {
			jInfo.addProperty(field.getDataElement(), field.getValue());
		}
		return jInfo.toString();
	}
	
	private static Field toField(Context context, int labelId, 
			RowTypes type, String dataElement, String value) {
		
		Field field = new Field();
		field.setLabel(context.getString(labelId));
		field.setDataElement(dataElement);
		field.setType(type.name());
		field.setValue(value);
		return field;
	}
	
	private static ArrayList<Field> addAll(Field... fields){
		ArrayList<Field> mFields = new ArrayList<Field>();
		for (Field field : fields) {
			mFields.add(field);
		}
		return mFields;
	} 
}
