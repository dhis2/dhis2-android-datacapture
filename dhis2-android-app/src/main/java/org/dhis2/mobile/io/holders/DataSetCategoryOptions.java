package org.dhis2.mobile.io.holders;

import org.dhis2.mobile.io.models.CategoryCombo;

import java.util.HashMap;
import java.util.List;

public class DataSetCategoryOptions {
    private CategoryCombo defaultCategoryCombo;
    private HashMap<String, List<CategoryCombo>> categoryComboByDataElement = new HashMap<>();
    private HashMap<String, List<String>> categoryOptionComboUIdsBySection = new HashMap<>();


    public HashMap<String, List<CategoryCombo>> getCategoryComboByDataElement() {
        return categoryComboByDataElement;
    }

    public void setCategoryComboByDataElement(
            HashMap<String, List<CategoryCombo>> categoryComboByDataElement) {
        this.categoryComboByDataElement = categoryComboByDataElement;
    }

    public CategoryCombo getDefaultCategoryCombo() {
        return defaultCategoryCombo;
    }

    public void setDefaultCategoryCombo(CategoryCombo defaultCategoryCombo) {
        this.defaultCategoryCombo = defaultCategoryCombo;
    }

    public HashMap<String, List<String>>
    getCategoryComboDataElementBySection() {
        return categoryOptionComboUIdsBySection;
    }

    public void setCategoryOptionComboUIdsBySection(
            HashMap<String, List<String>>
                    categoryOptionComboUIdsBySection) {
        this.categoryOptionComboUIdsBySection = categoryOptionComboUIdsBySection;
    }
}
