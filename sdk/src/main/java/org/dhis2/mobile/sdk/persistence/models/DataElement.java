package org.dhis2.mobile.sdk.persistence.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.Table;

import org.dhis2.mobile.sdk.persistence.DbDhis;

@Table(databaseName = DbDhis.NAME)
public final class DataElement extends BaseIdentifiableObject {
    public static final String VALUE_TYPE_INT = "int";
    public static final String VALUE_TYPE_STRING = "string";
    public static final String VALUE_TYPE_USER_NAME = "username";
    public static final String VALUE_TYPE_BOOL = "bool";
    public static final String VALUE_TYPE_TRUE_ONLY = "trueOnly";
    public static final String VALUE_TYPE_DATE = "date";
    public static final String VALUE_TYPE_UNIT_INTERVAL = "unitInterval";
    public static final String VALUE_TYPE_PERCENTAGE = "percentage";
    public static final String VALUE_TYPE_NUMBER = "number";
    public static final String VALUE_TYPE_POSITIVE_INT = "posInt";
    public static final String VALUE_TYPE_NEGATIVE_INT = "negInt";
    public static final String VALUE_TYPE_ZERO_OR_POSITIVE_INT = "zeroPositiveInt";
    public static final String VALUE_TYPE_TEXT = "text";
    public static final String VALUE_TYPE_LONG_TEXT = "longText";

    @JsonProperty("displayName") @Column String displayName;
    @JsonProperty("type") @Column String type;
    @JsonProperty("numberType") @Column String numberType;
    @JsonProperty("textType") @Column String textType;
    @JsonProperty("zeroIsSignificant") @Column boolean zeroIsSignificant;
    @JsonProperty("categoryCombo") CategoryCombo categoryCombo;

    @JsonIgnore
    public String getDisplayName() {
        return displayName;
    }

    @JsonIgnore
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @JsonIgnore
    public String getType() {
        return type;
    }

    @JsonIgnore
    public void setType(String type) {
        this.type = type;
    }

    @JsonIgnore
    public String getNumberType() {
        return numberType;
    }

    @JsonIgnore
    public void setNumberType(String numberType) {
        this.numberType = numberType;
    }

    @JsonIgnore
    public String getTextType() {
        return textType;
    }

    @JsonIgnore
    public void setTextType(String textType) {
        this.textType = textType;
    }

    @JsonIgnore
    public boolean isZeroIsSignificant() {
        return zeroIsSignificant;
    }

    @JsonIgnore
    public void setZeroIsSignificant(boolean zeroIsSignificant) {
        this.zeroIsSignificant = zeroIsSignificant;
    }

    @JsonIgnore
    public CategoryCombo getCategoryCombo() {
        return categoryCombo;
    }

    @JsonIgnore
    public void setCategoryCombo(CategoryCombo categoryCombo) {
        this.categoryCombo = categoryCombo;
    }
}