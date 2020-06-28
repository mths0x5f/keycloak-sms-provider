package io.github.mths0x5f.keycloak.providers.sms.sender;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import io.github.mths0x5f.keycloak.providers.sms.exception.MessageSendException;
import io.github.mths0x5f.keycloak.providers.sms.spi.MessageSenderService;
import org.jboss.logging.Logger;
import org.keycloak.Config.Scope;

public class TwilioMessageSenderService implements MessageSenderService {

    private static final Logger logger = Logger.getLogger(TwilioMessageSenderService.class);
    private final String twilioPhoneNumber;

    TwilioMessageSenderService(Scope config) {
        Twilio.init(config.get("accountSid"), config.get("authToken"));
        this.twilioPhoneNumber = config.get("twilioPhoneNumber");
    }

    @Override
    public void sendMessage(String phoneNumber, String message) throws MessageSendException {

        Message msg = Message.creator(
                new PhoneNumber(phoneNumber),
                new PhoneNumber(twilioPhoneNumber),
                message).create();

        if (msg.getStatus() == Message.Status.FAILED) {
            throw new MessageSendException(msg.getStatus().ordinal(),
                    String.valueOf(msg.getErrorCode()),
                    msg.getErrorMessage());
        }
    }

    @Override
    public void close() {
    }
}
