package com.babacar.pdfapi;

import CalculatriceApp.PlagePages;
import CalculatriceApp.ResultatPdf;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class PdfController {

    private final CorbaService corbaService;

    public PdfController(CorbaService corbaService) {
        this.corbaService = corbaService;
    }

    @PostMapping("/fusion")
    public String fusion(@RequestBody Map<String, String> payload) {
        String pdf1Base64 = payload.get("pdf1");
        String pdf2Base64 = payload.get("pdf2");

        ResultatPdf resultat = corbaService.getPdfService().fusionPdf(pdf1Base64, pdf2Base64);

        if (resultat.succes) {
            return resultat.nomFichierSortie;
        } else {
            return resultat.message;
        }
    }

    @PostMapping("/decoupage")
    public String decoupage(@RequestBody Map<String, Object> payload) {
        String pdfBase64 = (String) payload.get("pdf");
        int debut = (int) payload.get("debut");
        int fin = (int) payload.get("fin");

        PlagePages plage = new PlagePages(debut, fin);
        ResultatPdf resultat = corbaService.getPdfService().decoupagePdf(pdfBase64, plage);

        if (resultat.succes) {
            return resultat.nomFichierSortie;
        } else {
            return resultat.message;
        }
    }

    @PostMapping("/extraction")
    public String extraction(@RequestBody Map<String, Object> payload) {
        String pdfBase64 = (String) payload.get("pdf");
        int page = (int) payload.get("page");

        PlagePages plage = new PlagePages(page, page);
        ResultatPdf resultat = corbaService.getPdfService().extractionPage(pdfBase64, plage);

        if (resultat.succes) {
            return resultat.nomFichierSortie;
        } else {
            return resultat.message;
        }
    }

    @PostMapping("/suppression")
    public String suppression(@RequestBody Map<String, Object> payload) {
        String pdfBase64 = (String) payload.get("pdf");
        int debut = (int) payload.get("debut");
        int fin = (int) payload.get("fin");

        PlagePages plage = new PlagePages(debut, fin);
        ResultatPdf resultat = corbaService.getPdfService().suppressionPage(pdfBase64, plage);

        if (resultat.succes) {
            return resultat.nomFichierSortie;
        } else {
            return resultat.message;
        }
    }

    @PostMapping("/ajout-mot-de-passe")
    public String ajoutMotDePasse(@RequestBody Map<String, String> payload) {
        String pdfBase64 = payload.get("pdf");
        String password = payload.get("password");

        ResultatPdf resultat = corbaService.getPdfService().ajoutMotDePasse(pdfBase64, password);

        if (resultat.succes) {
            return resultat.nomFichierSortie;
        } else {
            return resultat.message;
        }
    }

    @PostMapping("/conversion-image")
    public String conversionImage(@RequestBody Map<String, Object> payload) {
        String pdfBase64 = (String) payload.get("pdf");
        int dpi = (int) payload.get("dpi");

        ResultatPdf resultat = corbaService.getPdfService().conversionPdfEnImage(pdfBase64, dpi);

        if (resultat.succes) {
            return resultat.nomFichierSortie;
        } else {
            return resultat.message;
        }
    }

    @PostMapping("/extraction-texte")
    public String extractionTexte(@RequestBody Map<String, String> payload) {
        String pdfBase64 = payload.get("pdf");

        ResultatPdf resultat = corbaService.getPdfService().extractionTexte(pdfBase64);

        if (resultat.succes) {
            return resultat.message;
        } else {
            return resultat.message;
        }
    }

    @PostMapping("/creation-pdf")
    public String creationPdf(@RequestBody Map<String, String> payload) {
        String texte = payload.get("texte");

        ResultatPdf resultat = corbaService.getPdfService().creationPdf(texte);

        if (resultat.succes) {
            return resultat.nomFichierSortie;
        } else {
            return resultat.message;
        }
    }
}
