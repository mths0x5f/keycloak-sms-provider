package io.github.mths0x5f.keycloak.providers.sms.rest;

import io.github.mths0x5f.keycloak.providers.sms.spi.PhoneMessageService;
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
    public String checkVerificationCode() {
        if (auth == null) throw new NotAuthorizedException("Bearer");
        return "Hello ";
    }
}
