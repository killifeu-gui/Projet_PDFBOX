package CalculatriceClient;

import CalculatriceApp.*;
import org.omg.CORBA.*;

import java.io.File;
import java.nio.file.Files;
import java.util.Base64;

public class StartPdfClientConversionEtTexte {
    private static String readFileToBase64(String path) throws Exception {
        byte[] bytes = Files.readAllBytes(new File(path).toPath());
        return Base64.getEncoder().encodeToString(bytes);
    }

    public static void main(String[] args) {
        try {
            // args[0]=pdf
            // args[1]=outputPath (image)
            // args[2]=dpi

            String pdfPath = (args.length > 0 ? args[0] : "input1.pdf");
            String outputPath = (args.length > 1 ? args[1] : "output.png");
            int dpi = (args.length > 2 ? Integer.parseInt(args[2]) : 150);

            // ORB comme dans StartClient.
            ORB orb = ORB.init(args, null);

            PdfService pdf = CorbaClientHelper.getPdfService(orb);

            String pdfB64 = readFileToBase64(pdfPath);

            // 1) Conversion PDF en Image
            ResultatPdf conversion = pdf.conversionPdfEnImage(pdfB64, dpi);
            if (!conversion.succes) {
                System.out.println("Conversion PDF->Image KO: " + conversion.message);
                return;
            }
            byte[] outConversion = Base64.getDecoder().decode(conversion.nomFichierSortie);
            Files.write(new File(outputPath).toPath(), outConversion);
            System.out.println("OK. Image ecrite dans: " + outputPath);

            // 2) Extraction de texte
            ResultatPdf extraction = pdf.extractionTexte(pdfB64);
            if (!extraction.succes) {
                System.out.println("Extraction de texte KO: " + extraction.message);
                return;
            }

            System.out.println("OK. Texte extrait du PDF:");
            System.out.println(extraction.message);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
