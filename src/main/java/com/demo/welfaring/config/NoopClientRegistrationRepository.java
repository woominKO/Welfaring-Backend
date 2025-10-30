package com.demo.welfaring.config;

import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;

import java.util.Collections;
import java.util.Iterator;

/**
 * A no-op ClientRegistrationRepository that contains no registrations.
 * This allows the application to start even when OAuth registrations
 * are not provided via environment variables.
 */
public class NoopClientRegistrationRepository implements ClientRegistrationRepository, Iterable<ClientRegistration> {

    @Override
    public ClientRegistration findByRegistrationId(String registrationId) {
        return null;
    }

    @Override
    public Iterator<ClientRegistration> iterator() {
        return Collections.<ClientRegistration>emptyList().iterator();
    }
}


