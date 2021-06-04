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
import java.util.UUID;

public class TagView {
    private Fragment fragment;
    private View root;
    private TodoListViewModel viewModel;
    private ConstraintLayout tagLayout;
    private LinearLayout localRow;
    private LinearLayout globalRow;
    private List<Tag> localTags;
    private List<Tag> globalTags;
    private UUID todolistId;

    public TagView(Fragment fragment, TodoListViewModel viewModel, View root) {
        this.fragment = fragment;
        this.root = root;
        this.viewModel = viewModel;
        todolistId = viewModel.getTodoList().getValue().getId();
        localTags = viewModel.getLocalTags();
        globalTags = viewModel.getUnlinkedTags();
        tagLayout = root.findViewById(R.id.layout_update_tags);
        localRow = root.findViewById(R.id.tag_row_local);
        globalRow = root.findViewById(R.id.tag_row_global);
        init();
    }

    private void init() {
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
        localRow.removeAllViews();
        globalRow.removeAllViews();
        Button plusButton =
                new Button(
                        new ContextThemeWrapper(fragment.getContext(), R.style.TagInList), null, 0);
        plusButton.setText("+");
        plusButton.setBackgroundTintList(ColorStateList.valueOf(Color.LTGRAY));
        plusButton.setOnClickListener(v -> createTag());
        globalRow.addView(plusButton);
        localTags.forEach(tag -> createLocalTagButton(tag));
        globalTags.forEach(tag -> createGlobalTagButton(tag));
    }

    public void tagButton() {
        tagLayout.setVisibility(View.VISIBLE);
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

                    globalRow.removeView(tagButton);
                    createLocalTagButton(tag);
                });
        tagButton.setOnLongClickListener(
                view -> {
                    globalRow.removeView(tagButton);
                    viewModel.destroyTag(tag);
                    return true;
                });
        globalRow.addView(tagButton);
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
