package io.github.mths0x5f.keycloak.providers.sms.sender;

import br.com.totalvoice.TotalVoiceClient;
import br.com.totalvoice.api.Sms;
import io.github.mths0x5f.keycloak.providers.sms.exception.MessageSendException;
import io.github.mths0x5f.keycloak.providers.sms.spi.MessageSenderService;
import org.json.JSONObject;
import org.keycloak.Config.Scope;

public class TotalVoiceMessageSenderService implements MessageSenderService {

    private final TotalVoiceClient client;
    private final Sms smsClient;

    TotalVoiceMessageSenderService(Scope config) {
        this.client = new TotalVoiceClient(config.get("authToken"));
        this.smsClient = new Sms(client);
    }

    @Override
    public void sendMessage(String phoneNumber, String message) throws MessageSendException {

        try {
            JSONObject response = smsClient.enviar(phoneNumber, message);

            if (!response.getBoolean("sucesso")) {
                throw new MessageSendException(response.getInt("status"),
                        String.valueOf(response.getInt("motivo")),
                        response.getString("mensagem"));
            }
        } catch (Exception e) {
            throw new MessageSendException(500, "500", "Unexpected exception");
        }
    }

    @Override
    public void close() {
    }
}
