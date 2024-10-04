package com.ramo.getride.android.global.util

internal val android.content.Context.isDarkMode: Boolean
    get() {
        val nightModeFlags: Int =
            resources.configuration.uiMode and
                    android.content.res.Configuration.UI_MODE_NIGHT_MASK
        return when (nightModeFlags) {
            android.content.res.Configuration.UI_MODE_NIGHT_YES -> true
            android.content.res.Configuration.UI_MODE_NIGHT_NO -> false
            else -> true
        }
    }

internal val android.content.Context.isTablet: Boolean
    get() {
        return resources.configuration.screenLayout and android.content.res.Configuration.SCREENLAYOUT_SIZE_MASK >=
                android.content.res.Configuration.SCREENLAYOUT_SIZE_LARGE
    }

fun android.content.Context.bitmapDescriptorFromVector(id: Int): com.google.android.gms.maps.model.BitmapDescriptor {
    val vectorDrawable = androidx.core.content.ContextCompat.getDrawable(this, id)!!
    val h = (50 * resources.displayMetrics.density).toInt()
    val w = (50 * resources.displayMetrics.density).toInt()
    vectorDrawable.setBounds(0, 0, w, h)
    val bm = android.graphics.Bitmap.createBitmap(w, h, android.graphics.Bitmap.Config.ARGB_8888)
    val canvas = android.graphics.Canvas(bm)
    vectorDrawable.draw(canvas)
    return com.google.android.gms.maps.model.BitmapDescriptorFactory.fromBitmap(bm)
}

internal val android.content.Context.imageBuildr: (String) -> coil.request.ImageRequest
    get() = {
        coil.request.ImageRequest.Builder(this@imageBuildr)
            .data(it)
            .diskCacheKey(it)
            .networkCachePolicy(coil.request.CachePolicy.ENABLED)
            .diskCachePolicy(coil.request.CachePolicy.ENABLED)
            .memoryCachePolicy(coil.request.CachePolicy.ENABLED)
            .crossfade(true)
            .build()
    }

val String.videoType: String
    get() = reversed().split(".").getOrNull(0)?.reversed() ?: ""

/*
* val painter = rememberAsyncImagePainter(
    model = state.course.briefVideo,
    imageLoader = context.videoImageBuildr,
)*/
internal val android.content.Context.videoImageBuildr: coil.ImageLoader
    get() = coil.ImageLoader.Builder(this@videoImageBuildr)
        .components {
            add(coil.decode.VideoFrameDecoder.Factory())
        }
        .networkCachePolicy(coil.request.CachePolicy.ENABLED)
        .diskCachePolicy(coil.request.CachePolicy.ENABLED)
        .memoryCachePolicy(coil.request.CachePolicy.ENABLED)
        .crossfade(true)
        .build()

fun android.content.Context.getMimeType(uri: android.net.Uri): String = kotlin.runCatching {
    val extension = if (uri.scheme == android.content.ContentResolver.SCHEME_CONTENT) {
        val mime = android.webkit.MimeTypeMap.getSingleton()
        mime.getExtensionFromMimeType(contentResolver.getType(uri)) ?: ""
    } else {
        android.webkit.MimeTypeMap.getFileExtensionFromUrl(android.net.Uri.fromFile(java.io.File(uri.path ?: return@runCatching "")).toString())
    }
    return@runCatching ".$extension"
}.getOrDefault("")


/*
val videoItem: (String, String) -> io.sanghun.compose.video.uri.VideoPlayerMediaItem
    get() = { videoUri, videoTitle ->
        io.sanghun.compose.video.uri.VideoPlayerMediaItem.NetworkMediaItem(
            url = videoUri,
            mediaMetadata = androidx.media3.common.MediaMetadata.Builder().setSubtitle(videoTitle).setAlbumTitle(videoTitle)
                .setDisplayTitle(videoTitle).build(),
            mimeType = videoUri.videoType,
        )
    }

@get:androidx.compose.runtime.Composable
@get:androidx.compose.runtime.ReadOnlyComposable
val videoConfig: io.sanghun.compose.video.controller.VideoPlayerControllerConfig
    get() {
        return io.sanghun.compose.video.controller.VideoPlayerControllerConfig(
            showSpeedAndPitchOverlay = false,
            showSubtitleButton = false,
            showCurrentTimeAndTotalTime = true,
            showBufferingProgress = true,
            showForwardIncrementButton = true,
            showBackwardIncrementButton = true,
            showBackTrackButton = true,
            showNextTrackButton = true,
            showRepeatModeButton = true,
            controllerShowTimeMilliSeconds = 5_000,
            controllerAutoShow = true,
            showFullScreenButton = true,
        )
    }*/