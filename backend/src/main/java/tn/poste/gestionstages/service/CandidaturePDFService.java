package tn.poste.gestionstages.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tn.poste.gestionstages.entity.Candidature;
import tn.poste.gestionstages.entity.Stagiaire;
import tn.poste.gestionstages.repository.CandidatureRepository;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@Slf4j
public class CandidaturePDFService {

    private final CandidatureRepository candidatureRepository;

    /**
     * Générer un PDF pour une candidature (version simple en HTML converti)
     */
    public byte[] generateCandidaturePDF(Long candidatureId) {
        Candidature candidature = candidatureRepository.findById(candidatureId)
                .orElseThrow(() -> new IllegalArgumentException("Candidature introuvable"));

        try {
            // Construire le contenu HTML
            String htmlContent = generateHtmlContent(candidature);

            // Pour une solution simple, générer un PDF basique texte
            // En production, utiliser une librairie comme wkhtmltopdf ou Thymeleaf avec Flying Saucer
            byte[] pdfContent = generateSimplePDF(htmlContent);

            log.info("PDF généré pour la candidature ID: {}", candidatureId);
            return pdfContent;

        } catch (Exception e) {
            log.error("Erreur lors de la génération du PDF pour la candidature: {}", candidatureId, e);
            throw new RuntimeException("Erreur lors de la génération du PDF");
        }
    }

    /**
     * Générer le contenu HTML de la candidature
     */
    private String generateHtmlContent(Candidature candidature) {
        Stagiaire stagiaire = candidature.getStagiaire();
        return String.format(
                "<html><body>" +
                "<h1>DEMANDE DE STAGE</h1>" +
                "<h2>Informations du Stagiaire</h2>" +
                "<p><strong>Nom:</strong> %s</p>" +
                "<p><strong>Prénom:</strong> %s</p>" +
                "<p><strong>Email:</strong> %s</p>" +
                "<p><strong>Établissement:</strong> %s</p>" +
                "<p><strong>Filière:</strong> %s</p>" +
                "<p><strong>Niveau d'étude:</strong> %s</p>" +
                "<h2>Informations du Stage</h2>" +
                "<p><strong>Titre:</strong> %s</p>" +
                "<p><strong>Service:</strong> %s</p>" +
                "<p><strong>Type de stage:</strong> %s</p>" +
                "<p><strong>Date de début:</strong> %s</p>" +
                "<p><strong>Date de fin:</strong> %s</p>" +
                "<p><strong>Statut:</strong> %s</p>" +
                "<p><strong>Date:</strong> %s</p>" +
                "</body></html>",
                stagiaire.getUtilisateur().getNom(),
                stagiaire.getUtilisateur().getPrenom(),
                stagiaire.getUtilisateur().getEmail(),
                stagiaire.getEtablissement() != null ? stagiaire.getEtablissement() : "N/A",
                stagiaire.getFiliere() != null ? stagiaire.getFiliere() : "N/A",
                stagiaire.getNiveauEtude() != null ? stagiaire.getNiveauEtude() : "N/A",
                candidature.getStage().getTitre(),
                candidature.getStage().getService(),
                candidature.getStage().getTypeStage(),
                candidature.getStage().getDateDebut().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                candidature.getStage().getDateFin().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                candidature.getStatut(),
                java.time.LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
        );
    }

    /**
     * Générer un PDF simple (texte)
     * En production, utiliser une vraie librairie PDF
     */
    private byte[] generateSimplePDF(String htmlContent) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        
        try {
            // Pour maintenant, retourner le contenu HTML encodé en bytes
            // En production, utiliser Flying Saucer ou wkhtmltopdf
            String pdfText = htmlContent
                    .replaceAll("<[^>]*>", "")
                    .replaceAll("&nbsp;", " ")
                    .replaceAll("&lt;", "<")
                    .replaceAll("&gt;", ">");

            out.write(pdfText.getBytes());
            return out.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la génération du PDF", e);
        }
    }
}

