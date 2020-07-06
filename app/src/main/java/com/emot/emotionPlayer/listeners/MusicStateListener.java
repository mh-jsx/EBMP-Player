

package com.emot.emotionPlayer.listeners;

/**
 * Listens for playback changes to send the the fragments bound to this activity
 */
public interface MusicStateListener {

    /**
     * Called when {@link com.emot.emotionPlayer.MusicService#REFRESH} is invoked
     */
    void restartLoader();

    /**
     * Called when {@link com.emot.emotionPlayer.MusicService#PLAYLIST_CHANGED} is invoked
     */
    void onPlaylistChanged();

    /**
     * Called when {@link com.emot.emotionPlayer.MusicService#META_CHANGED} is invoked
     */
    void onMetaChanged();

}
