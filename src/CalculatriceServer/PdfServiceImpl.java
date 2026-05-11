import CalculatriceApp.*;
import org.omg.CORBA.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.interactive.action.PDActionRemoteGoTo;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.PDFTextStripperByArea;
import org.apache.pdfbox.text.TextPosition;
import org.apache.pdfbox.text.PDFTextStripper;

import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.pdmodel.encryption.StandardProtectionPolicy;

import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import org.apache.pdfbox.multipdf.PDFMergerUtility;

import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.PDPageContentStream;

/**
 * Implémentation CORBA du service PDF.
 * Entrées/sorties : base64 (String)
 */
public class PdfServiceImpl extends PdfServicePOA
{
    private static final Base64.Decoder DECODER = Base64.getDecoder();
    private static final Base64.Encoder ENCODER = Base64.getEncoder();

    private byte[] decodeBase64(String base64) throws IllegalArgumentException
    {
        if (base64 == null) return new byte[0];
        // accepte les éventuels préfixes "data:...;base64," si fournis
        String cleaned = base64;
        int comma = cleaned.indexOf(',');
        if (comma >= 0 && cleaned.substring(0, comma).toLowerCase().contains("base64"))
            cleaned = cleaned.substring(comma + 1);
        return DECODER.decode(cleaned);
    }

    private String encodeBase64(byte[] bytes)
    {
        return ENCODER.encodeToString(bytes);
    }

    private ResultatPdf ok(String message, String sortieNomFichier, String fichierBase64)
    {
        ResultatPdf r = new ResultatPdf();
        r.succes = true;
        r.message = message;
        // On met le base64 résultat dans nomFichierSortie pour que le client puisse le récupérer
        r.nomFichierSortie = fichierBase64;
        return r;
    }

    private ResultatPdf fail(String message)
    {
        ResultatPdf r = new ResultatPdf();
        r.succes = false;
        r.message = message;
        r.nomFichierSortie = null;
        return r;
    }

    private PDDocument loadDocument(String pdfBase64) throws Exception
    {
        byte[] data = decodeBase64(pdfBase64);
        return PDDocument.load(new ByteArrayInputStream(data));
    }

