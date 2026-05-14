package com.babacar.pdfapi;

import CalculatriceApp.PdfService;
import CalculatriceApp.PdfServiceHelper;
import org.omg.CORBA.ORB;
import org.omg.CORBA.ORBPackage.InvalidName;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.CosNaming.NamingContextPackage.CannotProceed;
import org.omg.CosNaming.NamingContextPackage.NotFound;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Properties;

@Service
public class CorbaService {

    private static final Logger logger = LoggerFactory.getLogger(CorbaService.class);
    private PdfService pdfService;

    public CorbaService() {
        // Delayed CORBA initialization: do not connect during Spring context startup.
    }

    private synchronized void initCorba() {
        if (pdfService != null) {
            return;
        }

        try {
            Properties props = new Properties();
            String host = System.getenv().getOrDefault("ORB_INITIAL_HOST", "localhost");
            String port = System.getenv().getOrDefault("ORB_INITIAL_PORT", "1050");
            props.put("org.omg.CORBA.ORBInitialHost", host);
            props.put("org.omg.CORBA.ORBInitialPort", port);
            ORB orb = ORB.init(new String[0], props);

            org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
            NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);

            pdfService = PdfServiceHelper.narrow(ncRef.resolve_str("PdfService"));
            logger.info("CORBA PdfService initialized using {}:{}", host, port);
        } catch (InvalidName | CannotProceed | org.omg.CosNaming.NamingContextPackage.InvalidName | NotFound e) {
            logger.error("Failed to initialize CORBA PdfService", e);
        }
    }

    public PdfService getPdfService() {
        if (pdfService == null) {
            initCorba();
        }
        if (pdfService == null) {
            throw new IllegalStateException("CORBA PdfService is not available. Check ORB_INITIAL_HOST/ORB_INITIAL_PORT and NameService availability.");
        }
        return pdfService;
    }
}
