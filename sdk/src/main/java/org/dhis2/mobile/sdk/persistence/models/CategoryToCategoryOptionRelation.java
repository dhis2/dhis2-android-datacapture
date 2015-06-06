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

import org.dhis2.mobile.sdk.persistence.DbDhis;

@Table(databaseName = DbDhis.NAME, uniqueColumnGroups = {
        @UniqueGroup(groupNumber = CategoryToCategoryOptionRelation.UNIQUE_CATEGORY_TO_OPTION_GROUP, uniqueConflict = ConflictAction.FAIL)
})
public class CategoryToCategoryOptionRelation extends BaseModel implements RelationModel {
    static final int UNIQUE_CATEGORY_TO_OPTION_GROUP = 1;
    static final String CATEGORY_KEY = "category";
    static final String CATEGORY_OPTION_KEY = "categoryOption";

    @Column @PrimaryKey(autoincrement = true) int id;

    @Column @Unique(unique = false, uniqueGroups = {UNIQUE_CATEGORY_TO_OPTION_GROUP})
    @ForeignKey(
            references = {
                    @ForeignKeyReference(columnName = CATEGORY_KEY, columnType = String.class, foreignColumnName = "id")
            }, saveForeignKeyModel = false, onDelete = ForeignKeyAction.CASCADE
    ) Category category;

    @Column @Unique(unique = false, uniqueGroups = {UNIQUE_CATEGORY_TO_OPTION_GROUP})
    @ForeignKey(
            references = {
                    @ForeignKeyReference(columnName = CATEGORY_OPTION_KEY, columnType = String.class, foreignColumnName = "id")
            }, saveForeignKeyModel = false, onDelete = ForeignKeyAction.CASCADE
    ) CategoryOption categoryOption;

    @Override public String getFirstKey() {
        return category.getId();
    }

    @Override public String getSecondKey() {
        return categoryOption.getId();
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public CategoryOption getCategoryOption() {
        return categoryOption;
    }

    public void setCategoryOption(CategoryOption categoryOption) {
        this.categoryOption = categoryOption;
    }
}
