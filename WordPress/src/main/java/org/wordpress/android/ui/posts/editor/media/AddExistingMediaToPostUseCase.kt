package org.wordpress.android.ui.posts.editor.media

import android.util.Log
import dagger.Reusable
import org.wordpress.android.fluxc.model.SiteModel
import org.wordpress.android.ui.posts.editor.EditorTracker
import org.wordpress.android.ui.posts.editor.media.EditorMedia.AddExistingMediaSource
import javax.inject.Inject

/**
 * Loads existing media items (they must have a valid url) from the local db and adds them to the editor.
 */
@Reusable
class AddExistingMediaToPostUseCase @Inject constructor(
    private val editorTracker: EditorTracker,
    private val getMediaModelUseCase: GetMediaModelUseCase,
    private val appendMediaToEditorUseCase: AppendMediaToEditorUseCase
) {
    suspend fun addMediaExistingInRemoteToEditorAsync(
        site: SiteModel,
        source: AddExistingMediaSource,
        mediaIdList: List<Long>,
        editorMediaListener: EditorMediaListener
    ) {
        getMediaModelUseCase
                .loadMediaByRemoteId(site, mediaIdList)
                .onEach { media ->
                    Log.d("vojta", "On each loaded media: $media")
                    editorTracker.trackAddMediaEvent(site, source, media.isVideo)
                }
                .let {
                    Log.d("vojta", "Adding media to editor $it")
                    appendMediaToEditorUseCase.addMediaToEditor(editorMediaListener, it)
                    editorMediaListener.syncPostObjectWithUiAndSaveIt()
                }
    }
}