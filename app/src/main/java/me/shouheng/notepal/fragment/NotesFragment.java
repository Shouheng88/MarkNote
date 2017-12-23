package me.shouheng.notepal.fragment;

import android.os.Bundle;

import me.shouheng.notepal.R;
import me.shouheng.notepal.databinding.FragmentNotesBinding;


public class NotesFragment extends CommonFragment<FragmentNotesBinding> {

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_notes;
    }

    @Override
    protected void doCreateView(Bundle savedInstanceState) {

    }
}
