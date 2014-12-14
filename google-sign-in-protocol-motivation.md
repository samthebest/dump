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
