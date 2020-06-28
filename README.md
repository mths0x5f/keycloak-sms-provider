# Keycloak SMS Provider

With this provider you can **enforce authentication policies based on a verification token sent to users' mobile phones**.
Currently, there are implementations of Twilio and TotalVoice SMS sender services. That said, is nice to note that more
services can be used with ease thankfully for the adopted modularity and in fact, nothing stop you from implementing a 
sender of TTS calls or WhatsApp messages. 

This is what you can do:
  + [x] Check ownership of phone number
  + [ ] Use SMS as second factor in 2FA method (Browser)
  + [ ] Use SMS-Token pair as credentials for the login
  + [ ] Use SMS to reset password
  
## Compatibility

This was initially developed using 10.0.2 version of Keycloak as test base, but as long the version adopted by you provides
the needed SPIs it is fine. Give it a try and report if anything does not work as expected. I will **try** to gladly help.

___
### to be continued...
