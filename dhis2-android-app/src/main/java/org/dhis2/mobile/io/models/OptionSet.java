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

package org.dhis2.mobile.io.models;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Iterator;

public class OptionSet {
	private static final String ID = "id";
	private static final String NAME = "name";
	private static final String CREATED = "created";
	private static final String LAST_UPDATED = "lastUpdated";
	private static final String EXTERNAL_ACCESS = "externalAccess";
	private static final String VERSION = "version";
	private static final String OPTIONS = "options";
	
	private String id;
	private String name;
	private String created;
	private String lastUpdated;
	private String externalAccess;
	private String version;
	private ArrayList<Option> options;
	
	/* public OptionSet(JsonObject jSource) {
		if (jSource.has(ID)) {
			id = jSource.getAsJsonPrimitive(ID).getAsString();
		}
		
		if (jSource.has(NAME)) {
			name = jSource.getAsJsonPrimitive(NAME).getAsString();
		}
		
		if (jSource.has(CREATED)) {
			created = jSource.getAsJsonPrimitive(CREATED).getAsString();
		}
		
		if (jSource.has(LAST_UPDATED)) {
			lastUpdated = jSource.getAsJsonPrimitive(LAST_UPDATED).getAsString();
		}
		
		if (jSource.has(EXTERNAL_ACCESS)) {
			externalAccess = jSource.getAsJsonPrimitive(EXTERNAL_ACCESS).getAsString();
		}
		
		if (jSource.has(VERSION)) {
			version = jSource.getAsJsonPrimitive(VERSION).getAsString();
		}
		
		if (jSource.has(OPTIONS)) {
			JsonArray jOptions = jSource.getAsJsonArray(OPTIONS);
			Iterator<JsonElement> iterator = jOptions.iterator();
			options = new ArrayList<Option>();
			while(iterator.hasNext()) {
				JsonElement jElement = iterator.next();
				if (!jElement.isJsonNull()) {
					String option = jElement.getAsJsonPrimitive().getAsString();
					options.add(option);
				}
			}
		}
	} */



	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getCreated() {
		return created;
	}

	public String getLastUpdated() {
		return lastUpdated;
	}

	public String getExternalAccess() {
		return externalAccess;
	}

	public ArrayList<Option> getOptions() {
		return options;
	}

	public String getVersion() {
		return version;
	}
}
