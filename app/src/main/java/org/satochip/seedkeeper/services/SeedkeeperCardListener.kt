package org.satochip.seedkeeper.services

import android.util.Log
import org.satochip.client.SatochipCommandSet
import org.satochip.io.CardChannel
import org.satochip.io.CardListener
import org.satochip.seedkeeper.data.NfcResultCode

private const val TAG = "SeedkeeperCardListener"

object SatochipCardListenerForAction : CardListener {

    override fun onConnected(cardChannel: CardChannel?) {

        NFCCardService.isConnected.postValue(true)
        SatoLog.d(TAG, "onConnected: Card is connected")
        try {
            val cmdSet = SatochipCommandSet(cardChannel)
            // start to interact with card
            NFCCardService.initialize(cmdSet)

            // TODO: disconnect?
            onDisconnected()
            Thread.sleep(100) // delay to let resultCodeLive update (avoid race condition?)
            SatoLog.d(TAG, "onConnected: resultAfterConnection delay: ${NFCCardService.resultCodeLive.value}")
            NFCCardService.disableScanForAction()
        } catch (e: Exception) {
            SatoLog.e(TAG, "onConnected: an exception has been thrown during card init.")
            SatoLog.e(TAG, Log.getStackTraceString(e))
            onDisconnected()
        }
    }

    override fun onDisconnected() {
        NFCCardService.isConnected.postValue(false)
        SatoLog.d(TAG, "onDisconnected: Card disconnected!")
    }
}