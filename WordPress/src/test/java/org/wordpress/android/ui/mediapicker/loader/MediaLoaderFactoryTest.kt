package org.wordpress.android.ui.mediapicker.loader

import com.nhaarman.mockitokotlin2.whenever
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatExceptionOfType
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.wordpress.android.R.string
import org.wordpress.android.fluxc.model.SiteModel
import org.wordpress.android.ui.mediapicker.MediaPickerSetup
import org.wordpress.android.ui.mediapicker.MediaPickerSetup.DataSource.DEVICE
import org.wordpress.android.ui.mediapicker.MediaPickerSetup.DataSource.GIF_LIBRARY
import org.wordpress.android.ui.mediapicker.MediaPickerSetup.DataSource.STOCK_LIBRARY
import org.wordpress.android.ui.mediapicker.MediaPickerSetup.DataSource.WP_LIBRARY
import org.wordpress.android.ui.mediapicker.loader.DeviceListBuilder.DeviceListBuilderFactory
import org.wordpress.android.ui.mediapicker.loader.MediaLibraryDataSource.MediaLibraryDataSourceFactory
import org.wordpress.android.util.LocaleManagerWrapper

@RunWith(MockitoJUnitRunner::class)
class MediaLoaderFactoryTest {
    @Mock lateinit var deviceListBuilderFactory: DeviceListBuilderFactory
    @Mock lateinit var deviceListBuilder: DeviceListBuilder
    @Mock lateinit var mediaLibraryDataSourceFactory: MediaLibraryDataSourceFactory
    @Mock lateinit var mediaLibraryDataSource: MediaLibraryDataSource
    @Mock lateinit var stockMediaDataSource: StockMediaDataSource
    @Mock lateinit var localeManagerWrapper: LocaleManagerWrapper
    @Mock lateinit var site: SiteModel
    private lateinit var mediaLoaderFactory: MediaLoaderFactory

    @Before
    fun setUp() {
        mediaLoaderFactory = MediaLoaderFactory(
                deviceListBuilderFactory,
                mediaLibraryDataSourceFactory,
                stockMediaDataSource,
                localeManagerWrapper
        )
    }

    @Test
    fun `returns device list builder on DEVICE source`() {
        val mediaPickerSetup = MediaPickerSetup(
                DEVICE,
                canMultiselect = true,
                requiresStoragePermissions = true,
                allowedTypes = setOf(),
                cameraEnabled = false,
                systemPickerEnabled = true,
                editingEnabled = true,
                queueResults = false,
                defaultSearchView = false,
                title = string.wp_media_title
        )
        whenever(deviceListBuilderFactory.build(setOf())).thenReturn(deviceListBuilder)
        val mediaLoader = mediaLoaderFactory.build(mediaPickerSetup, site)

        assertThat(mediaLoader).isEqualTo(
                MediaLoader(
                        deviceListBuilder,
                        localeManagerWrapper
                )
        )
    }

    @Test
    fun `returns WP media source on WP_LIBRARY source`() {
        val mediaPickerSetup = MediaPickerSetup(
                WP_LIBRARY,
                canMultiselect = true,
                requiresStoragePermissions = false,
                allowedTypes = setOf(),
                cameraEnabled = false,
                systemPickerEnabled = false,
                editingEnabled = false,
                queueResults = false,
                defaultSearchView = false,
                title = string.wp_media_title
        )
        whenever(mediaLibraryDataSourceFactory.build(site, setOf())).thenReturn(mediaLibraryDataSource)

        val mediaLoader = mediaLoaderFactory.build(mediaPickerSetup, site)

        assertThat(mediaLoader).isEqualTo(
                MediaLoader(
                        mediaLibraryDataSource,
                        localeManagerWrapper
                )
        )
    }

    @Test
    fun `returns stock media source on STOCK_LIBRARY source`() {
        val mediaPickerSetup = MediaPickerSetup(
                STOCK_LIBRARY,
                canMultiselect = true,
                requiresStoragePermissions = false,
                allowedTypes = setOf(),
                cameraEnabled = false,
                systemPickerEnabled = false,
                editingEnabled = false,
                queueResults = false,
                defaultSearchView = false,
                title = string.wp_media_title
        )

        val mediaLoader = mediaLoaderFactory.build(mediaPickerSetup, site)

        assertThat(mediaLoader).isEqualTo(
                MediaLoader(
                        stockMediaDataSource,
                        localeManagerWrapper
                )
        )
    }

    @Test
    fun `throws exception on not implemented sources`() {
        assertThatExceptionOfType(NotImplementedError::class.java).isThrownBy {
            mediaLoaderFactory.build(
                    MediaPickerSetup(
                            GIF_LIBRARY,
                            canMultiselect = true,
                            requiresStoragePermissions = true,
                            allowedTypes = setOf(),
                            cameraEnabled = false,
                            systemPickerEnabled = true,
                            editingEnabled = true,
                            queueResults = false,
                            defaultSearchView = false,
                            title = string.wp_media_title
                    ),
                    site
            )
        }
    }
}
