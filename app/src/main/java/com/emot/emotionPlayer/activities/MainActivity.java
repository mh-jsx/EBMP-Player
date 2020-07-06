

package com.emot.emotionPlayer.activities;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.appthemeengine.customizers.ATEActivityThemeCustomizer;
import com.anjlab.android.iab.v3.BillingProcessor;
import com.emot.emotionPlayer.dataloaders.PlaylistLoader;
import com.emot.emotionPlayer.dataloaders.PlaylistSongLoader;
import com.emot.emotionPlayer.models.Playlist;
import com.emot.emotionPlayer.models.Song;
import com.google.android.gms.cast.framework.media.widget.ExpandedControllerActivity;
import com.emot.emotionPlayer.MusicPlayer;
import com.emot.emotionPlayer.R;
import com.emot.emotionPlayer.cast.ExpandedControlsActivity;
import com.emot.emotionPlayer.fragments.AlbumDetailFragment;
import com.emot.emotionPlayer.fragments.ArtistDetailFragment;
import com.emot.emotionPlayer.fragments.Emotion;
import com.emot.emotionPlayer.fragments.FoldersFragment;
import com.emot.emotionPlayer.fragments.MainFragment;
import com.emot.emotionPlayer.fragments.PlaylistFragment;
import com.emot.emotionPlayer.fragments.QueueFragment;
import com.emot.emotionPlayer.permissions.Nammu;
import com.emot.emotionPlayer.permissions.PermissionCallback;
import com.emot.emotionPlayer.slidinguppanel.SlidingUpPanelLayout;
import com.emot.emotionPlayer.subfragments.LyricsFragment;
import com.emot.emotionPlayer.utils.Constants;
import com.emot.emotionPlayer.utils.Helpers;
import com.emot.emotionPlayer.utils.NavigationUtils;
import com.emot.emotionPlayer.utils.TimberUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.testfairy.TestFairy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Info.Login;
import Info.Profile;
import emot.emotionPlayer.welcome.welcome;

public class MainActivity extends BaseActivity implements ATEActivityThemeCustomizer {

    Menu nav_Menu;
    private SlidingUpPanelLayout panelLayout;
    public FirebaseAuth firebaseAuth;
    private NavigationView navigationView;
    private TextView songtitle, songartist;
    private ImageView albumart;
    private String action;
    private Map<String, Runnable> navigationMap = new HashMap<String, Runnable>();
    private Handler navDrawerRunnable = new Handler();
    private Runnable runnable;
    private DrawerLayout mDrawerLayout;
    private boolean isDarkTheme;
    private int playlistcount;
    private long happy_id, sad_id, neutral_id;
    private boolean showAuto;
    private List<Playlist> playlists = new ArrayList<>();
    private List<Playlist> emotion_playlists = new ArrayList<>();


