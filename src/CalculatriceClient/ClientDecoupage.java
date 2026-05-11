package CalculatriceClient;

import CalculatriceApp.*;
import org.omg.CORBA.*;
import org.omg.CosNaming.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;

/**
 * Client CORBA pour le découpage de PDF.
 * Usage: java ClientDecoupage pdf.pdf debut fin output.pdf
 */
public class ClientDecoupage
{
    private static final Base64.Encoder ENCODER = Base64.getEncoder();
    private static final Base64.Decoder DECODER = Base64.getDecoder();

    public static void main(String[] args)
    {
        if (args.length != 4)
        {
            System.out.println("Usage: java ClientDecoupage pdf.pdf debut fin output.pdf");
            return;
        }

        String pdfPath = args[0];
        int debut = Integer.parseInt(args[1]);
        int fin = Integer.parseInt(args[2]);
        String outputPath = args[3];

        try
        {
            // Initialisation ORB
            ORB orb = ORB.init(args, null);

            // Obtenir le service PDF
            PdfService pdfService = CorbaClientHelper.getPdfService(orb);

            // Lire le PDF en base64
            String pdfBase64 = encodeFileToBase64(pdfPath);

            // Créer PlagePages
            PlagePages plage = new PlagePages();
            plage.debut = debut;
            plage.fin = fin;

            // Appeler le découpage
            ResultatPdf resultat = pdfService.decoupagePdf(pdfBase64, plage);

            if (resultat.succes)
            {
                // Décoder et sauvegarder
                byte[] pdfBytes = DECODER.decode(resultat.nomFichierSortie);
                Files.write(Paths.get(outputPath), pdfBytes);
                System.out.println("Découpage réussi : " + outputPath);
            }
            else
            {
                System.err.println("Erreur découpage : " + resultat.message);
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