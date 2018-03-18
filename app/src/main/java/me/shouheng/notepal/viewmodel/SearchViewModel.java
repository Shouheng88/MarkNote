package me.shouheng.notepal.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import java.util.LinkedList;
import java.util.List;

import me.shouheng.notepal.async.ResourceAsyncTask;
import me.shouheng.notepal.model.MindSnagging;
import me.shouheng.notepal.model.Note;
import me.shouheng.notepal.model.data.Resource;
import me.shouheng.notepal.repository.QueryRepository;
import me.shouheng.notepal.util.tools.SearchConditions;
import me.shouheng.notepal.util.tools.SearchResult;

/**
 * Created by shouh on 2018/3/18.*/
public class SearchViewModel extends ViewModel {

    /**
     * Use the default conditions if the condition not set. */
    private SearchConditions conditions = SearchConditions.getDefaultConditions();

    public void setConditions(SearchConditions conditions) {
        this.conditions = conditions;
    }

    public LiveData<Resource<SearchResult>> getSearchResult(String queryText) {
        MutableLiveData<Resource<SearchResult>> result = new MutableLiveData<>();
        new ResourceAsyncTask<>(result, () -> {
            QueryRepository queryRepository = new QueryRepository(conditions);

            List<Note> notes = new LinkedList<>();
            if (conditions.isIncludeNote()) {
                notes = queryRepository.getNotes(queryText);
            }
            List<MindSnagging> snaggings = new LinkedList<>();
            if (conditions.isIncludeMindSnagging()) {
                snaggings = queryRepository.getMindSnaggings(queryText);
            }

            SearchResult searchResult = new SearchResult(notes, snaggings);

            return Resource.success(searchResult);
        }).execute();
        return result;
    }
}