    private Runnable navigateLibrary = new Runnable() {
        public void run() {
            navigationView.getMenu().findItem(R.id.nav_library).setChecked(true);
            Fragment fragment = new MainFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, fragment).commitAllowingStateLoss();

        }
    };

    private Runnable navigateEmotion = new Runnable() {
        public void run() {
            navigationView.getMenu().findItem(R.id.nav_emotion).setChecked(true);
            Fragment fragment = new Emotion();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.hide(getSupportFragmentManager().findFragmentById(R.id.fragment_container));
            transaction.replace(R.id.fragment_container, fragment).commit();
        }
    };

    private Runnable navigatePlaylist = new Runnable() {
        public void run() {
            navigationView.getMenu().findItem(R.id.nav_playlists).setChecked(true);
            Fragment fragment = new PlaylistFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.hide(getSupportFragmentManager().findFragmentById(R.id.fragment_container));
            transaction.replace(R.id.fragment_container, fragment).commit();

        }
    };

    private Runnable navigateFolder = new Runnable() {
        public void run() {
            navigationView.getMenu().findItem(R.id.nav_folders).setChecked(true);
            Fragment fragment = new FoldersFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.hide(getSupportFragmentManager().findFragmentById(R.id.fragment_container));
            transaction.replace(R.id.fragment_container, fragment).commit();

        }
    };

    private Runnable navigateQueue = new Runnable() {
        public void run() {
            navigationView.getMenu().findItem(R.id.nav_queue).setChecked(true);
            Fragment fragment = new QueueFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.hide(getSupportFragmentManager().findFragmentById(R.id.fragment_container));
            transaction.replace(R.id.fragment_container, fragment).commit();

        }
    };

    private Runnable navigateAlbum = new Runnable() {
        public void run() {
            long albumID = getIntent().getExtras().getLong(Constants.ALBUM_ID);
            Fragment fragment = AlbumDetailFragment.newInstance(albumID, false, null);
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, fragment).commit();
        }
    };

    private Runnable navigateArtist = new Runnable() {
        public void run() {
            long artistID = getIntent().getExtras().getLong(Constants.ARTIST_ID);
            Fragment fragment = ArtistDetailFragment.newInstance(artistID, false, null);
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, fragment).commit();
        }
    };

    private Runnable navigateLyrics = new Runnable() {
        public void run() {
            Fragment fragment = new LyricsFragment();
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, fragment).commit();
        }
    };

    private Runnable navigateNowplaying = new Runnable() {
        public void run() {
            navigateLibrary.run();
            startActivity(new Intent(MainActivity.this, NowPlayingActivity.class));
        }
    };

    private final PermissionCallback permissionReadstorageCallback = new PermissionCallback() {
        @Override
        public void permissionGranted() {
            loadEverything();
        }

        @Override
        public void permissionRefused() {
            finish();
        }
    };


    @Override
    public void onCreate(Bundle savedInstanceState) {

        TestFairy.begin(this, "SDK-8RnvyV19");


        action = getIntent().getAction();

        isDarkTheme = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("dark_theme", false);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        navigationMap.put(Constants.NAVIGATE_LIBRARY, navigateLibrary);
        navigationMap.put(Constants.NAVIGATE_PLAYLIST, navigatePlaylist);
        navigationMap.put(Constants.NAVIGATE_EMOTION, navigateEmotion);
        navigationMap.put(Constants.NAVIGATE_QUEUE, navigateQueue);
        navigationMap.put(Constants.NAVIGATE_NOWPLAYING, navigateNowplaying);
        navigationMap.put(Constants.NAVIGATE_ALBUM, navigateAlbum);
        navigationMap.put(Constants.NAVIGATE_ARTIST, navigateArtist);
        navigationMap.put(Constants.NAVIGATE_LYRICS, navigateLyrics);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        panelLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        View header = navigationView.inflateHeaderView(R.layout.nav_header);

        albumart = (ImageView) header.findViewById(R.id.album_art);
        songtitle = (TextView) header.findViewById(R.id.song_title);
        songartist = (TextView) header.findViewById(R.id.song_artist);

        setPanelSlideListeners(panelLayout);

        navDrawerRunnable.postDelayed(new Runnable() {
            @Override
            public void run() {
                setupDrawerContent(navigationView);
                setupNavigationIcons(navigationView);
            }
        }, 700);


        if (TimberUtils.isMarshmallow()) {
            checkPermissionAndThenLoad();
            //checkWritePermissions();
        } else {
            loadEverything();
        }

        addBackstackListener();

        if(Intent.ACTION_VIEW.equals(action)) {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    MusicPlayer.clearQueue();
                    MusicPlayer.openFile(getIntent().getData().getPath());
                    MusicPlayer.playOrPause();
                    navigateNowplaying.run();
                }
            }, 350);
        }

        if (!panelLayout.isPanelHidden() && MusicPlayer.getTrackName() == null ) {
            panelLayout.hidePanel();
        }
        if (playServicesAvailable) {

            final FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.WRAP_CONTENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.BOTTOM;

            FrameLayout contentRoot = findViewById(R.id.content_root);
            contentRoot.addView(LayoutInflater.from(this)
                    .inflate(R.layout.fragment_cast_mini_controller, null), params);

            findViewById(R.id.castMiniController).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(MainActivity.this, ExpandedControllerActivity.class));
                }
            });
        }

        createAdditionalPlaylists();

        //////play detective playlist

        Bundle bundle = getIntent().getExtras();
        String message = bundle.getString("msg");
        //get message value from emotion_Main class
        if(message.equals("Sad")){
            play();
            List<Song> playlistsongs = PlaylistSongLoader.getSongsInPlaylist(this, sad_id);
            int songCountInt = playlistsongs.size();

            if(songCountInt>0) {
                long[] songIDs = new long[songCountInt];
                for (int i = 0; i < songCountInt; i++) {
                    songIDs[i] = playlistsongs.get(i).id;
                }

                MusicPlayer.playAll(getApplicationContext(), songIDs, 0, -1, TimberUtils.IdType.NA, false);
            }

            else {
                Toast.makeText(getApplicationContext(), "Empty playlist - Please add songs",
                        Toast.LENGTH_LONG).show();
            }
        }
        if(message.equals("Happy")){
            play();
            List<Song> playlistsongs = PlaylistSongLoader.getSongsInPlaylist(this, happy_id);
            int songCountInt = playlistsongs.size();

            if(songCountInt>0) {
                long[] songIDs = new long[songCountInt];
                for (int i = 0; i < songCountInt; i++) {
                    songIDs[i] = playlistsongs.get(i).id;
                }

                MusicPlayer.playAll(getApplicationContext(), songIDs, 0, -1, TimberUtils.IdType.NA, false);
            }

            else {
                Toast.makeText(getApplicationContext(), "Happy playlist is Empty - Please add songs",
                        Toast.LENGTH_LONG).show();
            }
        }
        if(message.equals("Neutral")){
            play();
            List<Song> playlistsongs = PlaylistSongLoader.getSongsInPlaylist(this, neutral_id);
            int songCountInt = playlistsongs.size();

            if(songCountInt>0) {
                long[] songIDs = new long[songCountInt];
                for (int i = 0; i < songCountInt; i++) {
                    songIDs[i] = playlistsongs.get(i).id;
                }

                MusicPlayer.playAll(getApplicationContext(), songIDs, 0, -1, TimberUtils.IdType.NA, false);
            }

            else {
                Toast.makeText(getApplicationContext(), "Neutral playlist is Empty - Please add songs",
                        Toast.LENGTH_LONG).show();
            }
        }

        /////logout


        firebaseAuth = firebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser() == null){
            navigationView = (NavigationView) findViewById(R.id.nav_view);
            nav_Menu = navigationView.getMenu();
            nav_Menu.findItem(R.id.nav_donate).setTitle("Login");
        }
        else{
            nav_Menu = navigationView.getMenu();
            nav_Menu.findItem(R.id.nav_donate).setTitle("Logout");
            //nav_Menu.findItem(R.id.nav_donate).setEnabled(true);
        }


        }

    private void play() {
        //to get the playlist
        playlists = PlaylistLoader.getPlaylists(this, showAuto);

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
    }


    public  void createAdditionalPlaylists() {
         MusicPlayer.createPlaylist(this, "Happy");


         MusicPlayer.createPlaylist(this, "Sad");


         MusicPlayer.createPlaylist(this, "Neutral");


        // Log.d("DEEP2",playistId+" "+playistI+" "+playist+" ");
    }



    private void loadEverything() {

        Runnable navigation = navigationMap.get(action);
        if (navigation != null) {
            navigation.run();
        } else {
            navigateLibrary.run();
        }

        new initQuickControls().execute("");
    }

    private void checkPermissionAndThenLoad() {
        //check for permission
        if (Nammu.checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE) && Nammu.checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            loadEverything();
        } else {
            if (Nammu.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Snackbar.make(panelLayout, "Timber will need to read external storage to display songs on your device.",
                        Snackbar.LENGTH_INDEFINITE)
                        .setAction("OK", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Nammu.askForPermission(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, permissionReadstorageCallback);
                            }
                        }).show();
            } else {
                Nammu.askForPermission(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, permissionReadstorageCallback);
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                if (isNavigatingMain()) {
                    mDrawerLayout.openDrawer(GravityCompat.START);
                } else super.onBackPressed();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (panelLayout.isPanelExpanded()) {
            panelLayout.collapsePanel();
        } else if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(final MenuItem menuItem) {
                        updatePosition(menuItem);
                        return true;

                    }
                });
    }

    private void setupNavigationIcons(NavigationView navigationView) {

        //material-icon-lib currently doesn't work with navigationview of design support library 22.2.0+
        //set icons manually for now
        //https://github.com/code-mc/material-icon-lib/issues/15

        if (!isDarkTheme) {
            navigationView.getMenu().findItem(R.id.nav_library).setIcon(R.drawable.library_music);
            navigationView.getMenu().findItem(R.id.nav_playlists).setIcon(R.drawable.playlist_play);
            navigationView.getMenu().findItem(R.id.nav_emotion).setIcon(R.drawable.smile_black);

            navigationView.getMenu().findItem(R.id.nav_queue).setIcon(R.drawable.music_note);
            navigationView.getMenu().findItem(R.id.nav_folders).setIcon(R.drawable.ic_folder_open_black_24dp);
            navigationView.getMenu().findItem(R.id.nav_nowplaying).setIcon(R.drawable.bookmark_music);
            navigationView.getMenu().findItem(R.id.nav_settings).setIcon(R.drawable.settings);
            navigationView.getMenu().findItem(R.id.nav_about).setIcon(R.drawable.information);
            navigationView.getMenu().findItem(R.id.nav_donate).setIcon(R.drawable.payment_black);
        } else {
            navigationView.getMenu().findItem(R.id.nav_library).setIcon(R.drawable.library_music_white);
            navigationView.getMenu().findItem(R.id.nav_emotion).setIcon(R.drawable.smile_white);
            navigationView.getMenu().findItem(R.id.nav_playlists).setIcon(R.drawable.playlist_play_white);
            navigationView.getMenu().findItem(R.id.nav_queue).setIcon(R.drawable.music_note_white);
            navigationView.getMenu().findItem(R.id.nav_folders).setIcon(R.drawable.ic_folder_open_white_24dp);
            navigationView.getMenu().findItem(R.id.nav_nowplaying).setIcon(R.drawable.bookmark_music_white);
            navigationView.getMenu().findItem(R.id.nav_settings).setIcon(R.drawable.settings_white);
            navigationView.getMenu().findItem(R.id.nav_about).setIcon(R.drawable.information_white);
            navigationView.getMenu().findItem(R.id.nav_donate).setIcon(R.drawable.payment_white);
        }

        try {
            if (!BillingProcessor.isIabServiceAvailable(this)) {
                navigationView.getMenu().removeItem(R.id.nav_donate);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void updatePosition(final MenuItem menuItem) {
        runnable = null;

        switch (menuItem.getItemId()) {
            case R.id.nav_library:
                runnable = navigateLibrary;

                break;
            case R.id.nav_playlists:
                runnable = navigatePlaylist;

                break;
            case R.id.nav_emotion:
                runnable = navigateEmotion;

                break;

            case R.id.nav_folders:
                runnable = navigateFolder;

                break;
            case R.id.nav_nowplaying:
                if (getCastSession() != null) {
                    startActivity(new Intent(MainActivity.this, ExpandedControlsActivity.class));
                } else {
                    NavigationUtils.navigateToNowplaying(MainActivity.this, false);
                }
                break;
            case R.id.nav_queue:
                runnable = navigateQueue;

                break;
            case R.id.nav_settings:
                NavigationUtils.navigateToSettings(MainActivity.this);
                break;
            case R.id.nav_about:
                if(firebaseAuth.getCurrentUser() == null){
                    Toast.makeText(this, "Please Login first to view the Profile", Toast.LENGTH_LONG).show();
                }
                else{
                    startActivity(new Intent(this, Profile.class));
                }

                break;
            case R.id.nav_donate:
                if(nav_Menu.findItem(R.id.nav_donate).getTitle().equals("Login"))
                    startActivity(new Intent(MainActivity.this, Login.class));
                if(nav_Menu.findItem(R.id.nav_donate).getTitle().equals("Logout")) {
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(MainActivity.this, welcome.class));
                }

                break;
        }

        if (runnable != null) {
            menuItem.setChecked(true);
            mDrawerLayout.closeDrawers();
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    runnable.run();
                }
            }, 350);
        }
    }

    public void setDetailsToHeader() {
        String name = MusicPlayer.getTrackName();
        String artist = MusicPlayer.getArtistName();

        if (name != null && artist != null) {
            songtitle.setText(name);
            songartist.setText(artist);
        }
        ImageLoader.getInstance().displayImage(TimberUtils.getAlbumArtUri(MusicPlayer.getCurrentAlbumId()).toString(), albumart,
                new DisplayImageOptions.Builder().cacheInMemory(true)
                        .showImageOnFail(R.drawable.ic_empty_music2)
                        .resetViewBeforeLoading(true)
                        .build());
    }

    @Override
    public void onMetaChanged() {
        super.onMetaChanged();
        setDetailsToHeader();

        if (panelLayout.isPanelHidden() && MusicPlayer.getTrackName() != null) {
            panelLayout.showPanel();
        }
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, String[] permissions, int[] grantResults) {
        Nammu.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private boolean isNavigatingMain() {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        return (currentFragment instanceof MainFragment || currentFragment instanceof QueueFragment || currentFragment instanceof Emotion
                || currentFragment instanceof PlaylistFragment || currentFragment instanceof FoldersFragment);
    }

    private void addBackstackListener() {
        getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                getSupportFragmentManager().findFragmentById(R.id.fragment_container).onResume();
            }
        });
    }


    @Override
    public int getActivityTheme() {
        return isDarkTheme ? R.style.AppThemeNormalDark : R.style.AppThemeNormalLight;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        getSupportFragmentManager().findFragmentById(R.id.fragment_container).onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public void showCastMiniController() {
        findViewById(R.id.castMiniController).setVisibility(View.VISIBLE);
        findViewById(R.id.quickcontrols_container).setVisibility(View.GONE);
        panelLayout.hidePanel();
    }

    @Override
    public void hideCastMiniController() {

        findViewById(R.id.castMiniController).setVisibility(View.GONE);
        findViewById(R.id.quickcontrols_container).setVisibility(View.VISIBLE);

        panelLayout.showPanel();
    }
}


