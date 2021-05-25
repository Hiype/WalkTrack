package com.hiype.walktrack.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.Toast;

import com.hiype.walktrack.Base;
import com.hiype.walktrack.DBHelper;
import com.hiype.walktrack.FriendUser;
import com.hiype.walktrack.MySqlApi;
import com.hiype.walktrack.R;
import com.hiype.walktrack.User;
import com.hiype.walktrack.adapters.FriendsListAdapter;
import com.hiype.walktrack.adapters.UserListAdapter;
import com.hiype.walktrack.http.RequestHandler;
import com.hiype.walktrack.http.WebServiceURL;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FriendsAddFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FriendsAddFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private EditText searchUser;
    private RecyclerView friendsAddList;
    private JSONObject obj;
    private UserListAdapter userListAdapter;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public FriendsAddFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FriendsAddFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FriendsAddFragment newInstance(String param1, String param2) {
        FriendsAddFragment fragment = new FriendsAddFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(View view ,Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        searchUser = (EditText) view.findViewById(R.id.searchUser);
        friendsAddList = (RecyclerView) view.findViewById(R.id.friends_add_list);

        searchUser.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length() >= 3) {
                    findUser(view.getContext());
                }

            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_friends_add, container, false);
    }

    private void findUser(Context context) {
        //First getting the values
        final String input_username = searchUser.getText().toString();

        if(input_username.isEmpty() && userListAdapter != null) {
            userListAdapter.clear();
            return;
        }

        ArrayList<FriendUser> searchResultArray;

        try {
            searchResultArray = MySqlApi.findUser(input_username, context);
        } catch (Exception e) {
            searchResultArray = new ArrayList<>();
            e.printStackTrace();
        }

        if(searchResultArray.size() > 0) {
            friendsAddList.setLayoutManager(new LinearLayoutManager(context));
            userListAdapter = new UserListAdapter(searchResultArray);

            if(userListAdapter.getItemCount() > 0) {
                friendsAddList.setAdapter(userListAdapter);
        } else {
            Toast.makeText(getContext(), "No user found!", Toast.LENGTH_SHORT).show();
        }
    }
}}