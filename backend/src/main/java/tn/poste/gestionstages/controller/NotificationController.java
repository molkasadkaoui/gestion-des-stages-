package tn.poste.gestionstages.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.poste.gestionstages.dto.NotificationResponse;
import tn.poste.gestionstages.service.NotificationService;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<Page<NotificationResponse>> getMesNotifications(Pageable pageable) {
        return ResponseEntity.ok(notificationService.getMesNotifications(pageable));
    }

    @GetMapping("/non-lues/count")
    public ResponseEntity<Long> getNombreNonLues() {
        return ResponseEntity.ok(notificationService.getNombreNonLues());
    }

    @PatchMapping("/{id}/marquer-comme-lue")
    public ResponseEntity<Void> marquerCommeLue(@PathVariable Long id) {
        notificationService.marquerCommeLue(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/marquer-tout-comme-lu")
    public ResponseEntity<Void> marquerToutCommeLu() {
        notificationService.marquerToutCommeLu();
        return ResponseEntity.noContent().build();
    }
}
