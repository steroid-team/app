package com.github.steroidteam.todolist.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.github.steroidteam.todolist.R;

public class CreditsFragment extends Fragment {

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View root = inflater.inflate(R.layout.fragment_credits, container, false);

        TextView textView = root.findViewById(R.id.credits_body);

        textView.setText(
                "Copyright (C) 2021 González Gómez Yago, Graziano Leandro, Hauke Sydney Victor "
                        + "Balthazar, Kaltenrieder Noah Alec Daniel, Pellenc Vincent Jean, Théo Damiani\n\n"
                        + "Copyright (C) 2012 Android Holo ColorPicker, Lars Werkman\n\n"
                        + "Rich Editor used in the application:\n"
                        + "Copyright (C) 2020 Wasabeef\n"
                        + "\n"
                        + "Licensed under the Apache License, Version 2.0 (the \"License\");\n"
                        + "you may not use this file except in compliance with the License.\n"
                        + "You may obtain a copy of the License at\n"
                        + "\n"
                        + "   http://www.apache.org/licenses/LICENSE-2.0\n"
                        + "\n"
                        + "Unless required by applicable law or agreed to in writing, software\n"
                        + "distributed under the License is distributed on an \"AS IS\" BASIS,\n"
                        + "WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n"
                        + "See the License for the specific language governing permissions and\n"
                        + "limitations under the License."
                        + "\n"
                        + "\n"
                        + "    \"Search\" icon by Miss Jannes from thenounproject.com\n"
                        + "    \"Bold\" icon by Gregor Cresnar from thenounproject.com\n"
                        + "    \"Italics\" icon by Gregor Cresnar from thenounproject.com\n"
                        + "    \"Underline\" icon by Gregor Cresnar from thenounproject.com\n"
                        + "    \"Strike out text\" icon by Gregor Cresnar from thenounproject.com\n"
                        + "    \"Numeric order\" icon by Gregor Cresnar from thenounproject.com\n"
                        + "    \"Numbering\" icon (modified version) by Gregor Cresnar from thenounproject.com\n"
                        + "    \"Photography\" icon by Gregor Cresnar from thenounproject.com\n"
                        + "    \"Quotes\" icon by Gregor Cresnar from thenounproject.com\n"
                        + "    \"Pencil\" icon by Gregor Cresnar from thenounproject.com\n"
                        + "    \"Clock\" icon by Gregor Cresnar from thenounproject.com\n"
                        + "    \"Image\" icon by Gregor Cresnar from thenounproject.com\n"
                        + "    \"Location\" icon by Gregor Cresnar from thenounproject.com\n"
                        + "    \"Microphone\" icon by Gregor Cresnar from thenounproject.com\n"
                        + "    \"Label\" icon by Gregor Cresnar from thenounproject.com\n");

        return root;
    }
}
