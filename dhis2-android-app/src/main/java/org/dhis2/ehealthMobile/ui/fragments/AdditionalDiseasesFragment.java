package org.dhis2.ehealthMobile.ui.fragments;

import android.app.Dialog;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.dhis2.ehealthMobile.R;
import org.dhis2.ehealthMobile.io.models.Form;
import org.dhis2.ehealthMobile.io.models.eidsr.Disease;
import org.dhis2.ehealthMobile.ui.activities.DataEntryActivity;
import org.dhis2.ehealthMobile.utils.DiseaseImporter;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by george on 9/28/16.
 */

public class AdditionalDiseasesFragment extends BottomSheetDialogFragment {

    private ListView listview;
    private ArrayList<String> additionalDiseases;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> additionalDiseasesIds;
    private Map diseases;
    //Diseases already displayed in the DataEntryActivity listView.
    private String alreadyDisplayed;
    private String formId;
    @Override
    public void setupDialog(final Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        View contentView = View.inflate(getContext(), R.layout.fragment_bottomsheet, null);
        dialog.setContentView(contentView);

        alreadyDisplayed = getArguments().getString(DataEntryActivity.ALREADY_DISPLAYED);
        formId = getArguments().getString(Form.TAG);
        listview = (ListView) dialog.findViewById(R.id.additional_diseases_listview);
        setupListView();
        setupListViewOnclickListener();


    }
    private void setupListView(){
        additionalDiseases = new ArrayList<>();
        additionalDiseasesIds = new ArrayList<>();
        diseases = DiseaseImporter.importDiseases(getContext(), formId);

        assert diseases != null;
        for (Object key : diseases.keySet()) {
            Disease disease = (Disease) diseases.get(key);
            if(disease.isAdditionalDisease() && !alreadyDisplayed.contains(disease.getId())){
                additionalDiseases.add(disease.getLabel().split("EIDSR-")[1]);
                additionalDiseasesIds.add(disease.getId());
            }
        }

        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_expandable_list_item_1, additionalDiseases);
        listview.setAdapter(adapter);

    }
    private void setupListViewOnclickListener(){
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                DataEntryActivity dataEntryActivity = (DataEntryActivity) getActivity();
                Disease disease = (Disease) diseases.get(additionalDiseasesIds.get(i));

                dataEntryActivity.adapters.get(0).addItem(disease);
                dataEntryActivity.addToDiseasesShown(disease.getId(), disease.getLabel());
                dataEntryActivity.scrollToBottomOfListView();
                dismiss();

            }
        });
    }
}
