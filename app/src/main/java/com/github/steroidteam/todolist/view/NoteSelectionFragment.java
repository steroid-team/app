package com.github.steroidteam.todolist.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.github.steroidteam.todolist.R;
import com.github.steroidteam.todolist.model.notes.Note;
import com.github.steroidteam.todolist.model.user.UserFactory;
import com.github.steroidteam.todolist.view.adapter.NoteArrayListAdapter;
import com.github.steroidteam.todolist.view.dialog.DialogListener;
import com.github.steroidteam.todolist.view.dialog.InputDialogFragment;
import com.github.steroidteam.todolist.view.dialog.SimpleDialogFragment;
import com.github.steroidteam.todolist.view.misc.SwipeTouchHelper;
import com.github.steroidteam.todolist.viewmodel.NoteViewModel;
import com.github.steroidteam.todolist.viewmodel.NoteViewModelFactory;
import com.github.steroidteam.todolist.viewmodel.ViewModelFactoryInjection;
import java.io.File;
import java.util.Optional;
import java.util.UUID;

public class NoteSelectionFragment extends Fragment {

    private NoteViewModel viewModel;
    private NoteArrayListAdapter adapter;

    public static final String NOTE_ID_KEY = "id";

    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View root = inflater.inflate(R.layout.fragment_note_selection, container, false);

        String headerFilePath = getActivity().getCacheDir().getAbsolutePath();
        File imagePath =
                new File(headerFilePath, "user-data/" + UserFactory.get().getUid() + "/images/");

        root.findViewById(R.id.create_note_button).setOnClickListener(this::createNote);

        RecyclerView recyclerView = root.findViewById(R.id.activity_noteselection_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);

        adapter = new NoteArrayListAdapter(createCustomListener());
        recyclerView.setAdapter(adapter);

        NoteViewModelFactory noteViewModelFactory =
                ViewModelFactoryInjection.getNoteViewModelFactory(getContext());
        viewModel =
                new ViewModelProvider(requireActivity(), noteViewModelFactory)
                        .get(NoteViewModel.class);
        viewModel
                .getNoteList()
                .observe(
                        getViewLifecycleOwner(),
                        (noteList) -> {
                            for (Note note : noteList) {
                                Optional<UUID> optionalUUID = note.getHeaderID();
                                optionalUUID.ifPresent(
                                        uuid ->
                                                viewModel
                                                        .getNoteHeader(
                                                                uuid, imagePath.getAbsolutePath())
                                                        .thenAccept(
                                                                f -> {
                                                                    adapter.putHeaderPath(
                                                                            note.getId(), f);
                                                                    adapter.notifyDataSetChanged();
                                                                }));
                            }
                            adapter.setNoteList(noteList);
                            adapter.notifyDataSetChanged();
                        });

        createAndSetSwipeListener(recyclerView);

        return root;
    }

    private NoteArrayListAdapter.NoteHolder.NoteCustomListener createCustomListener() {
        return holder -> {
            viewModel.selectNote(holder.getNote().getId());
            Navigation.findNavController(holder.itemView)
                    .navigate(R.id.nav_note_display, new Bundle());
        };
    }

    private void createAndSetSwipeListener(RecyclerView recyclerView) {
        SwipeTouchHelper.SwipeListener swipeListener =
                new SwipeTouchHelper.SwipeListener() {
                    @Override
                    public void onSwipeLeft(RecyclerView.ViewHolder viewHolder, int position) {
                        removeNote(
                                ((NoteArrayListAdapter.NoteHolder) viewHolder).getNote(), position);
                    }

                    @Override
                    public void onSwipeRight(RecyclerView.ViewHolder viewHolder, int position) {
                        renameNote(
                                ((NoteArrayListAdapter.NoteHolder) viewHolder).getNote(), position);
                    }
                };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeTouchHelper(swipeListener));
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    private void createNote(View view) {

        DialogListener dialogListener =
                new DialogListener() {

                    @Override
                    public void onPositiveClick(String title) {
                        if (title.length() > 0) viewModel.putNote(title);
                    }

                    @Override
                    public void onPositiveClick() {
                        // NEVER CALLED
                    }

                    @Override
                    public void onNegativeClick() {
                        // DO NOTHING
                    }
                };

        DialogFragment newFragment =
                new InputDialogFragment().newInstance(dialogListener, R.string.add_note_suggestion);
        newFragment.show(getParentFragmentManager(), "add_dialog");
    }

    private void removeNote(final Note note, final int position) {
        DialogListener simpleDialogListener =
                new DialogListener() {

                    @Override
                    public void onPositiveClick(String input) {
                        // NEVER CALLED
                    }

                    @Override
                    public void onPositiveClick() {
                        viewModel.removeNote(note.getId(), note.getHeaderID());
                    }

                    @Override
                    public void onNegativeClick() {
                        adapter.notifyItemChanged(position);
                    }
                };

        DialogFragment newFragment =
                new SimpleDialogFragment()
                        .newInstance(simpleDialogListener, R.string.delete_note_suggestion);
        newFragment.show(getParentFragmentManager(), "deletion_dialog");
    }

    public void renameNote(Note note, final int position) {

        DialogListener dialogListener =
                new DialogListener() {

                    @Override
                    public void onPositiveClick(String title) {
                        if (title.length() > 0)
                            viewModel.renameNote(note.getId(), note.setTitle(title));
                    }

                    @Override
                    public void onPositiveClick() {
                        // NEVER CALLED
                    }

                    @Override
                    public void onNegativeClick() {
                        adapter.notifyItemChanged(position);
                    }
                };

        DialogFragment newFragment =
                new InputDialogFragment()
                        .newInstance(dialogListener, R.string.rename_note_suggestion);
        newFragment.show(getParentFragmentManager(), "rename_dialog");
    }
}
