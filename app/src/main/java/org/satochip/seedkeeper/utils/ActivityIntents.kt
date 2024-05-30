package org.satochip.seedkeeper.utils

import android.content.Context
import android.content.Intent
import org.satochip.seedkeeper.WebviewActivity
import org.satochip.seedkeeper.data.IntentConstants

fun webviewActivityIntent(
    url: String,
    context: Context
) {
    context.startActivity(
        Intent(context, WebviewActivity::class.java)
            .putExtra(IntentConstants.URL_STRING.name, url)
    )
}