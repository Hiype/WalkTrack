package com.hiype.walktrack.fragments;

import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hiype.walktrack.DBHelper;
import com.hiype.walktrack.FriendUser;
import com.hiype.walktrack.MySqlApi;
import com.hiype.walktrack.R;
import com.hiype.walktrack.adapters.FriendsListAdapter;
import com.hiype.walktrack.adapters.UserListAdapter;
import com.hiype.walktrack.http.RequestHandler;
import com.hiype.walktrack.http.WebServiceURL;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FriendsListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FriendsListFragment extends Fragment {

    public static final String ARG_OBJECT = "object";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private RecyclerView friendsList;
    private FriendsListAdapter friendsListAdapter;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public FriendsListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FriendsListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FriendsListFragment newInstance(String param1, String param2) {
        FriendsListFragment fragment = new FriendsListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
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
    public void onViewCreated(View view ,Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        DBHelper db = new DBHelper(getContext());

        friendsList = (RecyclerView) view.findViewById(R.id.friends_list);

        if(friendsList == null) {
            Log.e("FriendsListFragment", "Friends list was null");
            return;
        }

        findFriend();

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_friends_list, container, false);
    }

    private void findFriend() {

        ArrayList<FriendUser> searchResultArray = MySqlApi.findFriend(getContext());

        friendsList.setLayoutManager(new LinearLayoutManager(getContext()));
        friendsListAdapter = new FriendsListAdapter(searchResultArray);

        if(friendsListAdapter.getItemCount() > 0) {
            friendsList.setAdapter(friendsListAdapter);
        } else {
            TextView no_friends_text =  (TextView) requireView().findViewById(R.id.no_friends_text);
            ImageView no_friends_image =  (ImageView) requireView().findViewById(R.id.no_friends_image);

            no_friends_text.setVisibility(View.VISIBLE);
            no_friends_image.setVisibility(View.VISIBLE);
        }
    }
}