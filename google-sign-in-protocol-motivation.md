Having read the documentation on Google sign-in I've found it to only explain the how and the what developers need to do, not any of the WHY.  If your like me and find it hard to implement anything without understanding why, especially when you are concerned about users security, then this should be interesting.  We will go step by step through each bit of the protocol.

### Anti-request forgery state token - prevents CSRF Attack

First the attacker must

 - Trick the user's browser into sending HTTP requests to a target site
 - Involve HTTP requests that have side effects
 - The attacker must determine the right values for all the forms or URL inputs; if any of them are required to be secret authentication values or IDs that the attacker can't guess, the attack will fail.

##### CSRF - 1. DOS / Accidental Blocking Attack

For example, this very document could contain an image tag: \<img src="http://mysite.com/login" /\> or several hundred, which could cause the user to get locked out (maybe the server enforces some limit on the number of times the user can visit a specifc URL).  

##### CSRF - 2. Privacy Attack

Other more sophisticated scripts might login the user using an attackers credentials (the script magically inputs the attackers username and password), if the user was sufficiently stupid they may then use the site while doing private things.  Later the attacker could login to the site and possibly view history of what the user did.

This attack only applies to websites where privacy is an issue, and history is recorded in the users profile.

##### CSRF - 3. State changing URLs while logged in

Suppose the user has actually logged in, and the URL is "mysite.com/vote/25" then that user may unintentially vote for something. 

##### CSRF - Anti-request forgery state token Solution

Attacks 1 & 3 can be avoided without an Anti-request forgery state token - instead the website can use hidden form fields http://en.wikipedia.org/wiki/HTTP_cookie#Hidden_form_fields

The Google+ Sign-In documentation essentially fleshes this out by making it explicit how to do this, but once the user is signed in the hidden field could just be the access_token (which we will come to). Perhaps Google just wanted to add in this somewhat redundant layer just in case web developers where submitting the access_token only using cookies, and given them the flexibility to submit access_tokens in this way.  

Attack 2 can only be avoided with the Anti-request forgery state token, but is a strange somewhat pointless attack.

### access_token and id_token fields

The access_token should be considered as a shared secret between the client and server, and the id_token contains the id of the user (it needs to be decoded using the google public key).

These are the most important fields in the protocol, the point of the protocol is to get the client and server into a state (the process by which we will explain) such that the server knows these fields and knows access_token is held by the user corresponding to the id_token, while no one else knows the access_token.  Then every time the user wants to do something which requires authentication, they can send the access_token (and an id, which can be extracted from the token_id) (SSL encrypted so no one else can read sniff it) to the server and the server can check "yup this access_token corresponds to the said user, let's do what they asked".

The id_token field is necessary for sites where different users have different permissions/ownership.  Some sites may have a simple "your in or your out" policy in which case the id isn't necessary.

### Digression - SSL

Here we give a super fast explanation of how SSL connections can encrypt all your traffic between two points.

1. Browser requests public key
2. Server sends public key
3. Browser creates a symmetric session key, and encrypts it using the public key
4. Server decrypts symmetric session key with private key
5. Server and Browser can not use session key to encrypt all traffic

### Step 1 - Website creates CLIENT_ID and CLIENT_SECRET fields

Using SSL the webmasters use the google console to create their CLIENT_ID and CLIENT_SECRET fields, it's essential the CLIENT_SECRET is kept secret the ID cannot be kept secret as it will have to be given to the client.

### Step 2 - User signs in using google button

First the user is redirected, the username and password are not given to the website, only Google and the user knows the username and password thanks to SSL.  

##### Digression into sign in saftey

This is essence the most vulnerable part of the protocol if the user is on a dodgy network, where that network has a fake Google running and is tricking users into giving away their username and password.  The protections the user must take are:

1. Make sure the URL starts with https - if a website is found out to be tricking users into giving away passwords, it's SSL certificate may get revoked and thus https wouldn't work.
2. Make sure the website/login screen looks geniune
3. Trust the network you are on (very difficult, basically impossible unless home or a good office)
4. Immediately check that since your login you can now access data that only you knew, e.g. read emails or something, if you cannot view your emails then it may be a fake site.  **If you cannot view emails, you should straight away find another network, login to Google again, and change your password.**
5. Use 2-step authentication - if someone obtains your password it means it's relatively useless without one of your devices.  Make sure you enable 2-step authentication while on a trusted network.


