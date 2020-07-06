

package com.emot.emotionPlayer.lastfmapi.callbacks;

import com.emot.emotionPlayer.lastfmapi.models.LastfmArtist;

public interface ArtistInfoListener {

    void artistInfoSucess(LastfmArtist artist);

    void artistInfoFailed();

}
