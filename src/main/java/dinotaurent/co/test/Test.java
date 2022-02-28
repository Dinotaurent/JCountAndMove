package dinotaurent.co.test;

import dinotaurent.co.domain.*;
import org.apache.log4j.*;

/**
 *
 * @author dandazme
 */
public class Test {
    private static Logger log = Logger.getLogger(Test.class);
    
    public static void main(String[] args) {
        var  vFolder = new FolderImpl();
        vFolder.contar();
    }
    
}
