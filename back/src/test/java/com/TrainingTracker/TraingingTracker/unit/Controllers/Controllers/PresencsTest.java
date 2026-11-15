package com.TrainingTracker.TraingingTracker.unit.Controllers.Controllers;

import com.TrainingTracker.TraingingTracker.Controllers.Presencs;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.User.UserPresence;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Entites.User;
import com.TrainingTracker.TraingingTracker.Security.User.SecurityUser;
import java.security.Principal;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class PresencsTest {

    @Mock
    private ApplicationEventPublisher publisher;

    @InjectMocks
    private Presencs presencs;

    private Principal principalFor(Long userId) {
        User user = User.builder().id(userId).build();
        return new UsernamePasswordAuthenticationToken(new SecurityUser(user), null, List.of());
    }

    @Test
    void testUpdatePresenceUsesAuthenticatedUser() {
        Long userId = 10L;
        Principal principal = principalFor(userId);
        UserPresence payload = new UserPresence(99L);

        presencs.updatePresence(payload, principal);

        verify(publisher).publishEvent(new UserPresence(userId));
    }

    @Test
    void testUpdatePresenceWithoutAuthenticationShouldFail() {
        assertThrows(AccessDeniedException.class, () -> presencs.updatePresence(null, null));

        verifyNoInteractions(publisher);
    }

    @Test
    void testUpdatePresenceWithUnexpectedPrincipalShouldFail() {
        Principal principal = new UsernamePasswordAuthenticationToken("John", null, List.of());

        assertThrows(AccessDeniedException.class, () -> presencs.updatePresence(null, principal));

        verifyNoInteractions(publisher);
    }
}
