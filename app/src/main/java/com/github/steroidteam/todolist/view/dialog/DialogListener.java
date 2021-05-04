package com.github.steroidteam.todolist.view.dialog;

public interface DialogListener {

    /** Method to call when the user, click on the positive button. */
    void onPositiveClick();

    /**
     * Method to call when the user, click on the positive button and we want to return an input.
     */
    void onPositiveClick(String input);

    /** Method to call when the user, click on the negative button. */
    void onNegativeClick();
}
