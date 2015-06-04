package org.dhis2.mobile.sdk.persistence.models;


import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ConflictAction;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.ForeignKeyAction;
import com.raizlabs.android.dbflow.annotation.ForeignKeyReference;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.annotation.Unique;
import com.raizlabs.android.dbflow.annotation.UniqueGroup;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.dhis2.mobile.sdk.persistence.database.DhisDatabase;

@Table(databaseName = DhisDatabase.NAME, uniqueColumnGroups = {
        @UniqueGroup(groupNumber = CategoryComboToCategoryRelation.UNIQUE_COMBO_TO_CATEGORY_GROUP, uniqueConflict = ConflictAction.FAIL)
})
public final class CategoryComboToCategoryRelation extends BaseModel implements RelationModel {
    static final int UNIQUE_COMBO_TO_CATEGORY_GROUP = 1;
    static final String CATEGORY_COMBO_KEY = "categoryCombo";
    static final String CATEGORY_KEY = "category";

    @Column @PrimaryKey(autoincrement = true) int id;

    @Column @Unique(unique = false, uniqueGroups = {UNIQUE_COMBO_TO_CATEGORY_GROUP})
    @ForeignKey(
            references = {
                    @ForeignKeyReference(columnName = CATEGORY_COMBO_KEY, columnType = String.class, foreignColumnName = "id")
            }, saveForeignKeyModel = false, onDelete = ForeignKeyAction.CASCADE
    ) CategoryCombo categoryCombo;

    @Column @Unique(unique = false, uniqueGroups = {UNIQUE_COMBO_TO_CATEGORY_GROUP})
    @ForeignKey(
            references = {
                    @ForeignKeyReference(columnName = CATEGORY_KEY, columnType = String.class, foreignColumnName = "id")
            }, saveForeignKeyModel = false, onDelete = ForeignKeyAction.CASCADE
    ) Category category;


    @Override public String getFirstKey() {
        return categoryCombo.getId();
    }

    @Override public String getSecondKey() {
        return category.getId();
    }

    public CategoryCombo getCategoryCombo() {
        return categoryCombo;
    }

    public void setCategoryCombo(CategoryCombo categoryCombo) {
        this.categoryCombo = categoryCombo;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }
}
