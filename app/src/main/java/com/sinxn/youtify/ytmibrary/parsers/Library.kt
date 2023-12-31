package com.sinxn.youtify.ytmibrary.parsers

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.sinxn.youtify.ytmibrary.YTAuth
import com.sinxn.youtify.ytmibrary.findObjectByKey
import com.sinxn.youtify.ytmibrary.nav

fun getLibraryContents(response: JsonObject, renderer: List<Any>): JsonElement? {
    val section = nav(response, YTAuth.SINGLE_COLUMN_TAB + YTAuth.SECTION_LIST, true)
    var contents: JsonElement? = null
    contents = if (section==null){
        nav(response, YTAuth.SINGLE_COLUMN + YTAuth.TAB_1_CONTENT + YTAuth.SECTION_LIST_ITEM + renderer,
            true)
    }else {
        val results = findObjectByKey(section.asJsonArray,"itemSectionRenderer")
        if (results==null) nav(response, YTAuth.SINGLE_COLUMN_TAB + YTAuth.SECTION_LIST_ITEM + renderer, true)
        else nav(results, YTAuth.ITEM_SECTION + renderer, true)
    }
    return contents
}