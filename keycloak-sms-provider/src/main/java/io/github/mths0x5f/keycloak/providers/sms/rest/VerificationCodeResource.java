package io.github.mths0x5f.keycloak.providers.sms.rest;

import io.github.mths0x5f.keycloak.providers.sms.constants.TokenCodeType;
import io.github.mths0x5f.keycloak.providers.sms.representations.TokenCodeRepresentation;
import io.github.mths0x5f.keycloak.providers.sms.spi.PhoneMessageService;
import io.github.mths0x5f.keycloak.providers.sms.spi.TokenCodeService;
import org.jboss.logging.Logger;
import org.jboss.resteasy.annotations.cache.NoCache;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.UserModel;
import org.keycloak.services.managers.AppAuthManager;
import org.keycloak.services.managers.AuthenticationManager.AuthResult;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

public class VerificationCodeResource {

    private static final Logger logger = Logger.getLogger(VerificationCodeResource.class);
    private final KeycloakSession session;
    private final AuthResult auth;

    VerificationCodeResource(KeycloakSession session) {
        this.session = session;
        this.auth = new AppAuthManager().authenticateBearerToken(session, session.getContext().getRealm());
    }

    private TokenCodeService getTokenCodeService() {
        return session.getProvider(TokenCodeService.class);
    }

    @GET
    @NoCache
    @Path("verification-code")
    @Produces(APPLICATION_JSON)
    public Response getVerificationCode() {

        if (auth == null) throw new NotAuthorizedException("Bearer");

        UserModel user = auth.getUser();

        String phoneNumber = user.getFirstAttribute("phoneNumber");
        if (phoneNumber == null) throw new BadRequestException("User does not have a phone number attribute");

        logger.info(String.format("Requested verification code to %s for user of id %s", phoneNumber, user.getId()));
        session.getProvider(PhoneMessageService.class).sendVerificationCode(user);

        return Response.noContent().build();
    }

    @POST
    @NoCache
    @Path("verification-code")
    @Produces(APPLICATION_JSON)
    public Response checkVerificationCode(@QueryParam("code") String code) {

        if (auth == null) throw new NotAuthorizedException("Bearer");

        UserModel user = auth.getUser();

        String phoneNumber = user.getFirstAttribute("phoneNumber");
        if (phoneNumber == null) throw new BadRequestException("User does not have a phone number attribute");

        TokenCodeRepresentation tokenCode = getTokenCodeService().ongoingProcess(user, TokenCodeType.VERIFY_PHONE_NUMBER);
        if (tokenCode == null) throw new BadRequestException("There is no valid ongoing verification process");

        if (!tokenCode.getCode().equals(code)) throw new ForbiddenException("Code does not match with expected value");

        logger.info(String.format("User %s correctly answered the verification code", user.getId()));
        session.users()
                .searchForUserByUserAttribute("phoneNumber", phoneNumber, session.getContext().getRealm())
                .stream().filter(u -> !u.getId().equals(user.getId()))
                .forEach(u -> {
                    logger.info(String.format("User %s also has phone number %s. Un-verifying.", u.getId(), phoneNumber));
                    u.setSingleAttribute("isPhoneNumberVerified", "false");
                });

        user.setSingleAttribute("isPhoneNumberVerified", "true");
        getTokenCodeService().validateProcess(tokenCode.getId());

        return Response.noContent().build();
    }
}
