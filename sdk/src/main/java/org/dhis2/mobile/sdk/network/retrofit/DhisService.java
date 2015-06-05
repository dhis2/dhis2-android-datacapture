package org.dhis2.mobile.sdk.network.retrofit;

import org.dhis2.mobile.sdk.persistence.models.Category;
import org.dhis2.mobile.sdk.persistence.models.CategoryCombo;
import org.dhis2.mobile.sdk.persistence.models.CategoryOption;
import org.dhis2.mobile.sdk.persistence.models.DataSet;
import org.dhis2.mobile.sdk.persistence.models.OrganisationUnit;

import java.util.List;
import java.util.Map;

import retrofit.http.GET;
import retrofit.http.QueryMap;


public interface DhisService {

    @GET("/me/")
    List<OrganisationUnit> getAssignedOrganisationUnitIds(@QueryMap Map<String, String> queryParams);

    @GET("/organisationUnits/")
    List<OrganisationUnit> getOrganisationUnits(@QueryMap Map<String, String> queryParams);

    @GET("/dataSets/")
    List<DataSet> getDataSets(@QueryMap Map<String, String> queryParams);

    @GET("categoryCombos")
    List<CategoryCombo> getCategoryCombos(@QueryMap Map<String, String> queryParams);

    @GET("categories")
    List<Category> getCategories(@QueryMap Map<String, String> queryMap);

    @GET("categoryOptions")
    List<CategoryOption> getCategoryOptions(@QueryMap Map<String, String> queryMap);


}
