package com.sakethh.linkora

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sakethh.linkora.data.local.localization.language.translations.TranslationsRepo
import com.sakethh.linkora.ui.screens.settings.SettingsPreference
import com.sakethh.linkora.utils.ifNullOrBlank
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LocalizedStringsVM @Inject constructor(private val translationsRepo: TranslationsRepo) :
    ViewModel() {

    private val _general = MutableStateFlow("")
    val general = _general.asStateFlow()

    private val _useLanguageStringsFetchedFromTheServer = MutableStateFlow("")
    val useLanguageStringsFetchedFromTheServer =
        _useLanguageStringsFetchedFromTheServer.asStateFlow()

    private val _useLanguageStringsFetchedFromTheServerDesc = MutableStateFlow("")
    val useLanguageStringsFetchedFromTheServerDesc =
        _useLanguageStringsFetchedFromTheServerDesc.asStateFlow()

    private val _userAgentDesc = MutableStateFlow("")
    val userAgentDesc =
        _userAgentDesc.asStateFlow()

    private val _userAgent = MutableStateFlow("")
    val userAgent =
        _userAgent.asStateFlow()

    private val _refreshingLinks = MutableStateFlow("")
    val refreshingLinks =
        _refreshingLinks.asStateFlow()

    private val _workManagerDesc = MutableStateFlow("")
    val workManagerDesc =
        _workManagerDesc.asStateFlow()

    private val _of = MutableStateFlow("")
    val of =
        _of.asStateFlow()

    private val _linksRefreshed = MutableStateFlow("")
    val linksRefreshed =
        _linksRefreshed.asStateFlow()

    private val _refreshingLinksInfo = MutableStateFlow("")
    val refreshingLinksInfo =
        _refreshingLinksInfo.asStateFlow()

    private val _refreshAllLinksTitlesAndImages = MutableStateFlow("")
    val refreshAllLinksTitlesAndImages =
        _refreshAllLinksTitlesAndImages.asStateFlow()

    private val _refreshAllLinksTitlesAndImagesDesc = MutableStateFlow("")
    val refreshAllLinksTitlesAndImagesDesc =
        _refreshAllLinksTitlesAndImagesDesc.asStateFlow()

    // TODO()
    private val _titleCopiedToClipboard = MutableStateFlow("")
    val titleCopiedToClipboard =
        _titleCopiedToClipboard.asStateFlow()

    private val _viewNote = MutableStateFlow("")
    val viewNote = _viewNote.asStateFlow()

    private val _rename = MutableStateFlow("")
    val rename =
        _rename.asStateFlow()

    private val _refreshingTitleAndImage = MutableStateFlow("")
    val refreshingTitleAndImage =
        _refreshingTitleAndImage.asStateFlow()

    private val _refreshImageAndTitle = MutableStateFlow("")
    val refreshImageAndTitle =
        _refreshImageAndTitle.asStateFlow()

    private val _unarchive = MutableStateFlow("")
    val unarchive =
        _unarchive.asStateFlow()

    private val _deleteTheNote = MutableStateFlow("")
    val deleteTheNote =
        _deleteTheNote.asStateFlow()


    private val _deleteFolder = MutableStateFlow("")
    val deleteFolder =
        _deleteFolder.asStateFlow()

    private val _deleteLink = MutableStateFlow("")
    val deleteLink =
        _deleteLink.asStateFlow()

    private val _savedNote = MutableStateFlow("")
    val savedNote =
        _savedNote.asStateFlow()

    private val _noteCopiedToClipboard = MutableStateFlow("")
    val noteCopiedToClipboard =
        _noteCopiedToClipboard.asStateFlow()

    private val _youDidNotAddNoteForThis = MutableStateFlow("")
    val youDidNotAddNoteForThis =
        _youDidNotAddNoteForThis.asStateFlow()


    fun loadStrings(context: Context) {
        viewModelScope.launch {
            awaitAll(
                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _general.emit(
                            translationsRepo.getLocalizedStringValueFor(
                                "general",
                                SettingsPreference.preferredAppLanguageCode.value
                            ).let {
                                if (it.isNullOrBlank()) {
                                    context.getString(R.string.general)
                                } else {
                                    it
                                }
                            }
                        )
                    } else {
                        _general.emit(context.getString(R.string.general))
                    }
                },
                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _useLanguageStringsFetchedFromTheServer.emit(
                            translationsRepo.getLocalizedStringValueFor(
                                "use_language_strings_fetched_from_the_server",
                                SettingsPreference.preferredAppLanguageCode.value
                            ).let {
                                it.ifNullOrBlank {
                                    context.getString(R.string.use_language_strings_fetched_from_the_server)
                                }
                            }
                        )
                    } else {
                        _useLanguageStringsFetchedFromTheServer.emit(context.getString(R.string.use_language_strings_fetched_from_the_server))
                    }
                },
                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _useLanguageStringsFetchedFromTheServerDesc.emit(
                            translationsRepo.getLocalizedStringValueFor(
                                "use_language_strings_fetched_from_the_server_desc",
                                SettingsPreference.preferredAppLanguageCode.value
                            ).let {
                                it.ifNullOrBlank {
                                    context.getString(R.string.use_language_strings_fetched_from_the_server_desc)
                                }
                            }
                        )
                    } else {
                        _useLanguageStringsFetchedFromTheServerDesc.emit(context.getString(R.string.use_language_strings_fetched_from_the_server_desc))
                    }
                },
                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _userAgentDesc.emit(
                            translationsRepo.getLocalizedStringValueFor(
                                "user_agent_desc",
                                SettingsPreference.preferredAppLanguageCode.value
                            ).let {
                                it.ifNullOrBlank {
                                    context.getString(R.string.user_agent_desc)
                                }
                            }
                        )
                    } else {
                        _userAgentDesc.emit(context.getString(R.string.user_agent_desc))
                    }
                },
                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _userAgent.emit(
                            translationsRepo.getLocalizedStringValueFor(
                                "user_agent",
                                SettingsPreference.preferredAppLanguageCode.value
                            ).let {
                                it.ifNullOrBlank {
                                    context.getString(R.string.user_agent)
                                }
                            }
                        )
                    } else {
                        _userAgent.emit(context.getString(R.string.user_agent))
                    }
                },
                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _refreshingLinks.emit(
                            translationsRepo.getLocalizedStringValueFor(
                                "refreshing_links",
                                SettingsPreference.preferredAppLanguageCode.value
                            ).let {
                                it.ifNullOrBlank {
                                    context.getString(R.string.refreshing_links)
                                }
                            }
                        )
                    } else {
                        _refreshingLinks.emit(context.getString(R.string.refreshing_links))
                    }
                },
                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _workManagerDesc.emit(
                            translationsRepo.getLocalizedStringValueFor(
                                "work_manager_desc",
                                SettingsPreference.preferredAppLanguageCode.value
                            ).let {
                                it.ifNullOrBlank {
                                    context.getString(R.string.work_manager_desc)
                                }
                            }
                        )
                    } else {
                        _workManagerDesc.emit(context.getString(R.string.work_manager_desc))
                    }
                },
                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _linksRefreshed.emit(
                            translationsRepo.getLocalizedStringValueFor(
                                "links_refreshed",
                                SettingsPreference.preferredAppLanguageCode.value
                            ).let {
                                it.ifNullOrBlank {
                                    context.getString(R.string.links_refreshed)
                                }
                            }
                        )
                    } else {
                        _linksRefreshed.emit(context.getString(R.string.links_refreshed))
                    }
                },
                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _refreshingLinksInfo.emit(
                            translationsRepo.getLocalizedStringValueFor(
                                "refreshing_links_info",
                                SettingsPreference.preferredAppLanguageCode.value
                            ).let {
                                it.ifNullOrBlank {
                                    context.getString(R.string.refreshing_links_info)
                                }
                            }
                        )
                    } else {
                        _refreshingLinksInfo.emit(context.getString(R.string.refreshing_links_info))
                    }
                },
                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _refreshAllLinksTitlesAndImages.emit(
                            translationsRepo.getLocalizedStringValueFor(
                                "refresh_all_links_titles_and_images",
                                SettingsPreference.preferredAppLanguageCode.value
                            ).let {
                                it.ifNullOrBlank {
                                    context.getString(R.string.refresh_all_links_titles_and_images)
                                }
                            }
                        )
                    } else {
                        _refreshAllLinksTitlesAndImages.emit(context.getString(R.string.refresh_all_links_titles_and_images))
                    }
                },
                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _refreshAllLinksTitlesAndImagesDesc.emit(
                            translationsRepo.getLocalizedStringValueFor(
                                "refresh_all_links_titles_and_images_desc",
                                SettingsPreference.preferredAppLanguageCode.value
                            ).let {
                                it.ifNullOrBlank {
                                    context.getString(R.string.refresh_all_links_titles_and_images_desc)
                                }
                            }
                        )
                    } else {
                        _refreshAllLinksTitlesAndImagesDesc.emit(context.getString(R.string.refresh_all_links_titles_and_images_desc))
                    }
                },
                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _of.emit(
                            translationsRepo.getLocalizedStringValueFor(
                                "of",
                                SettingsPreference.preferredAppLanguageCode.value
                            ).let {
                                it.ifNullOrBlank {
                                    context.getString(R.string.of)
                                }
                            }
                        )
                    } else {
                        _of.emit(context.getString(R.string.of))
                    }
                },
                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _titleCopiedToClipboard.emit(
                            translationsRepo.getLocalizedStringValueFor(
                                "title_copied_to_clipboard",
                                SettingsPreference.preferredAppLanguageCode.value
                            ).let {
                                it.ifNullOrBlank {
                                    context.getString(R.string.title_copied_to_clipboard)
                                }
                            }
                        )
                    } else {
                        _titleCopiedToClipboard.emit(context.getString(R.string.title_copied_to_clipboard))
                    }
                },
                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _viewNote.emit(
                            translationsRepo.getLocalizedStringValueFor(
                                "view_note",
                                SettingsPreference.preferredAppLanguageCode.value
                            ).let {
                                it.ifNullOrBlank {
                                    context.getString(R.string.view_note)
                                }
                            }
                        )
                    } else {
                        _viewNote.emit(context.getString(R.string.view_note))
                    }
                },
                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _rename.emit(
                            translationsRepo.getLocalizedStringValueFor(
                                "rename",
                                SettingsPreference.preferredAppLanguageCode.value
                            ).let {
                                it.ifNullOrBlank {
                                    context.getString(R.string.rename)
                                }
                            }
                        )
                    } else {
                        _rename.emit(context.getString(R.string.rename))
                    }
                },
                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _refreshingTitleAndImage.emit(
                            translationsRepo.getLocalizedStringValueFor(
                                "refreshing_title_and_image",
                                SettingsPreference.preferredAppLanguageCode.value
                            ).let {
                                it.ifNullOrBlank {
                                    context.getString(R.string.refreshing_title_and_image)
                                }
                            }
                        )
                    } else {
                        _refreshingTitleAndImage.emit(context.getString(R.string.refreshing_title_and_image))
                    }
                },
                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _refreshImageAndTitle.emit(
                            translationsRepo.getLocalizedStringValueFor(
                                "refresh_image_and_title",
                                SettingsPreference.preferredAppLanguageCode.value
                            ).let {
                                it.ifNullOrBlank {
                                    context.getString(R.string.refresh_image_and_title)
                                }
                            }
                        )
                    } else {
                        _refreshImageAndTitle.emit(context.getString(R.string.refresh_image_and_title))
                    }
                },
                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _unarchive.emit(
                            translationsRepo.getLocalizedStringValueFor(
                                "unarchive",
                                SettingsPreference.preferredAppLanguageCode.value
                            ).let {
                                it.ifNullOrBlank {
                                    context.getString(R.string.unarchive)
                                }
                            }
                        )
                    } else {
                        _unarchive.emit(context.getString(R.string.unarchive))
                    }
                },
                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _deleteTheNote.emit(
                            translationsRepo.getLocalizedStringValueFor(
                                "delete_the_note",
                                SettingsPreference.preferredAppLanguageCode.value
                            ).let {
                                it.ifNullOrBlank {
                                    context.getString(R.string.delete_the_note)
                                }
                            }
                        )
                    } else {
                        _deleteTheNote.emit(context.getString(R.string.delete_the_note))
                    }
                },
                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _deleteFolder.emit(
                            translationsRepo.getLocalizedStringValueFor(
                                "delete_folder",
                                SettingsPreference.preferredAppLanguageCode.value
                            ).let {
                                it.ifNullOrBlank {
                                    context.getString(R.string.delete_folder)
                                }
                            }
                        )
                    } else {
                        _deleteFolder.emit(context.getString(R.string.delete_folder))
                    }
                },
                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _deleteLink.emit(
                            translationsRepo.getLocalizedStringValueFor(
                                "delete_link",
                                SettingsPreference.preferredAppLanguageCode.value
                            ).let {
                                it.ifNullOrBlank {
                                    context.getString(R.string.delete_link)
                                }
                            }
                        )
                    } else {
                        _deleteLink.emit(context.getString(R.string.delete_link))
                    }
                },
                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _savedNote.emit(
                            translationsRepo.getLocalizedStringValueFor(
                                "saved_note",
                                SettingsPreference.preferredAppLanguageCode.value
                            ).let {
                                it.ifNullOrBlank {
                                    context.getString(R.string.saved_note)
                                }
                            }
                        )
                    } else {
                        _savedNote.emit(context.getString(R.string.saved_note))
                    }
                },
                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _noteCopiedToClipboard.emit(
                            translationsRepo.getLocalizedStringValueFor(
                                "note_copied_to_clipboard",
                                SettingsPreference.preferredAppLanguageCode.value
                            ).let {
                                it.ifNullOrBlank {
                                    context.getString(R.string.note_copied_to_clipboard)
                                }
                            }
                        )
                    } else {
                        _noteCopiedToClipboard.emit(context.getString(R.string.note_copied_to_clipboard))
                    }
                },
                async {
                    if (SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                        _youDidNotAddNoteForThis.emit(
                            translationsRepo.getLocalizedStringValueFor(
                                "you_did_not_add_note_for_this",
                                SettingsPreference.preferredAppLanguageCode.value
                            ).let {
                                it.ifNullOrBlank {
                                    context.getString(R.string.you_did_not_add_note_for_this)
                                }
                            }
                        )
                    } else {
                        _youDidNotAddNoteForThis.emit(context.getString(R.string.you_did_not_add_note_for_this))
                    }
                },
            )
        }
    }
}