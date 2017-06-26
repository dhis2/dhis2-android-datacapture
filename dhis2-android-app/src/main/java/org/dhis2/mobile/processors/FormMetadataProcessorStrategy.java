package org.dhis2.mobile.processors;


import android.content.Context;

import org.dhis2.mobile.io.holders.DatasetInfoHolder;
import org.dhis2.mobile.io.json.ParsingException;
import org.dhis2.mobile.io.models.Form;
import org.dhis2.mobile.network.NetworkException;
import org.dhis2.mobile.utils.PrefUtils;

class FormMetadataProcessorStrategy {

    public static void process(Context context, Form form, DatasetInfoHolder info)
            throws ParsingException, NetworkException {
        String jsonContent = DataSetMetaData.download(context, info.getFormId(), PrefUtils.getServerVersion(context).equals("2.25"));
        DataSetMetaData.addCompulsoryDataElements(
                DataElementOperandParser.parse(jsonContent), form);
        DataSetMetaData.removeFieldsWithInvalidCategoryOptionRelation(form,
                DataSetCategoryOptionParser.parse(jsonContent));
    }
}
