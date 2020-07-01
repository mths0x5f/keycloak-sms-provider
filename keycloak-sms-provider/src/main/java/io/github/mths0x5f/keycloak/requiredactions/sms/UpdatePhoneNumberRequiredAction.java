package io.github.mths0x5f.keycloak.requiredactions.sms;

import io.github.mths0x5f.keycloak.providers.sms.spi.TokenCodeService;
import org.keycloak.authentication.RequiredActionContext;
import org.keycloak.authentication.RequiredActionProvider;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.core.Response;

public class UpdatePhoneNumberRequiredAction implements RequiredActionProvider {

    public static final String PROVIDER_ID = "UPDATE_PHONE_NUMBER";

    @Override
    public void evaluateTriggers(RequiredActionContext context) {
    }

    @Override
    public void requiredActionChallenge(RequiredActionContext context) {
        String phoneNumber = context.getUser().getFirstAttribute("phoneNumber");
        Response challenge = context.form()
                .setAttribute("phoneNumber", phoneNumber)
                .createForm("login-update-phone-number.ftl");
        context.challenge(challenge);
    }

    @Override
    public void processAction(RequiredActionContext context) {
        TokenCodeService tokenCodeService = context.getSession().getProvider(TokenCodeService.class);
        String phoneNumber = context.getHttpRequest().getDecodedFormParameters().getFirst("phoneNumber");
        String code = context.getHttpRequest().getDecodedFormParameters().getFirst("code");
        try {
            tokenCodeService.validateCode(context.getUser(), phoneNumber, code);
            context.success();
        } catch (BadRequestException e) {

            Response challenge = context.form()
                    .setError("noOngoingVerificationProcess")
                    .createForm("login-update-phone-number.ftl");
            context.challenge(challenge);

        } catch (ForbiddenException e) {

            Response challenge = context.form()
                    .setAttribute("phoneNumber", phoneNumber)
                    .setError("verificationCodeDoesNotMatch")
                    .createForm("login-update-phone-number.ftl");
            context.challenge(challenge);
        }
    }

    @Override
    public void close() {
    }
}
