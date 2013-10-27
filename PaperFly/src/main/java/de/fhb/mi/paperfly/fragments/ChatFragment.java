package de.fhb.mi.paperfly.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import de.fhb.mi.paperfly.R;

/**
 * @author Christoph Ott
 */
public class ChatFragment extends Fragment {

    private View rootView;
    private ListView messagesList;
    private EditText messageInput;
    private ImageButton buSend;
    private ArrayAdapter<String> messagesAdapter;

    public static String ROOM_GLOBAL = "Global";
    public static final String ARG_CHAT_ROOM = "chat_room";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        String room = getArguments().getString(ARG_CHAT_ROOM);
        this.rootView = inflater.inflate(R.layout.fragment_chat, container, false);
        initViewsById();

        messagesAdapter = new ArrayAdapter<String>(rootView.getContext(), android.R.layout.simple_list_item_1);

        messageInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    buSend.setAlpha(1.0f);
                    buSend.setClickable(true);
                } else {
                    buSend.setAlpha(0.5f);
                    buSend.setClickable(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        // make button not clickable
        buSend.setAlpha(0.5f);
        buSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                messagesAdapter.add(messageInput.getText().toString());
                messagesAdapter.notifyDataSetChanged();
                messageInput.setText("");
            }
        });
        buSend.setClickable(false);

        messagesList.setAdapter(messagesAdapter);
        messagesList.setStackFromBottom(true);
        messagesList.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);

        return rootView;
    }

    private void initViewsById() {
        messagesList = (ListView) this.rootView.findViewById(R.id.messagesList);
        messageInput = (EditText) this.rootView.findViewById(R.id.messageInput);
        buSend = (ImageButton) this.rootView.findViewById(R.id.buSend);
    }
}
