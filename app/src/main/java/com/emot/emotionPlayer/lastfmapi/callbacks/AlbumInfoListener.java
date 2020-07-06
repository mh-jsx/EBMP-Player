
package com.emot.emotionPlayer.lastfmapi.callbacks;

import com.emot.emotionPlayer.lastfmapi.models.LastfmAlbum;

public interface AlbumInfoListener {

    void albumInfoSuccess(LastfmAlbum album);

    void albumInfoFailed();

}
