package org.satochip.seedkeeper.utils

import org.bouncycastle.crypto.digests.SHA256Digest
import org.bouncycastle.crypto.digests.SHA512Digest
import org.bouncycastle.crypto.generators.PKCS5S2ParametersGenerator
import org.bouncycastle.crypto.params.KeyParameter
import java.text.Normalizer

/**
 * BIP39 mnemonic code, English wordlist only.
 * See https://github.com/bitcoin/bips/blob/master/bip-0039.mediawiki
 *
 * Tested against the official BIP39 test vectors (Bip39Test).
 * Hashing and PBKDF2 use the BouncyCastle lightweight API (provided by satochip-lib).
 */
object Bip39 {

    private const val WORDLIST_SIZE = 2048
    private const val PBKDF2_ROUNDS = 2048
    private const val SEED_SIZE_BITS = 512

    // official BIP39 English wordlist (sha256: 2f5eed53a4727b4bf8880d8f3f199efc90e58503646d9ff8eff3a2ed3b24dbda)
    val wordList: List<String> by lazy {
        val stream = Bip39::class.java.getResourceAsStream("/bip39/english.txt")
            ?: throw IllegalStateException("BIP39 wordlist not found")
        val words = stream.bufferedReader(Charsets.UTF_8).use { reader ->
            reader.readLines().map { it.trim() }.filter { it.isNotEmpty() }
        }
        check(words.size == WORDLIST_SIZE) { "Invalid BIP39 wordlist size: ${words.size}" }
        words
    }

    private val wordIndex: Map<String, Int> by lazy {
        wordList.withIndex().associate { (index, word) -> word to index }
    }

    /**
     * Converts entropy (16 to 32 bytes, multiple of 4) to a list of 12 to 24 words.
     */
    fun toMnemonic(entropy: ByteArray): List<String> {
        require(entropy.size in 16..32 && entropy.size % 4 == 0) {
            "Invalid entropy size: ${entropy.size} bytes"
        }
        val checksumBits = entropy.size * 8 / 32
        val checksum = sha256(entropy)
        val totalBits = entropy.size * 8 + checksumBits

        return (0 until totalBits / 11).map { wordNumber ->
            var index = 0
            for (i in 0 until 11) {
                val bit = wordNumber * 11 + i
                index = (index shl 1) or getBit(entropy, checksum, bit)
            }
            wordList[index]
        }
    }

    /**
     * Converts a list of 12 to 24 words back to entropy, verifying the checksum.
     *
     * @throws IllegalArgumentException on invalid length, unknown word or wrong checksum.
     */
    fun toEntropy(words: List<String>): ByteArray {
        require(words.size in listOf(12, 15, 18, 21, 24)) { "Invalid number of words: ${words.size}" }

        val totalBits = words.size * 11
        val checksumBits = totalBits / 33
        val entropyBits = totalBits - checksumBits
        val bits = BooleanArray(totalBits)
        words.forEachIndexed { wordNumber, word ->
            val index = wordIndex[word] ?: throw IllegalArgumentException("Word not in BIP39 wordlist")
            for (i in 0 until 11) {
                bits[wordNumber * 11 + i] = (index shr (10 - i)) and 1 == 1
            }
        }

        val entropy = ByteArray(entropyBits / 8)
        for (i in 0 until entropyBits) {
            if (bits[i]) {
                entropy[i / 8] = (entropy[i / 8].toInt() or (0x80 ushr (i % 8))).toByte()
            }
        }

        val checksum = sha256(entropy)
        for (i in 0 until checksumBits) {
            val expected = (checksum[i / 8].toInt() shr (7 - i % 8)) and 1 == 1
            require(bits[entropyBits + i] == expected) { "Invalid BIP39 checksum" }
        }
        return entropy
    }

    /**
     * Checks that the words form a valid mnemonic (known words, valid length and checksum).
     *
     * @throws IllegalArgumentException if the mnemonic is invalid.
     */
    fun check(words: List<String>) {
        toEntropy(words)
    }

    /**
     * Derives the 64-byte seed from the mnemonic words and passphrase (PBKDF2-HMAC-SHA512, 2048 rounds).
     * Mnemonic and passphrase are NFKD-normalized, as required by BIP39.
     */
    fun toSeed(words: List<String>, passphrase: String): ByteArray {
        val password = nfkd(words.joinToString(" ")).toByteArray(Charsets.UTF_8)
        val salt = nfkd("mnemonic$passphrase").toByteArray(Charsets.UTF_8)
        val generator = PKCS5S2ParametersGenerator(SHA512Digest())
        generator.init(password, salt, PBKDF2_ROUNDS)
        return (generator.generateDerivedParameters(SEED_SIZE_BITS) as KeyParameter).key
    }

    private fun nfkd(text: String): String = Normalizer.normalize(text, Normalizer.Form.NFKD)

    private fun sha256(data: ByteArray): ByteArray {
        val digest = SHA256Digest()
        digest.update(data, 0, data.size)
        return ByteArray(digest.digestSize).also { digest.doFinal(it, 0) }
    }

    // bit at position 'bit' in (entropy || checksum), most significant bit first
    private fun getBit(entropy: ByteArray, checksum: ByteArray, bit: Int): Int {
        val bytes = if (bit < entropy.size * 8) entropy else checksum
        val position = if (bit < entropy.size * 8) bit else bit - entropy.size * 8
        return (bytes[position / 8].toInt() shr (7 - position % 8)) and 1
    }
}