    private byte[] saveDocument(PDDocument doc) throws IOException
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        doc.save(baos);
        return baos.toByteArray();
    }

    @Override
    public ResultatPdf fusionPdf(String pdfBase64_1, String pdfBase64_2)
    {
        try (PDDocument doc1 = loadDocument(pdfBase64_1);
             PDDocument doc2 = loadDocument(pdfBase64_2))
        {
            // PDFBox 2.x : appendDocument accepte (destinationDoc, sourceDoc)
            PDFMergerUtility merger = new PDFMergerUtility();
            merger.appendDocument(doc1, doc2);

            byte[] out = saveDocument(doc1);
            return ok("Fusion OK", "fusionPdfBase64", encodeBase64(out));
        }
        catch (Exception e)
        {
            return fail("Fusion PDF KO : " + e.getMessage());
        }
    }

    @Override
    public ResultatPdf decoupagePdf(String pdfBase64, PlagePages plage)
    {
        try (PDDocument doc = loadDocument(pdfBase64))
        {
            int start = plage.debut;
            int end = plage.fin;
            if (start < 1 || end < start || end > doc.getNumberOfPages())
                return fail("Plage invalide");

            PDDocument outDoc = new PDDocument();
            for (int i = start; i <= end; i++)
            {
                PDPage page = doc.getPage(i - 1);
                outDoc.addPage(page);
            }

            byte[] out = saveDocument(outDoc);
            outDoc.close();
            return ok("Découpage OK", "decoupagePdfBase64", encodeBase64(out));
        }
        catch (Exception e)
        {
            return fail("Découpage PDF KO : " + e.getMessage());
        }
    }

    @Override
    public ResultatPdf extractionPage(String pdfBase64, PlagePages plage)
    {
        // On considère debut==fin
        try (PDDocument doc = loadDocument(pdfBase64))
        {
            int pageNum = plage.debut;
            if (pageNum < 1 || pageNum > doc.getNumberOfPages())
                return fail("Numéro de page invalide");

            PDDocument outDoc = new PDDocument();
            PDPage page = doc.getPage(pageNum - 1);
            outDoc.addPage(page);

            byte[] out = saveDocument(outDoc);
            outDoc.close();
            return ok("Extraction page OK", "extractionPageBase64", encodeBase64(out));
        }
        catch (Exception e)
        {
            return fail("Extraction page KO : " + e.getMessage());
        }
    }

    @Override
    public ResultatPdf suppressionPage(String pdfBase64, PlagePages plage)
    {
        try (PDDocument doc = loadDocument(pdfBase64))
        {
            int start = plage.debut;
            int end = plage.fin;
            if (start < 1 || end < start || end > doc.getNumberOfPages())
                return fail("Plage invalide");

            // remove en partant de la fin
            for (int i = end; i >= start; i--)
            {
                doc.removePage(i - 1);
            }

            byte[] out = saveDocument(doc);
            return ok("Suppression pages OK", "suppressionPageBase64", encodeBase64(out));
        }
        catch (Exception e)
        {
            return fail("Suppression pages KO : " + e.getMessage());
        }
    }

    @Override
    public ResultatPdf ajoutMotDePasse(String pdfBase64, String password)
    {
        try (PDDocument doc = loadDocument(pdfBase64))
        {
            AccessPermission ap = new AccessPermission();
            // lecture seule
            ap.setCanExtractContent(false);
            ap.setCanPrint(true);

            StandardProtectionPolicy spp = new StandardProtectionPolicy(
                password == null ? "" : password,
                password == null ? "" : password,
                ap
            );
            doc.protect(spp);

            byte[] out = saveDocument(doc);
            return ok("Ajout mot de passe OK", "ajoutMotDePasseBase64", encodeBase64(out));
        }
        catch (Exception e)
        {
            return fail("Ajout mot de passe KO : " + e.getMessage());
        }
    }

    @Override
    public ResultatPdf conversionPdfEnImage(String pdfBase64, int dpi)
    {
        try (PDDocument doc = loadDocument(pdfBase64))
        {
            if (doc.getNumberOfPages() < 1)
                return fail("PDF vide");

            int useDpi = (dpi <= 0 ? 150 : dpi);
            PDFRenderer renderer = new PDFRenderer(doc);
            BufferedImage bim = renderer.renderImageWithDPI(0, useDpi);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(bim, "png", baos);

            return ok("Conversion PDF->PNG OK", "image1.png_base64", encodeBase64(baos.toByteArray()));
        }
        catch (Exception e)
        {
            return fail("Conversion PDF->image KO : " + e.getMessage());
        }
    }

    @Override
    public ResultatPdf extractionTexte(String pdfBase64)
    {
        try (PDDocument doc = loadDocument(pdfBase64))
        {
            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setSortByPosition(true);
            String text = stripper.getText(doc);
            ResultatPdf r = new ResultatPdf();
            r.succes = true;
            r.message = (text != null ? text : "");
            r.nomFichierSortie = "";
            return r;
        }
        catch (Exception e)
        {
            return fail("Extraction texte KO : " + e.getMessage());
        }
    }

    @Override
    public ResultatPdf creationPdf(String texte)
    {
        try
        {
            PDDocument doc = new PDDocument();
            PDPage page = new PDPage();
            doc.addPage(page);

            PDPageContentStream contentStream = new PDPageContentStream(doc, page);
            contentStream.setFont(PDType1Font.HELVETICA, 12);
            contentStream.beginText();
            contentStream.newLineAtOffset(25, 750);
            contentStream.showText(texte != null ? texte : "");
            contentStream.endText();
            contentStream.close();

            byte[] out;
            try
            {
                out = saveDocument(doc);
            }
            finally
            {
                doc.close();
            }

            return ok("Création PDF OK", "creationPdfBase64", encodeBase64(out));
        }
        catch (Exception e)
        {
            return fail("Creation PDF KO : " + e.getMessage());
        }
    }
}

