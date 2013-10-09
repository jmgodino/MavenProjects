package org.codehaus.mojo.xsltc;

import java.io.File;
import java.net.MalformedURLException;
import java.util.Vector;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import com.sun.org.apache.xalan.internal.xsltc.compiler.*;

/**
 * Compila hojas de estilo
 * @goal xsltc
 */
public class XsltcMojo extends AbstractMojo
{

    /**
     * XSLT stylesheets to be compiled.
     *
     * @parameter
     * @required
     */
    private File[] stylesheets;

    /**
     * Package name for compiled translets.
     *
     * @parameter
     */
    private String packageName;

    /**
     * Directory in which to place compiled translets.
     *
     * @parameter expression="${project.build.outputDirectory}"
     * @required
     */
    private File outputDirectory;

    /**
     * @see org.apache.maven.plugin.Mojo#execute()
     */
    public void execute() throws MojoExecutionException, MojoFailureException
    {
        XSLTC xsltc = new XSLTC(true);
        xsltc.init();
        xsltc.setDebug( true );
        xsltc.setPackageName( packageName );
        xsltc.setDestDirectory( outputDirectory.getAbsolutePath() );
        Log log = getLog();
        Vector v = new Vector();
        for (int i=0; i<stylesheets.length; i++)
        {
            File stylesheet = (File) stylesheets[i];
            log.info( "Adding " + stylesheet + " to compilation vector");
            try
            {
                v.add(stylesheet.toURL());
            }
            catch ( MalformedURLException e )
            {
                throw new MojoExecutionException( "Could not convert file to URL", e );
            }
        }
        boolean success = xsltc.compile( v );
        for (int j=0; j<xsltc.getWarnings().size(); j++)
        {
            Object warning = xsltc.getWarnings().get(j);
            log.warn( warning.toString() );
        }
        for (int j=0; j<xsltc.getErrors().size(); j++)
        {
            Object error = xsltc.getErrors().get(j);
            log.error( error.toString() );
        }
        if ( !success )
        {
            throw new MojoFailureException( "There were XSLTC errors" );
        }
    }

}
