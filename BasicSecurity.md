# Basic Security - Aimed at ALL Ages (but mainly 8+ year olds)

## 0 Intro

As technology progresses gradually all of ones property will be accessible digitally.  It is therefore extremly important that young people understand basic security.  In the future a sufficiently savy hacker will be able to steal everything you own if you do not follow these instructions. Learn young.

The Notes section at the end aims to provide _some_ explanation.

## 1 Passwords

### 1.1 Master Passwords

This section will cover passwords for your 

 - Operating System (logging into your computer) 
 - and your email (we assume gmail). 
 
If you use **Mac** or **Linux** it is advised that these passwords _are the same_ for your computer and gmail. If you use **Windows**, the passwords should be _different_.

#### 1.1.1 Invention

The younger the child, the simpler the password ought to be, but loosely use the following system. Then see section 1.1.3

1. Pick a sentence at least 7 words such that:
  1. 2 words are random 2 digit numbers, use dice
  2. The words form a silly sentence, try to be as ridiculous as possible and pick long words. E.g. `57 giant potatoes leaked 14 lanterns happily`
  3. Invent a simple system to separate the words, e.g. a full stop, or the last letter is upper case, etc, e.g. `57,gianT,potatoeS,leakeD,14,lanternS,happilY`. The more complicated the better, but make sure you can remember it!  **Better start with something simple**
2. Write it down REALLY clearly in big letters on a sheet of **paper with a pen**.

NOTE: This oftens results in a password that is tedious to type, if this is a problem for your use case see NOTES 1.1.1

#### 1.1.2 Storage

Store the piece of paper in a safe place, provided you get into the habbit of locking your screen (section 4) you will soon memorise the password.

Copy the password onto another piece of paper and ask your parents to keep it safe but NOT near something that is likely to get stolen (like in a jewlery box).  Better to keep it with something of little interest to a robber, like a photo album.

**Do NOT label the paper** or label it with something misleading, like "Amazon Reference Number".

It's extremly unlikely that a robber will steal your computer and stumble across your password, _and_ want to get inside your computer to do something malicious.  It's more likely they will try to wipe your disk so they can sell it.  Therefore this guide will not cover advanced storage techniques that would protect against such a situation.

#### 1.1.3 Rotation & Complexity (when to change)

Since younger children will have data of virtually no value then start with simpler passwords.  Nevertheless _some_ complexity is desirable to train the child from an early age how to use secure passwords.  When the child gets their first bank account with online banking it's time to change password. Next should be 16, after 16 **only ever change a password** if it's believed it has been compromised.  Be sure to make this password as complex as possible, but still using the above system (so use more words, more digits in numbers, more weird ways to join words). 

**Above all is length** contrary to corporate nonsense, the number of types of characters is not so important (like using punctuation, upper case and numbers).  Too long and it becomes a pain to type, and this is the main benifit of using other forms of complexity.

### 1.2 Secondary Passwords

When you need to create an account for any website other than Gmail or a non-operating-system password do NOT use the same passwords as Gmail or your operating system.

#### 1.2.1 Websites/Tools That Manage Property or Money

For example this could be Amazon Prime, online banking etc, games that allow in app purchases, app stores where a credit/debit card has been added, any tool that allows you to buy something without entering card details by storing card details.

 - If you need to invent a password use the same process as described in 1.1.1
 - Keep every password different, especially different from your gmail & operating system password
 - Setup Multi-factor authentication (see section 2)
 - See section 8

##### Storage

To store these passwords (since it is hard to remember so many) try to invent a reminder system and store the reminders in emails sent to yourself from yourself.  For example you could store the first letters of each of the words along with the numbers.  E.g. `57,gianT,potatoeS,leakeD,14,lanternS,happilY` would become `57gpl14lh`.

Other approaches could be to write down on paper the passwords (see section 1.1.2) but this can be quite inconvenient for passwords used regularly.  For some things of low value plaintexting (i.e. written out in full without any encoding/encryption) the password in your email is acceptable, anything of high value should not be plaintexted.

#### 1.2.2 Websites/Tools That do NOT Manage Anything of Value

For example Forums, Stupid websites that want you to register, etc.

Use a password manager, like this one https://chrome.google.com/webstore/detail/lastpass-free-password-ma/hdokiejnpimakedhajhdlcegeplioahd.  Do NOT use a password manager for Gmail.

## 2 Multi Factor Authentication

**Two factor authentication is the best thing you can do to make something secure EVER**

You should setup two factor authentication with your Gmail. This will mean Google will text your phone a password when you try to login for the first time on a device.

It's assumed any online banking will require 2-factor authentication via a chip-and-pin device. If you happen to have a bank that doesn't require 2-factor auth, change bank right now.

To make it EVEN more unlikely that a robber could use your phone and your password, (and know your login ID), protect your phone with a pin or finger id.

## 3 Alternative Authentication Systems (like finger id)

All these new fancy techniques like finger recognition or voice recognition can be convenient for many use cases, but are not as secure as good passwords.

Nevertheless as an _additional_ form of authentication they are highly recommended for protecting digital value (see Section 2 & 1.2.1)

## 4 Locking Your Computer

Get into the habbit of locking your computer whenever you leave it unattended whereever you are.  Ensure the timeout is active, but it is not of great importance how long it is, it should be less than 12 hours.

The shorcut on a Mac is CTRL + SHIFT + POWER-KEY, the shortcut for Linux is usually CTRL + ALT + L, and the shortcut for Windows is probably WIN + L.

