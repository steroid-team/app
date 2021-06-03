package com.github.steroidteam.todolist.view.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.format.DateFormat;
import android.text.style.DynamicDrawableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.text.style.RelativeSizeSpan;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.github.steroidteam.todolist.R;
import com.github.steroidteam.todolist.model.todo.Task;
import com.github.steroidteam.todolist.model.todo.TodoList;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.Objects;

public class TodoAdapter extends RecyclerView.Adapter<TodoAdapter.TaskHolder> {

    private TodoList todoList;
    private TaskCustomListener listener;
    private Map<Task, Integer> taskIntegerMap;

    public TodoAdapter(TaskCustomListener listener, Map<Task, Integer> taskIntegerMap) {
        this.listener = listener;
        this.taskIntegerMap = taskIntegerMap;
    }

    @NonNull
    @Override
    public TaskHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutNotDone =
                LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.layout_task_item, parent, false);
        return new TaskHolder(layoutNotDone);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskHolder holder, int position) {
        Task currentTask = todoList.getTask(position);

        holder.setPosition(taskIntegerMap.get(currentTask));

        Context context = holder.itemView.getContext();
        SpannableString taskBodySS = new SpannableString(currentTask.getBody());
        taskBodySS.setSpan(
                new ForegroundColorSpan(fetchTextPrimaryColour(context)),
                0,
                taskBodySS.length(),
                Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        holder.taskBody.setText(taskBodySS);

        if (currentTask.getDueDate() != null) {
            SpannableString dueDate = createDateSpan(context, currentTask.getDueDate());

            // Add some margin between the task's body and the due date.
            holder.taskBody.append("   ");
            // Insert the span with the due date.
            holder.taskBody.append(dueDate);
        }
        holder.taskBox.setChecked(currentTask.isDone());

        if (currentTask.isDone()) {
            holder.taskBody.setTextColor(Color.LTGRAY);
            holder.taskBody.setPaintFlags(
                    holder.taskBody.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.displayDeleteButton();
        } else {
            holder.taskBody.setTextColor(Color.DKGRAY);
            holder.taskBody.setPaintFlags(0);
            holder.hideDeleteButton();
        }
    }

    private SpannableString createDateSpan(Context context, Date date) {
        int lightGrey = ContextCompat.getColor(context, R.color.light_grey);
        // The ImageSpan works by replacing a substring by the image (in this case, the clock
        // icon). We add two leading spaces here to:
        //  1) Keep useful text from being overwritten by the image.
        //  2) Add a little margin between the icon and the date.
        SpannableString dueDate = new SpannableString("  " + formatDate(date));
        // Make the due date 75% the size of the task's body.
        dueDate.setSpan(
                new RelativeSizeSpan(0.75f), 0, dueDate.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        // Tint the clock icon with the same colour as the text (light grey).
        Drawable clockIcon =
                DrawableCompat.wrap(
                        Objects.requireNonNull(
                                ContextCompat.getDrawable(context, R.drawable.clock_icon)));
        DrawableCompat.setTint(clockIcon, lightGrey);
        clockIcon.setBounds(0, 0, clockIcon.getIntrinsicWidth(), clockIcon.getIntrinsicHeight());
        // Add the clock icon to the start of the span.
        dueDate.setSpan(
                new ImageSpan(clockIcon, DynamicDrawableSpan.ALIGN_BASELINE),
                0,
                1,
                Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        // Make the text's colour light grey as well.
        dueDate.setSpan(
                new ForegroundColorSpan(lightGrey),
                0,
                dueDate.length(),
                Spanned.SPAN_EXCLUSIVE_INCLUSIVE);

        return dueDate;
    }

    @Override
    public int getItemCount() {
        return todoList.getSize();
    }

    private static CharSequence formatDate(Date date) {
        Calendar currentDate = Calendar.getInstance();
        Calendar inputDate = Calendar.getInstance();
        inputDate.setTime(date);

        String format = "yyyy-MM-dd HH:mm";

        // Remove the year if it matches the current one.
        if (currentDate.get(Calendar.YEAR) == inputDate.get(Calendar.YEAR)) {
            format = format.replace("yyyy-", "");

            // Just display the time if the date is today.
            if (currentDate.get(Calendar.DAY_OF_YEAR) == inputDate.get(Calendar.DAY_OF_YEAR)) {
                format = format.replace("MM-dd ", "");
            }
        }

        return DateFormat.format(format, date);
    }

    public void setTodoList(TodoList todoList) {
        // Updates the adapter with the new todoList (the observable one)
        // this.todoList = todoList;
        this.todoList = todoList.sortByDate();
        // Check notifyDataSetChanged() might not be the best function
        // considering performance
        notifyDataSetChanged();
    }

    public interface TaskCustomListener {
        void onItemClick(final int position);

        void onItemDelete(final int position);

        void onCheckedChangedCustom(int position, boolean isChecked);
    }

    public class TaskHolder extends RecyclerView.ViewHolder {

        private final TextView taskBody;
        private final CheckBox taskBox;
        private final ImageButton taskDelete;
        private int position;

        public TaskHolder(@NonNull View itemView) {
            super(itemView);
            taskBody = itemView.findViewById(R.id.layout_task_body);
            taskBox = itemView.findViewById(R.id.layout_task_checkbox);
            taskDelete = itemView.findViewById(R.id.layout_task_delete_button);

            taskBox.setOnClickListener(
                    (v) -> {
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onCheckedChangedCustom(position, taskBox.isChecked());
                        }
                    });

            taskDelete.setOnClickListener(v -> listener.onItemDelete(position));

            itemView.setOnClickListener(
                    (v) -> {
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                        }
                    });
        }

        public void setPosition(int position) {
            this.position = position;
        }

        public void displayDeleteButton() {
            taskDelete.setVisibility(View.VISIBLE);
        }

        public void hideDeleteButton() {
            taskDelete.setVisibility(View.GONE);
        }
    }

    static int fetchTextPrimaryColour(Context context) {
        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = context.getTheme();
        theme.resolveAttribute(android.R.attr.textColorPrimary, typedValue, true);
        TypedArray arr =
                context.obtainStyledAttributes(
                        typedValue.data, new int[] {android.R.attr.textColorPrimary});
        int primaryColor = arr.getColor(0, -1);
        arr.recycle();
        return primaryColor;
    }
}
