package io.github.mths0x5f.keycloak.providers.sms.rest;

import org.keycloak.models.KeycloakSession;
import org.keycloak.services.resource.RealmResourceProvider;

public class UserResourceProvider implements RealmResourceProvider {

    private KeycloakSession session;

    UserResourceProvider(KeycloakSession session) {
        this.session = session;
    }

    @Override
    public Object getResource() {
        return new VerificationCodeResource(session);
    }

    @Override
    public void close() {
    }
}