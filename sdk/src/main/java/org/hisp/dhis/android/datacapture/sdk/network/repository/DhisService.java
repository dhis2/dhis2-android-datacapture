package org.hisp.dhis.android.datacapture.sdk.network.repository;

import org.hisp.dhis.android.datacapture.sdk.persistence.models.Category;
import org.hisp.dhis.android.datacapture.sdk.persistence.models.CategoryCombo;
import org.hisp.dhis.android.datacapture.sdk.persistence.models.CategoryOption;
import org.hisp.dhis.android.datacapture.sdk.persistence.models.DataElement;
import org.hisp.dhis.android.datacapture.sdk.persistence.models.DataSet;
import org.hisp.dhis.android.datacapture.sdk.persistence.models.OrganisationUnit;
import org.hisp.dhis.android.datacapture.sdk.persistence.models.SystemInfo;
import org.hisp.dhis.android.datacapture.sdk.persistence.models.UserAccount;

import java.util.List;
import java.util.Map;

import retrofit.http.GET;
import retrofit.http.QueryMap;


public interface DhisService {

    @GET("/system/info/") SystemInfo getSystemInfo();

    @GET("/me/") UserAccount getCurrentUserAccount(@QueryMap Map<String, String> queryParams);

    @GET("/organisationUnits?paging=false") Map<String, List<OrganisationUnit>> getOrganisationUnits(@QueryMap Map<String, String> queryParams);

    @GET("/dataSets?paging=false") Map<String, List<DataSet>> getDataSets(@QueryMap Map<String, String> queryParams);

    @GET("/dataElements?paging=false") Map<String, List<DataElement>> getDataElements(@QueryMap Map<String, String> queryParams);

    @GET("/categoryCombos?paging=false") Map<String, List<CategoryCombo>> getCategoryCombos(@QueryMap Map<String, String> queryParams);

    @GET("/categories?paging=false") Map<String, List<Category>> getCategories(@QueryMap Map<String, String> queryMap);

    @GET("/categoryOptions?paging=false") Map<String, List<CategoryOption>> getCategoryOptions(@QueryMap Map<String, String> queryMap);

}