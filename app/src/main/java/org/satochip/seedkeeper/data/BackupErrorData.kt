package org.satochip.seedkeeper.data

import org.satochip.client.seedkeeper.SeedkeeperSecretType

data class BackupErrorData (
    var sid: Int = -1,
    var label: String = "",
    var type: SeedkeeperSecretType = SeedkeeperSecretType.DEFAULT_TYPE,
    var subtype: Byte = 0x00,
    var nfcResultCode: NfcResultCode = NfcResultCode.NONE,
){}