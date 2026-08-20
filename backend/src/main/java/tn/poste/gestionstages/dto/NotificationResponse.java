package tn.poste.gestionstages.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class NotificationResponse {
    private Long id;
    private String titre;
    private String message;
    private String type;
    private Boolean lu;
    private LocalDateTime dateCreation;
}