To encourage the habbit, when you see a laptop unlocked, play a prank on that person.

## 5 Logging Into Email

Only login to email on your own computer or a computer you trust.  You can only trust computers where you know that the user goes to the same lengths as you to keep your computer secure. I only login to my gmail using computers I bought and I installed.  I once had to use a password for an online app on a shop computer, after which I immediately changed my password.  I would NEVER use a shop computer to login into gmail.

## 6 Anti-virus Software

Is utterly useless.  Contrary to their publicity. Avoid installing any anti virus software, they are likely to **decrease** the security of your machine. (See notes)

## 7 Downloading And Installing Software

Never download any software from a website and install it.  You must use a package manager, the package manager will be the only thing you install manually (not required for linux).

### 7.1 Linux

Not required, will have something like `apt-get` or `yum` pre-installed

### 7.2 Mac

Install brew by running the following command in a terminal:

`brew >/dev/null || ruby -e "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/master/install)"`

You can then install Steam using `brew cask install steam`.  You can then use Steam to download games.

### 7.3 Windows

Use Chocalatey (not documented here).

### 7.4 How to Use a Package Manager

When you want to install some software, put this into Google: `<name of package manager> install <name of software>`. For example if you wanted to install Open Office on a Mac search for `brew install open office`, then one of the links will have the command you need to copy and paste into your terminal.  The command will always be 1 simple line, like `sudo apt-get install open-office` or `brew install potatoes`, it should not look more complex than this.

## 8 (OPTIONAL) Storing Digital Value / Property

If you go to all the lengths taken in this tutorial to the highest degree the only mechanisms of loss can occur from the following:

### 8.1 Company Hacking

The company/tool you are using to digitally store or manage value is hacked. This is surprisingly common. Nevertheless in this situation **you** are not responsible and so you are entitled to compensation.  Of course whether or not a company actually will pay out is another matter and not covered in this tutorial.

### 8.2 Loss of Authentication Keys

It's possible that your house burns down, and/or a head injury means your lose your password, computer and phone.  For some types of company it will be possible to regain access to your property using your identification alone.  In such rare circumstances you can get a passport by having whitnesses vouch for you being you.

This is especially true for companies like banks, therefore they are great places to store large amounts of value permanently.  Other legal mechanisms are useful, like land registries.  **But not everything works this way** for example bitcoin is not tied to your identity, if you lose keys to access your bitcoin it's gone forever and ever.  Therefore you need to be careful when choosing how to store large amounts of value permanently in order to protect yourself against the most special of circumstances.

# Notes

This section contains explanations to the above instructions.

### 1.1

Mac, Linux and Gmail can be considered secure platforms (the BSD variants of Linux being the most secure). Windows cannot be trusted as highly so keep your password for a Windows system separate.

**In general you want to minimise the number of passwords you remember** less is more.  Simplicity is key.

#### 1.1.1

As noted this particular system of generating passwords ensures rather long passwords, that may be awkward to type.  With practice you ought to be able to type it fast so it shouldn't matter.  Nevertheless you may have a job that requires frequently logging out and logging in again.  Some tips for ensuring easy to type passwords:

 - Test typing the password before becoming satisfied with it
 - Words/passwords that allow you to alternate left-right hands are often easier to type, e.g. jfjfjfjfjfjfjfjdkdkdkdkdkd
 - Words/passwords that allow you to use alternating fingers (rather than the same finger) are easier to type. For example jkjkjkjkjkjkdfdfdfdfdf is easier to type than jujujujujfrfrfrfr

#### 1.2.1

##### Storage

Only highly sophisticated hackers could write algorithms that could try to brute force the other words - furthermore most good websites will lock accounts that have too many authentication failures.

#### 1.1.3

A password only needs to be as complex as the data it protects.  

Avoid changing passwords because this introduces complexity.  Complexity introduces work arounds and room for mistakes.  Keep the system simple **and stick to it**.  Large corporations often ask employees to change their passwords regularly, this is why large corportaions are frequently on the news for getting hacked - most have NO IDEA about security.  

Large corporations cannot be trusted as examples of how to secure systems. They employ IT staff to handle security that do not have degrees in STEM. Corporations often pay other large corporations to do security for them while not having any talent in house. Corporations often **only** care about insurance, not about security.  Being insured and being secure are entirely different things.  Insurance companies also tend to employ uneducated people to determine their rules.  Crucially insurance polcies will require "anti-virus" software, maybe this is because the "anti-virus" software companies pay the insurance companies to do so - though incomptence is more likely.  The only **genuine** way to keep a corporation secure is to hire top in house STEM talent and frequent penetration testing from multiple independent testers - the smaller the tester, the better.

## 2

2 factor auth means someone will have to steal your phone as well as your password.

## 3

The more complex the technique of authenticating the higher the probability it will contain a flaw or exploit either now, or in the future.  A password guarantees mathematically a certain level of security.

Always use the simplest possible thing that works, because that thing will work most predicably and for the longest amount of time.

## 6

"Anti virus" software will likely run as root or admin on your system.  Introducing any program and allowing to run 24/7 on your machine as root/admin is a bad idea.  In fact "Anit virus" software may contain back doors put in intentionally to spy on terrorists, which in all likelyhood is actually being used by scammers.

## 7

Package managers cannot be fooled, but you can.  You may one day visit a website that is actually not the correct website.  This may seem unlikely, but it's better to get into the habit of using package managers.  They work because they check the softwares digital "signature" when they download it to make sure it's the real deal.
 


