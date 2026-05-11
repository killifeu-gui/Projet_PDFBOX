package CalculatriceClient;

import CalculatriceApp.*;
import org.omg.CORBA.*;
import org.omg.CosNaming.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;

/**
 * Client CORBA pour l'extraction de page PDF.
 * Usage: java ClientExtraction pdf.pdf page output.pdf
 */
public class ClientExtraction
{
    private static final Base64.Encoder ENCODER = Base64.getEncoder();
    private static final Base64.Decoder DECODER = Base64.getDecoder();

    public static void main(String[] args)
    {
        if (args.length != 3)
        {
            System.out.println("Usage: java ClientExtraction pdf.pdf page output.pdf");
            return;
        }

        String pdfPath = args[0];
        int page = Integer.parseInt(args[1]);
        String outputPath = args[2];

        try
        {
            // Initialisation ORB
            ORB orb = ORB.init(args, null);

            // Obtenir le service PDF
            PdfService pdfService = CorbaClientHelper.getPdfService(orb);

            // Lire le PDF en base64
            String pdfBase64 = encodeFileToBase64(pdfPath);

            // Créer PlagePages pour une seule page
            PlagePages plage = new PlagePages();
            plage.debut = page;
            plage.fin = page;

            // Appeler l'extraction
            ResultatPdf resultat = pdfService.extractionPage(pdfBase64, plage);

            if (resultat.succes)
            {
                // Décoder et sauvegarder
                byte[] pdfBytes = DECODER.decode(resultat.nomFichierSortie);
                Files.write(Paths.get(outputPath), pdfBytes);
                System.out.println("Extraction réussie : " + outputPath);
            }
            else
            {
                System.err.println("Erreur extraction : " + resultat.message);
            }
        }
        catch (Exception e)
        {
            System.err.println("Erreur : " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static String encodeFileToBase64(String path) throws IOException
    {
        byte[] bytes = Files.readAllBytes(Paths.get(path));
        return ENCODER.encodeToString(bytes);
    }
}