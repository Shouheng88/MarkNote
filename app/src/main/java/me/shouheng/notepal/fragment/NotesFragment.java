package me.shouheng.notepal.fragment;

import android.os.Bundle;

import me.shouheng.notepal.R;
import me.shouheng.notepal.databinding.FragmentNotesBinding;


public class NotesFragment extends CommonFragment<FragmentNotesBinding> {

    public static NotesFragment newInstance() {
        Bundle args = new Bundle();
        NotesFragment fragment = new NotesFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_notes;
    }

    @Override
    protected void doCreateView(Bundle savedInstanceState) {

    }
}
