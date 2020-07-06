
package com.emot.emotionPlayer.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.afollestad.appthemeengine.ATE;
import com.emot.emotionPlayer.MusicPlayer;
import com.emot.emotionPlayer.R;
import com.emot.emotionPlayer.adapters.EmotionAdapter;
import com.emot.emotionPlayer.dataloaders.PlaylistLoader;
import com.emot.emotionPlayer.dataloaders.PlaylistSongLoader;
import com.emot.emotionPlayer.models.Playlist;
import com.emot.emotionPlayer.models.Song;
import com.emot.emotionPlayer.utils.Constants;
import com.emot.emotionPlayer.utils.PreferencesUtility;
import com.emot.emotionPlayer.utils.TimberUtils;
import com.emot.emotionPlayer.widgets.MultiViewPager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Emotion extends Fragment {
    private int playlistcount;
    private FragmentStatePagerAdapter adapter;
    private MultiViewPager pager;
    private RecyclerView recyclerView;
    private GridLayoutManager layoutManager;
    private RecyclerView.ItemDecoration itemDecoration;

    private Button btn_sad, btn_happy, btn_neutral;

    private PreferencesUtility mPreferences;
    private boolean isGrid;
    private boolean isList;

    private boolean showAuto;
    private EmotionAdapter mAdapter;

    private List<Playlist> playlists = new ArrayList<>();
    private List<Playlist> emotion_playlists = new ArrayList<>();

    private long happy_id, sad_id, neutral_id;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPreferences = PreferencesUtility.getInstance(getActivity());


    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(
                R.layout.fragment_emotion, container, false);

        Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        btn_happy = (Button) rootView.findViewById(R.id.btn_happy);
        btn_sad = (Button) rootView.findViewById(R.id.btn_sad);
        btn_neutral = (Button) rootView.findViewById(R.id.btn_neutral);


        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        final ActionBar ab = ((AppCompatActivity) getActivity()).getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setTitle("Emotion");

//to get the playlist
        playlists = PlaylistLoader.getPlaylists(getActivity(), showAuto);

       HashMap<String,Long> hm = new HashMap<>();
        for (Playlist p : playlists) {

            //saving 3 playlists
            if (p.name.equals("Happy")|| p.name.equals("Sad") || p.name.equals("Neutral"))

                hm.put(p.name,p.id);

        }




        playlistcount = hm.size();
        happy_id = hm.get("Happy");
        sad_id = hm.get("Sad");
        neutral_id = hm.get("Neutral");


        return rootView;
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean("dark_theme", false)) {
            ATE.apply(this, "dark_theme");
        } else {
            ATE.apply(this, "light_theme");

        }


        btn_happy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               Intent intent = new Intent(getContext(), emotionDetect.emotion.emotion_Main.class);
               startActivity(intent);
            }
        });

        btn_sad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Song> playlistsongs = PlaylistSongLoader.getSongsInPlaylist(getContext(), sad_id);
                int songCountInt = playlistsongs.size();

                if(songCountInt>0) {
                    long[] songIDs = new long[songCountInt];
                    for (int i = 0; i < songCountInt; i++) {
                        songIDs[i] = playlistsongs.get(i).id;
                    }

                    MusicPlayer.playAll(getContext(), songIDs, 0, -1, TimberUtils.IdType.NA, false);
                }

                else {
                    Toast.makeText(getActivity(), "Empty playlist - Please add songs",
                            Toast.LENGTH_LONG).show();
                }
            }
        });

        btn_neutral.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Song> playlistsongs = PlaylistSongLoader.getSongsInPlaylist(getContext(), neutral_id);
                int songCountInt = playlistsongs.size();

                if(songCountInt>0) {
                    long[] songIDs = new long[songCountInt];
                    for (int i = 0; i < songCountInt; i++) {
                        songIDs[i] = playlistsongs.get(i).id;
                    }

                    MusicPlayer.playAll(getContext(), songIDs, 0, -1, TimberUtils.IdType.NA, false);
                }

                else {
                    Toast.makeText(getActivity(), "Empty playlist - Please add songs",
                            Toast.LENGTH_LONG).show();
                }
            }
        });


    }


    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }






    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constants.ACTION_DELETE_PLAYLIST) {
            if (resultCode == Activity.RESULT_OK) {
                //reloadPlaylists();
            }

        }
    }




}


