# Keycloak SMS Provider

With this provider you can **enforce authentication policies based on a verification token sent to users' mobile phones**.
Currently, there are implementations of Twilio and TotalVoice SMS sender services. That said, is nice to note that more
services can be used with ease thankfully for the adopted modularity and in fact, nothing stop you from implementing a 
sender of TTS calls or WhatsApp messages. 

This is what you can do:
  + Check ownership of a phone number (Forms and HTTP API)
  + Use SMS as second factor in 2FA method (Browser flow)
  + Use Phone number/OTP pair as credentials for the login (Direct grant flow)
  
## Compatibility

This was initially developed using 10.0.2 version of Keycloak as baseline, and I did not test another user storage beyond
the default like Kerberos or LDAP. I may try to help you but I cannot guarantee.

## Usage



## Thanks
Some code written is based on existing ones in these two projects: [keycloak-phone-authenticator](https://github.com/FX-HAO/keycloak-phone-authenticator)
and [keycloak-sms-authenticator](https://github.com/gwallet/keycloak-sms-authenticator). Certainly I would have many problems
coding all those providers blindly. Thank you!
