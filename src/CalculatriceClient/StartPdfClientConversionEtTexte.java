import CalculatriceApp.*;
import org.omg.CosNaming.*;
import org.omg.CosNaming.NamingContextPackage.*;
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
            // args:
            // [0]=pdf input
            // [1]=outImage.png
            // [2]=dpi (optionnel)

            String inPath = (args.length > 0 ? args[0] : "input.pdf");
            String outImgPath = (args.length > 1 ? args[1] : "image1.png");
            int dpi = (args.length > 2 ? Integer.parseInt(args[2]) : 150);

            ORB orb = ORB.init(args, null);

            PdfService pdf = null;
            try {
                // 1) Essayer NamingService
                org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
                NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);
                org.omg.CORBA.Object refObj = ncRef.resolve_str("PdfService");
                pdf = PdfServiceHelper.narrow(refObj);
            } catch (Exception e) {
                // 2) Fallback via IOR file
                try {
                    java.nio.file.Path p = java.nio.file.Paths.get("c:/RONDOMNUMBER9/TP_Corba/ior.txt");
                    String txt = new String(java.nio.file.Files.readAllBytes(p), java.nio.charset.StandardCharsets.UTF_8);
                    String iorPdf = null;
                    for (String line : txt.split("\\r?\\n")) {
                        if (line.startsWith("IOR_PDF=")) {
                            iorPdf = line.substring("IOR_PDF=".length()).trim();
                        }
                    }
                    if (iorPdf == null) throw new RuntimeException("IOR_PDF absent dans ior.txt");

                    org.omg.CORBA.Object iorObj = orb.string_to_object(iorPdf);
                    pdf = PdfServiceHelper.narrow(iorObj);
                    System.out.println("[INFO] PdfService récupéré via ior.txt");
                } catch (Exception e2) {
                    throw new RuntimeException("Impossible de contacter NamingService et impossible de lire ior.txt", e2);
                }
            }




            String inB64 = readFileToBase64(inPath);

            // Conversion -> png base64 (mis dans nomFichierSortie)
            ResultatPdf img = pdf.conversionPdfEnImage(inB64, dpi);
            if (!img.succes) {
                System.out.println("Conversion KO: " + img.message);
                return;
            }
            Files.write(new File(outImgPath).toPath(), Base64.getDecoder().decode(img.nomFichierSortie));

            // Extraction texte
            ResultatPdf txt = pdf.extractionTexte(inB64);
            if (!txt.succes) {
                System.out.println("Extraction texte KO: " + txt.message);
                return;
            }

            System.out.println("=== TEXTE EXTRAIT ===");
            System.out.println(txt.message);

            System.out.println("OK. Image->" + outImgPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

