package com.babacar.pdfapi;

import CalculatriceApp.PdfService;
import CalculatriceApp.PdfServiceHelper;
import org.omg.CORBA.ORB;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.Properties;

@Service
@Lazy // 1. Empêche Spring de créer le bean au démarrage si non utilisé
public class CorbaService {

    private static final Logger logger = LoggerFactory.getLogger(CorbaService.class);
    private volatile PdfService pdfService; // 2. 'volatile' pour la sécurité des threads

    public CorbaService() {
        // Constructeur vide pour respecter la logique de démarrage léger
    }

    private synchronized void initCorba() {
        // Double vérification (Thread-safety)
        if (pdfService != null) {
            return;
        }

        try {
            Properties props = new Properties();
            // Récupération des variables d'environnement pour éviter le 'localhost' figé en prod
            String host = System.getenv().getOrDefault("ORB_INITIAL_HOST", "localhost");
            String port = System.getenv().getOrDefault("ORB_INITIAL_PORT", "1050");
            
            props.put("org.omg.CORBA.ORBInitialHost", host);
            props.put("org.omg.CORBA.ORBInitialPort", port);
            
            // Initialisation de l'ORB
            ORB orb = ORB.init(new String[0], props);

            logger.info("Connecting to NameService at {}:{}", host, port);
            org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
            NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);

            // Résolution du service
            pdfService = PdfServiceHelper.narrow(ncRef.resolve_str("PdfService"));
            logger.info("CORBA PdfService successfully initialized");
            
        } catch (Exception e) {
            // 3. Capture générique pour éviter que l'app ne crash à cause d'une exception CORBA spécifique
            logger.error("Failed to initialize CORBA PdfService: {}", e.getMessage());
        }
    }

    public PdfService getPdfService() {
        if (pdfService == null) {
            initCorba();
        }
        if (pdfService == null) {
            throw new IllegalStateException("Le service CORBA n'est pas disponible. Vérifiez que le serveur CORBA tourne sur " 
                    + System.getenv().getOrDefault("ORB_INITIAL_HOST", "localhost"));
        }
        return pdfService;
    }
}