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
            // disable scanning once finished
            Thread.sleep(100) // delay to let resultCodeLive update (avoid race condition?)
            SatoLog.d(TAG, "onConnected: resultAfterConnection delay: ${NFCCardService.resultCodeLive.value}")
            if (NFCCardService.resultCodeLive.value != NfcResultCode.UNKNOWN_ERROR) { //todo: refine condition?
                // if result is OK, or failed with a known reason, we stop polling for the card
                NFCCardService.disableScanForAction()
            }

        } catch (e: Exception) {
            SatoLog.e(TAG, "onConnected: an exception has been thrown during card init.")
            SatoLog.e(TAG, Log.getStackTraceString(e))
            onDisconnected()
        }
    }

    override fun onDisconnected() {
        NFCCardService.isConnected.postValue(false)
        NFCCardService.resultCodeLive.postValue(NfcResultCode.OK)

        SatoLog.d(TAG, "onDisconnected: Card disconnected!")
    }
}