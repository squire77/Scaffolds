package scaffold.console.documents;

import javax.swing.text.*;
import java.io.*;

public class TextDocumentPrintWriter extends PrintWriter {
    public TextDocumentPrintWriter(TextDocumentViewer doc, JTextComponent textArea) {
        super(System.out);
        this.doc = doc;
        this.textArea = textArea;
    }

    @Override public void print( boolean b ) { doc.hasChanged(true); print( b ? "true" : "false" ); }
    @Override public void print( char c ) { doc.hasChanged(true); print( new String( new char[] { c } ) ); }
    @Override public void print( int i ) { doc.hasChanged(true); print( Integer.toString( i ) ); }
    @Override public void print( long l ) { doc.hasChanged(true); print( Long.toString( l ) ); }
    @Override public void print( float f ) { doc.hasChanged(true); print( Float.toString( f ) ); }
    @Override public void print( double d ) { doc.hasChanged(true); print( Double.toString( d ) ); }
    @Override public void print( char[] s ) { doc.hasChanged(true); print( new String( s ) ); }
    @Override public void print( String s ) { doc.hasChanged(true); textArea.setText( textArea.getText() + s ); } //textArea.append( s ); }
    @Override public void print( Object obj ) { doc.hasChanged(true); textArea.setText( textArea.getText() + String.valueOf(obj) ); } //textArea.append( String.valueOf( obj ) ); }
    @Override public void println() { doc.hasChanged(true); textArea.setText( textArea.getText() + "\n" ); } //textArea.append( "\n" ); }
    @Override public void println( boolean x ) { doc.hasChanged(true); print( x ); println(); }
    @Override public void println( char x ) { doc.hasChanged(true); print( x ); println(); }
    @Override public void println( int x ) { doc.hasChanged(true); print( x ); println(); }
    @Override public void println( long x ) { doc.hasChanged(true); print( x ); println(); }
    @Override public void println( float x ) { doc.hasChanged(true); print( x ); println(); }
    @Override public void println( double x ) { doc.hasChanged(true); print( x ); println(); }
    @Override public void println( char[] x ) { doc.hasChanged(true); print( x ); println(); }
    @Override public void println( String x ) { doc.hasChanged(true); print( x ); println(); }
    @Override public void println( Object x ) { doc.hasChanged(true); print( x ); println(); }
    @Override public void write( int c ) {
        doc.hasChanged(true);
        ByteArrayOutputStream out2 = new ByteArrayOutputStream();
        out2.write( c );
        write( out2.toString() );
    }
    @Override public void write( char[] buf, int off, int len ) { doc.hasChanged(true); write( new String( buf, off, len ) ); }
    @Override public void write( char[] buf ) { doc.hasChanged(true); write( new String( buf ) ); }
    @Override public void write( String s, int off, int len ) { doc.hasChanged(true); write( s.substring( off, len ) ); }
    @Override public void write( String s ) { doc.hasChanged(true); print( s ); }

    private TextDocumentViewer    doc;
    private JTextComponent  textArea;
}
