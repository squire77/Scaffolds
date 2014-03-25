package scaffold.fileexplorer;

import java.io.*;

public class FileUtility
{
    private static String extension;
    private static String startChars;
    
    public static void createDirectory(String path) throws IOException {
        int index1 = path.indexOf("\\");
        int index2 = path.indexOf("/");
        
        int index = 0;
        File dir;
        
        while (index1 > -1 || index2 > -1) {
            if (index1 > -1) {              
                if (index2 < 0 || index1 < index2) {
                    dir = new File(path.substring(0,index1));
                    if (!dir.exists()) {
                        dir.mkdir();
                    }
                    index = index1 + 2; // +2 for double backslash
                    if (index > path.length() - 1) {
                        break;
                    }
                    
                    index1 = path.indexOf("\\", index);
                    continue;
                }
            }
            
            dir = new File(path.substring(0,index2));
            if (!dir.exists()) {
                dir.mkdir();
            }
            index = index2 + 1;
            if (index > path.length() - 1) {
                break;
            }
            
            
            index2 = path.indexOf("/", index);
            continue;            
        }
    }
    
    public static String extractFileName(String path) {
        int index = path.lastIndexOf("\\");
        
        if (index > -1) {
            if (index < path.length() - 1) {
                return path.substring(index+1);
            } else {
                return "";
            }
        }
        
        index = path.lastIndexOf("/");
        
        if (index > -1) {
            if (index < path.length() - 1) {
                return path.substring(index+1);
            } else {
                return "";
            }
        }
        
        return path;
    }
    
    public static String extractDir(String path) {
        int index1 = path.lastIndexOf("\\");
        int index2 = path.lastIndexOf("/");
        int index = (index1 > index2) ? index1 : index2;
        
        if (index > 0) {
            return path.substring(0,index+1);
        } else {
            return "";
        }
    }
    
    public static void deleteFilesInDirectory(File dir, boolean recurse) {       
        File[] files = dir.listFiles();
        
        for (File f: files) {            
            if (f.isDirectory()) {
                if (recurse) {
                    deleteFilesInDirectory(f, recurse);
                    f.delete();
                }
            } else {
                f.delete();
            }
        }                
    }
    
    //extension must start with '.'
    public static File[] getFilesByExt( String dirName, String ext){
    	File dir = new File(dirName);
        extension = ext;
        
    	return dir.listFiles(new FilenameFilter() { 
            @Override
    	    public boolean accept(File dir, String fileName) {
    	        return fileName.endsWith(extension);
            }
    	});
    }
    
    public static File[] getFilesStartingWith( String dirName, String startsWith){
    	File dir = new File(dirName);
        startChars = startsWith;
        
    	return dir.listFiles(new FilenameFilter() { 
            @Override
    	    public boolean accept(File dir, String fileName) {
    	        return fileName.startsWith(startChars);
            }
    	});
    }

    public static void writeFile(String fileName, String textToSave) throws IOException {
        if (fileName == null)
            throw new IOException( "filename is null");
        if (textToSave == null)
            throw new IOException("textToSave is null");

        FileWriter fw     = new FileWriter(fileName);
        BufferedWriter bw = new BufferedWriter(fw);

        fw.write( textToSave );

        bw.close();
        fw.close();
    }
    
    public static String readFile(String fileName) throws IOException {
        if (fileName == null)
            throw new IOException( "filename is null");

        FileReader fr = new FileReader(fileName);
        BufferedReader br = new BufferedReader(fr);
        StringBuilder buffer = new StringBuilder();

        String nextLine;
        while ((nextLine = br.readLine()) != null)
            buffer.append(nextLine).append("\n");

        //Remove final newline
        if (buffer.length() > 0 && buffer.charAt(buffer.length()-1) == '\n')
            buffer.deleteCharAt( buffer.length() - 1 );

        br.close();
        fr.close();

        return buffer.toString();
    }
}
