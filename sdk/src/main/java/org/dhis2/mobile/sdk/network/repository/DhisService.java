package org.dhis2.mobile.sdk.network.repository;

import org.dhis2.mobile.sdk.persistence.models.Category;
import org.dhis2.mobile.sdk.persistence.models.CategoryCombo;
import org.dhis2.mobile.sdk.persistence.models.CategoryOption;
import org.dhis2.mobile.sdk.persistence.models.DataElement;
import org.dhis2.mobile.sdk.persistence.models.DataSet;
import org.dhis2.mobile.sdk.persistence.models.OrganisationUnit;
import org.dhis2.mobile.sdk.persistence.models.UserAccount;

import java.util.List;
import java.util.Map;

import retrofit.http.GET;
import retrofit.http.QueryMap;


public interface DhisService {

    @GET("/me/") UserAccount getCurrentUserAccount(@QueryMap Map<String, String> queryParams);

    @GET("/organisationUnits?paging=false") Map<String, List<OrganisationUnit>> getOrganisationUnits(@QueryMap Map<String, String> queryParams);

    @GET("/dataSets?paging=false") Map<String, List<DataSet>> getDataSets(@QueryMap Map<String, String> queryParams);

    @GET("/dataElements?paging=false") Map<String, List<DataElement>> getDataElements(@QueryMap Map<String, String> queryParams);

    @GET("/categoryCombos?paging=false") Map<String, List<CategoryCombo>> getCategoryCombos(@QueryMap Map<String, String> queryParams);

    @GET("/categories?paging=false") Map<String, List<Category>> getCategories(@QueryMap Map<String, String> queryMap);

    @GET("/categoryOptions?paging=false") Map<String, List<CategoryOption>> getCategoryOptions(@QueryMap Map<String, String> queryMap);

}