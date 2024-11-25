# Seedkeeper-Android

The companion app for your Seedkeeper card.
It will help you to safely store and manage your crypto-related secrets including seedphrases, passwords and credentials.



_________________

## A few words about the NFC communication

When the application asks you to to place your Seedkeeper card against your phone, make sure that the card remains on the phone's NFC reader throughout the process.
The NFC toast will inform you with a blue tick that the communication was successful and that you can withdraw the card. 

If you Seedkeeper card contains a lot of secrets (>25), the communication between the card and the app can take more time (up to 4 seconds): keep your card on the reader the whole time.

More information on how to use the NFC with your Android phone: 
https://www.youtube.com/watch?v=lVrKgzak2Bc
_________________


## Setup and manage your Seedkeeper

> 🛒 [Buy your Seedkeeper here 🔗](https://satochip.io/product/seedkeeper/).

### Setup your card

The companion app allows you to setup your **Seedkeeper**:
- set a PIN code (from 4 to 16 chars),
- set a label.



### Generate

Generate new and strong secret and import it into your Seedkeeper. 

Here you can either **generate**:
- a **password**, composed by:
    - a label,
    - a login (optional),
    - an URL (optional).
- a **mnemonic seedphrase**, composed by:
   - a label,
   - optionally a **passphrase**.



### Import

Import an existing secret into your Seedkeeper. 

Here you can either **import**:
- a **password**, composed by:
   - a label,
   - a login (optional),
   - an URL (optional).
- a **mnemonic seedphrase**, composed by:
  - a label,
  - an existing **12/18/24 words BIP39 seedphrase**,
  - a **passphrase** (optional),
  - a **wallet descriptor** (optional).
- a **wallet descriptor**, composed by:
  - a label,
  - your descriptor.
- some **free text**, composed by:
  - a label,
  - your plain text.



### Manage

The Seedkeeper mobile app also allows you to manage your crypto-related secrets. 
With a clear overview of all your secrets, sorted by labels, you will easily recover your passwords and seedphrases.

Using Seedkeeper V2 card also allows you to delete a secret.

_________________


**⚠️ The seedphrase is very important and necessary to recover your wallet. Don't share it, don't lose it. ⚠️**

_👉 Keep it in a safe place using a [Seedkeeper🔗](https://satochip.io/product/seedkeeper/)._

What's a seedphrase: [check this 🔗](https://satochip.io/whats-a-seedphrase/).

What's a passphrase: [check this 🔗](https://satochip.io/passphrase/).

_________________


## Settings, backup and card authenticity

### Card's settings

- Check the card status:
   - Number of secrets loaded into the card;
   - Memory available;
   - Total memory.

- Set a friendly label for your Seedkeeper

- Change the PIN code

- Check the card authenticity

- Get the card authentikey

- Manage the card's logs

### Make a backup
The backup wizard will help you to backup your Seedkeeper card's content to another one using an encrypted communication between both cards.
Be sure to have a second Seedkeeper card to make the backup. 
> 🛒 [Buy your Seedkeeper here 🔗](https://satochip.io/product/seedkeeper/).

Note: if you have a lot of secrets, please allow some time to the NFC communication to read/write everything from one card to another.


### Parameters
Here you can start the introductions screen again, activate the debug mode and even factory reset your Seedkeeper.

#### Factory reset
The factory reset feature allows you to reset your Seedkeeper card to its factory state. This means that all secrets, saved information and settings (such as PIN code or label) will be deleted from the device.
Before starting the reset process, make sure you have a backup of the contents; either your seedphrases or any other password stored in your card.
The reset process is simple: click on "Reset my card", read the text and click on "Start". Then follow wizard and scan your card several times.


_________________

## More information…

💮 Join us on [Telegram](https://t.me/Satochip)
🐦 Follow us on [Twitter](https://twitter.com/satochip)
👾 Check out our [website](https://satochip.io/)
🔨 Take a look at the sources [GitHub](https://github.com/Toporin)
