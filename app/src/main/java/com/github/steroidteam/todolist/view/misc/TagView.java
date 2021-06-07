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
import java.util.List;

public class TagView {
    public void tagButton(Fragment fragment, TodoListViewModel viewModel) {
        List<Tag> tags = viewModel.getTags();
        tags.sort(Tag.sortByBody);
        ConstraintLayout tagLayout = fragment.getView().findViewById(R.id.layout_update_tags);
        tagLayout.setVisibility(View.VISIBLE);
        LinearLayout row = fragment.getView().findViewById(R.id.tag_row_first);
        row.removeAllViews();
        Button plusButton =
                new Button(
                        new ContextThemeWrapper(fragment.getContext(), R.style.TagInList), null, 0);
        plusButton.setText("+");
        plusButton.setBackgroundTintList(ColorStateList.valueOf(Color.LTGRAY));
        plusButton.setOnClickListener(v -> createTag(fragment, viewModel));
        row.addView(plusButton);
        tags.forEach(tag -> createTagButton(fragment, tag, row, viewModel));
    }

    public void tagSaveButton(Fragment fragment) {
        ConstraintLayout tagLayout = fragment.getView().findViewById(R.id.layout_update_tags);
        tagLayout.setVisibility(View.GONE);
    }

    private void createTagButton(
            Fragment fragment, Tag tag, LinearLayout row, TodoListViewModel viewModel) {
        Button tagButton =
                new Button(
                        new ContextThemeWrapper(fragment.getContext(), R.style.TagInList), null, 0);
        tagButton.setText(tag.getBody());
        tagButton.setBackgroundTintList(ColorStateList.valueOf(tag.getColor()));
        tagButton.setOnLongClickListener(
                view -> {
                    viewModel.destroyTag(tag);
                    row.removeView(tagButton);
                    return true;
                });
        row.addView(tagButton);
    }

    public void createTag(Fragment fragment, TodoListViewModel viewModel) {

        DialogListener dialogListener =
                new DialogListener() {

                    @Override
                    public void onPositiveClick(String title) {
                        if (title.length() > 0) {
                            Tag tag = new Tag(title);
                            viewModel.addTag(tag);
                            createTagButton(
                                    fragment,
                                    tag,
                                    fragment.getView().findViewById(R.id.tag_row_first),
                                    viewModel);
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
