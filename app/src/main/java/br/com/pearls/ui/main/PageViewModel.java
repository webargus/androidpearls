package br.com.pearls.ui.main;

import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class PageViewModel extends ViewModel {

    private final String[] PAGE_NAMES = {"Search", "Languages"};
    private MutableLiveData<Integer> mIndex = new MutableLiveData<>();
    private LiveData<JSONArray> mJson = Transformations.map(mIndex, new Function<Integer, JSONArray>() {
        @Override
        public JSONArray apply(Integer input) {
            JSONObject object = new JSONObject();
            try {
                object.put("section", PAGE_NAMES[input]);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            JSONArray jsonArray = new JSONArray();
            jsonArray.put(object);
            return jsonArray;
        }
    });

    public void setIndex(int index) {
        mIndex.setValue(index);
    }

    public Integer getIndex() {
        return mIndex.getValue();
    }

    public LiveData<JSONArray> getJson() {
        return mJson;
    }
}