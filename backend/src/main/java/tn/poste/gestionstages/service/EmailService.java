package tn.poste.gestionstages.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendVerificationEmail(String email, String token, String firstName) {
        try {
            String verificationLink = "http://localhost:4200/verify-email?token=" + token;

            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("noreply@gestionstages.tn");
            message.setTo(email);
            message.setSubject("Vérification de votre email - Gestion des Stages");
            message.setText("Bonjour " + firstName + ",\n\n" +
                    "Veuillez vérifier votre adresse email en cliquant sur le lien ci-dessous :\n\n" +
                    verificationLink + "\n\n" +
                    "Ce lien expire dans 24 heures.\n\n" +
                    "Cordialement,\nL'équipe Gestion des Stages");

            mailSender.send(message);
            log.info("Email de vérification envoyé à : {}", email);
        } catch (Exception e) {
            log.error("Erreur lors de l'envoi de l'email de vérification à {}", email, e);
            throw new RuntimeException("Erreur lors de l'envoi de l'email de vérification");
        }
    }

    public void sendApprovalNotification(String email, String firstName) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("noreply@gestionstages.tn");
            message.setTo(email);
            message.setSubject("Votre compte encadrant a été approuvé - Gestion des Stages");
            message.setText("Bonjour " + firstName + ",\n\n" +
                    "Félicitations ! Votre compte encadrant a été approuvé par l'administrateur.\n\n" +
                    "Vous pouvez maintenant vous connecter à la plateforme :\n" +
                    "http://localhost:4200/login\n\n" +
                    "Cordialement,\nL'équipe Gestion des Stages");

            mailSender.send(message);
            log.info("Email d'approbation envoyé à : {}", email);
        } catch (Exception e) {
            log.error("Erreur lors de l'envoi de l'email d'approbation à {}", email, e);
        }
    }

    public void sendRejectionNotification(String email, String firstName) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("noreply@gestionstages.tn");
            message.setTo(email);
            message.setSubject("Votre compte encadrant a été rejeté - Gestion des Stages");
            message.setText("Bonjour " + firstName + ",\n\n" +
                    "Votre demande d'accès en tant qu'encadrant a été rejetée.\n\n" +
                    "Pour plus d'informations, veuillez contacter l'administrateur.\n\n" +
                    "Cordialement,\nL'équipe Gestion des Stages");

            mailSender.send(message);
            log.info("Email de rejet envoyé à : {}", email);
        } catch (Exception e) {
            log.error("Erreur lors de l'envoi de l'email de rejet à {}", email, e);
        }
    }

    /**
     * Email envoyé au stagiaire quand sa candidature est acceptée
     */
    public void sendCandidatureAcceptedEmail(String email, String firstName, String stageTitle) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("noreply@gestionstages.tn");
            message.setTo(email);
            message.setSubject("Votre candidature a été acceptée - Gestion des Stages");
            message.setText("Bonjour " + firstName + ",\n\n" +
                    "Félicitations ! Votre candidature au stage \"" + stageTitle + "\" a été acceptée.\n\n" +
                    "Vous pouvez maintenant vous connecter à la plateforme pour suivre votre affectation :\n" +
                    "http://localhost:4200/login\n\n" +
                    "Cordialement,\nL'équipe Gestion des Stages");

            mailSender.send(message);
            log.info("Email d'acceptation de candidature envoyé à : {}", email);
        } catch (Exception e) {
            log.error("Erreur lors de l'envoi de l'email d'acceptation à {}", email, e);
        }
    }

    /**
     * Email envoyé au stagiaire quand sa candidature est refusée
     */
    public void sendCandidatureRejectedEmail(String email, String firstName, String stageTitle) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("noreply@gestionstages.tn");
            message.setTo(email);
            message.setSubject("Votre candidature a été refusée - Gestion des Stages");
            message.setText("Bonjour " + firstName + ",\n\n" +
                    "Nous regrettons de vous informer que votre candidature au stage \"" + stageTitle + "\" a été refusée.\n\n" +
                    "N'hésitez pas à consulter nos autres offres de stage disponibles.\n\n" +
                    "Cordialement,\nL'équipe Gestion des Stages");

            mailSender.send(message);
            log.info("Email de rejet de candidature envoyé à : {}", email);
        } catch (Exception e) {
            log.error("Erreur lors de l'envoi de l'email de rejet de candidature à {}", email, e);
        }
    }

    /**
     * Email envoyé au stagiaire quand il est affecté à un encadrant
     */
    public void sendAffectationEmail(String email, String firstName, String encadrantName, String stageTitle) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("noreply@gestionstages.tn");
            message.setTo(email);
            message.setSubject("Affectation à un encadrant - Gestion des Stages");
            message.setText("Bonjour " + firstName + ",\n\n" +
                    "Vous avez été affecté à l'encadrant " + encadrantName + " pour le stage \"" + stageTitle + "\".\n\n" +
                    "Veuillez vous connecter à la plateforme pour plus de détails :\n" +
                    "http://localhost:4200/login\n\n" +
                    "Cordialement,\nL'équipe Gestion des Stages");

            mailSender.send(message);
            log.info("Email d'affectation envoyé à : {}", email);
        } catch (Exception e) {
            log.error("Erreur lors de l'envoi de l'email d'affectation à {}", email, e);
        }
    }

    /**
     * Email envoyé au stagiaire quand son rapport est validé
     */
    public void sendRapportValidatedEmail(String email, String firstName, String stageTitle) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("noreply@gestionstages.tn");
            message.setTo(email);
            message.setSubject("Votre rapport a été validé - Gestion des Stages");
            message.setText("Bonjour " + firstName + ",\n\n" +
                    "Félicitations ! Votre rapport pour le stage \"" + stageTitle + "\" a été validé.\n\n" +
                    "Consultez la plateforme pour plus de détails :\n" +
                    "http://localhost:4200/login\n\n" +
                    "Cordialement,\nL'équipe Gestion des Stages");

            mailSender.send(message);
            log.info("Email de validation de rapport envoyé à : {}", email);
        } catch (Exception e) {
            log.error("Erreur lors de l'envoi de l'email de validation de rapport à {}", email, e);
        }
    }

    /**
     * Email envoyé au stagiaire quand son rapport est rejeté
     */
    public void sendRapportRejectedEmail(String email, String firstName, String stageTitle) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("noreply@gestionstages.tn");
            message.setTo(email);
            message.setSubject("Votre rapport a été rejeté - Gestion des Stages");
            message.setText("Bonjour " + firstName + ",\n\n" +
                    "Votre rapport pour le stage \"" + stageTitle + "\" a été rejeté.\n\n" +
                    "Veuillez le revoir et le resoumettve. Consultez la plateforme pour plus de détails :\n" +
                    "http://localhost:4200/login\n\n" +
                    "Cordialement,\nL'équipe Gestion des Stages");

            mailSender.send(message);
            log.info("Email de rejet de rapport envoyé à : {}", email);
        } catch (Exception e) {
            log.error("Erreur lors de l'envoi de l'email de rejet de rapport à {}", email, e);
        }
    }
}
