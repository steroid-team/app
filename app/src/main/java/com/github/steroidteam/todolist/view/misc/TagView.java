package com.github.steroidteam.todolist.view.misc;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import com.github.steroidteam.todolist.R;
import com.github.steroidteam.todolist.model.todo.Tag;
import com.github.steroidteam.todolist.view.dialog.DialogListener;
import com.github.steroidteam.todolist.view.dialog.InputDialogFragment;
import com.github.steroidteam.todolist.viewmodel.TodoListViewModel;
import java.util.ArrayList;
import java.util.List;

public class TagView {
    private final Fragment fragment;
    private final TodoListViewModel viewModel;
    private final ConstraintLayout tagLayout;
    private final LinearLayout localRow;
    private final LinearLayout unlinkedRow;
    private List<Tag> localTags;
    private List<Tag> globalTags;
    private List<Tag> unlinkedTags;

    public TagView(Fragment fragment, TodoListViewModel viewModel, View root) {
        this.fragment = fragment;
        this.viewModel = viewModel;
        tagLayout = root.findViewById(R.id.layout_update_tags);
        localRow = root.findViewById(R.id.tag_row_local);
        unlinkedRow = root.findViewById(R.id.tag_row_global);
        init();
    }

    private void init() {
        localTags = viewModel.getTagsFromList();
        if (localTags == null) {
            localTags = new ArrayList<>();
        }
        globalTags = viewModel.getGlobalTags();
        if (globalTags == null) {
            globalTags = new ArrayList<>();
        }
        unlinkedTags = globalTags;
        for (int i = 0; i < localTags.size(); i++) {
            Tag localTag = localTags.get(i);
            for (int j = 0; j < globalTags.size(); j++) {
                Tag globalTag = globalTags.get(j);
                if (localTag.equals(globalTag)) {
                    unlinkedTags.remove(globalTag);
                }
            }
        }
        localTags.sort(Tag.sortByBody);
        unlinkedTags.sort(Tag.sortByBody);
        localRow.removeAllViews();
        unlinkedRow.removeAllViews();
        Button plusButton =
                new Button(
                        new ContextThemeWrapper(fragment.getContext(), R.style.TagInList), null, 0);
        plusButton.setText("+");
        plusButton.setBackgroundTintList(ColorStateList.valueOf(Color.LTGRAY));
        plusButton.setOnClickListener(v -> createTag());
        unlinkedRow.addView(plusButton);
        localTags.forEach(this::createLocalTagButton);
        unlinkedTags.forEach(this::createGlobalTagButton);
    }

    public void tagButton() {
        tagLayout.setVisibility(View.VISIBLE);
        init();
    }

    public void tagSaveButton() {
        tagLayout.setVisibility(View.GONE);
    }

    private void createLocalTagButton(Tag tag) {
        Button tagButton =
                new Button(
                        new ContextThemeWrapper(fragment.getContext(), R.style.TagInList), null, 0);
        tagButton.setText(tag.getBody());
        tagButton.setBackgroundTintList(ColorStateList.valueOf(tag.getColor()));
        tagButton.setOnClickListener(
                view -> {
                    viewModel.removeTagFromTodolist(tag);
                    localRow.removeView(tagButton);
                    createGlobalTagButton(tag);
                });
        localRow.addView(tagButton);
    }

    private void createGlobalTagButton(Tag tag) {
        Button tagButton =
                new Button(
                        new ContextThemeWrapper(fragment.getContext(), R.style.TagInList), null, 0);
        tagButton.setText(tag.getBody());
        tagButton.setBackgroundTintList(ColorStateList.valueOf(tag.getColor()));
        tagButton.setOnClickListener(
                view -> {
                    viewModel.putTagInTodolist(tag);

                    unlinkedRow.removeView(tagButton);
                    createLocalTagButton(tag);
                });
        tagButton.setOnLongClickListener(
                view -> {
                    unlinkedRow.removeView(tagButton);
                    viewModel.destroyTag(tag);
                    return true;
                });
        unlinkedRow.addView(tagButton);
    }

    public void createTag() {

        DialogListener dialogListener =
                new DialogListener() {

                    @Override
                    public void onPositiveClick(String title) {
                        if (title.length() > 0) {
                            Tag tag = new Tag(title);
                            viewModel.putTag(tag);
                            createGlobalTagButton(tag);
                        }
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
                new InputDialogFragment().newInstance(dialogListener, R.string.add_tag_suggestion);
        newFragment.show(fragment.getParentFragmentManager(), "add_tag_dialog");
    }
}
