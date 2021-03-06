package se.cambio.cds.util;

import se.cambio.openehr.util.ExceptionHandler;
import se.cambio.openehr.util.exceptions.InternalErrorException;
import se.cambio.openehr.util.exceptions.MissingConfigurationParameterException;
import se.cambio.openehr.util.misc.OpenEHRConfigurationParametersManager;

import java.io.*;

/**
 * User: Iago.Corbal
 * Date: 2013-11-04
 * Time: 18:46
 */
public class LocalCM {

    private static String AUTO_SAVE_FILE_CM_PREFIX = "auto.cm";

    public static void loadLocalCM() throws InternalErrorException {
        String hostname = null;
        try{
            hostname = OpenEHRConfigurationParametersManager.getParameter(OpenEHRConfigurationParametersManager.OPENEHR_SERVER_HOST);
        } catch (MissingConfigurationParameterException e) {
        }
        if (hostname==null){
            hostname = "local";
        }
        String fileName = AUTO_SAVE_FILE_CM_PREFIX+"."+hostname+".zip";
        InputStream is = null;
        try{
            is = LocalCM.class.getClassLoader().getResourceAsStream(fileName);
            if (is==null){
                File file = new File(fileName);
                if (file.exists()){
                    try {
                        is = new FileInputStream(file);
                    } catch (FileNotFoundException e) {
                        ExceptionHandler.handle(e);
                    }
                }
            }
            if (is!=null){
                try {
                    CMImportExportManager.importCM(is);
                } catch (IOException e) {
                    throw new InternalErrorException(e);
                }
            }
        }finally {
            if (is!=null){
                try {
                    is.close();
                } catch (IOException e) {
                    ExceptionHandler.handle(e);
                }
            }

        }
    }

    public static void saveLocalCM() throws InternalErrorException {
        String hostname = null;
        try{
            hostname = OpenEHRConfigurationParametersManager.getParameter(OpenEHRConfigurationParametersManager.OPENEHR_SERVER_HOST);
        } catch (MissingConfigurationParameterException e) {
        }
        try {
            if (hostname==null){
                hostname = "local";
            }
            File file = new File(AUTO_SAVE_FILE_CM_PREFIX+"."+hostname+".zip");
            CMImportExportManager.exportCurrentCM(file, true);
        } catch (IOException e) {
            throw new InternalErrorException(e);
        }
    }
}
