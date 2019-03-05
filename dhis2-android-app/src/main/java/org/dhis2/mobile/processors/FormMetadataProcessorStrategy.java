package org.dhis2.mobile.processors;


import android.content.Context;

import org.dhis2.mobile.io.holders.DatasetInfoHolder;
import org.dhis2.mobile.io.json.ParsingException;
import org.dhis2.mobile.io.models.Form;
import org.dhis2.mobile.network.NetworkException;
import org.dhis2.mobile.ui.adapters.dataEntry.FieldAdapter;
import org.dhis2.mobile.utils.PrefUtils;

class FormMetadataProcessorStrategy {

    public static void process(Context context, Form form, DatasetInfoHolder info)
            throws ParsingException, NetworkException {
        String jsonContent = DataSetMetaData.download(context, info.getFormId(), PrefUtils.getServerVersion(context).equals("2.25"));
        form.setFieldCombinationRequired(
                DataElementOperandParser.isFieldCombinationRequiredToForm(jsonContent));
        DataSetMetaData.addCompulsoryDataElements(
                DataElementOperandParser.parse(jsonContent), form);
        if(form.getGroups() != null && form.getGroups().size() > 0 && !form.getGroups().get(0).getLabel().equals(FieldAdapter.FORM_WITHOUT_SECTION))
            DataSetMetaData.removeFieldsWithInvalidCategoryOptionRelation(form,
                    DataSetCategoryOptionParser.parse(jsonContent));

        boolean isApproved = DataSetApprovals.download(context, info.getFormId(), info.getPeriod(), info.getOrgUnitId());
        form.setApproved(isApproved);
    }
}
