package CalculatriceClient;

import CalculatriceApp.*;
import org.omg.CORBA.*;
import org.omg.CosNaming.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;

/**
 * Client CORBA pour la fusion de PDFs.
 * Usage: java ClientFusion pdf1.pdf pdf2.pdf output.pdf
 */
public class ClientFusion
{
    private static final Base64.Encoder ENCODER = Base64.getEncoder();
    private static final Base64.Decoder DECODER = Base64.getDecoder();

    public static void main(String[] args)
    {
        if (args.length != 3)
        {
            System.out.println("Usage: java ClientFusion pdf1.pdf pdf2.pdf output.pdf");
            return;
        }

        String pdf1Path = args[0];
        String pdf2Path = args[1];
        String outputPath = args[2];

        try
        {
            // Initialisation ORB
            ORB orb = ORB.init(args, null);

            // Obtenir le service PDF
            PdfService pdfService = CorbaClientHelper.getPdfService(orb);

            // Lire les PDFs en base64
            String pdf1Base64 = encodeFileToBase64(pdf1Path);
            String pdf2Base64 = encodeFileToBase64(pdf2Path);

            // Appeler la fusion
            ResultatPdf resultat = pdfService.fusionPdf(pdf1Base64, pdf2Base64);

            if (resultat.succes)
            {
                // Décoder et sauvegarder
                byte[] pdfBytes = DECODER.decode(resultat.nomFichierSortie);
                Files.write(Paths.get(outputPath), pdfBytes);
                System.out.println("Fusion réussie : " + outputPath);
            }
            else
            {
                System.err.println("Erreur fusion : " + resultat.message);
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