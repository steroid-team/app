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
    private boolean initiated = false;

    private void init(Fragment fragment, TodoListViewModel viewModel) {
        List<Tag> localTags = viewModel.getTagsFromList();
        List<Tag> globalTags = viewModel.getUnlinkedTags();

        localTags.sort(Tag.sortByBody);
        globalTags.sort(Tag.sortByBody);
        System.out.println("Local tags : ");
        for (int i = 0; i < localTags.size(); i++) {
            System.out.println(localTags.get(i));
        }
        System.out.println("Global tags : ");
        for (int i = 0; i < globalTags.size(); i++) {
            System.out.println(globalTags.get(i));
        }

        LinearLayout localRow = fragment.getView().findViewById(R.id.tag_row_local);
        LinearLayout globalRow = fragment.getView().findViewById(R.id.tag_row_global);
        localRow.removeAllViews();
        globalRow.removeAllViews();
        Button plusButton =
                new Button(
                        new ContextThemeWrapper(fragment.getContext(), R.style.TagInList), null, 0);
        plusButton.setText("+");
        plusButton.setBackgroundTintList(ColorStateList.valueOf(Color.LTGRAY));
        plusButton.setOnClickListener(v -> createTag(fragment, viewModel));
        globalRow.addView(plusButton);
        localTags.forEach(
                tag -> createLocalTagButton(fragment, tag, localRow, globalRow, viewModel));
        globalTags.forEach(
                tag -> createGlobalTagButton(fragment, tag, localRow, globalRow, viewModel));
        initiated = true;
    }

    public void tagButton(Fragment fragment, TodoListViewModel viewModel) {
        ConstraintLayout tagLayout = fragment.getView().findViewById(R.id.layout_update_tags);
        tagLayout.setVisibility(View.VISIBLE);
        if (!initiated) {
            init(fragment, viewModel);
        }
    }

    public void tagSaveButton(Fragment fragment) {
        ConstraintLayout tagLayout = fragment.getView().findViewById(R.id.layout_update_tags);
        tagLayout.setVisibility(View.GONE);
    }

    private void createLocalTagButton(
            Fragment fragment,
            Tag tag,
            LinearLayout localRow,
            LinearLayout globalRow,
            TodoListViewModel viewModel) {
        Button tagButton =
                new Button(
                        new ContextThemeWrapper(fragment.getContext(), R.style.TagInList), null, 0);
        tagButton.setText(tag.getBody());
        tagButton.setBackgroundTintList(ColorStateList.valueOf(tag.getColor()));
        tagButton.setOnClickListener(
                view -> {
                    viewModel.removeTagFromTodolist(tag);
                    localRow.removeView(tagButton);
                    globalRow.addView(tagButton);
                });
        localRow.addView(tagButton);
    }

    private void createGlobalTagButton(
            Fragment fragment,
            Tag tag,
            LinearLayout localRow,
            LinearLayout globalRow,
            TodoListViewModel viewModel) {
        Button tagButton =
                new Button(
                        new ContextThemeWrapper(fragment.getContext(), R.style.TagInList), null, 0);
        tagButton.setText(tag.getBody());
        tagButton.setBackgroundTintList(ColorStateList.valueOf(tag.getColor()));
        tagButton.setOnClickListener(
                view -> {
                    viewModel.putTagInTodolist(tag);
                    globalRow.removeView(tagButton);
                    localRow.addView(tagButton);
                });
        tagButton.setOnLongClickListener(
                view -> {
                    globalRow.removeView(tagButton);
                    viewModel.destroyTag(tag);
                    return true;
                });
        globalRow.addView(tagButton);
    }

    public void createTag(Fragment fragment, TodoListViewModel viewModel) {

        DialogListener dialogListener =
                new DialogListener() {

                    @Override
                    public void onPositiveClick(String title) {
                        if (title.length() > 0) {
                            Tag tag = new Tag(title);
                            viewModel.putTag(tag);
                            createGlobalTagButton(
                                    fragment,
                                    tag,
                                    fragment.getView().findViewById(R.id.tag_row_local),
                                    fragment.getView().findViewById(R.id.tag_row_global),
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
