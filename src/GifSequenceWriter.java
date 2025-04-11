import javax.imageio.*;
import javax.imageio.metadata.*;
import javax.imageio.stream.*;
import java.awt.image.*;
import java.io.*;
import java.util.Iterator;

/**
 * GifSequenceWriter.java
 * 
 * Creates a GIF file from a sequence of BufferedImages
 * 
 * Original code by Elliot Kroo (elliot[at]kroo[dot]net)
 * Modified for this application
 */
public class GifSequenceWriter {
    protected ImageWriter gifWriter;
    protected ImageWriteParam imageWriteParam;
    protected IIOMetadata imageMetaData;
    
    /**
     * Creates a new GifSequenceWriter
     * 
     * @param outputStream the ImageOutputStream to be written to
     * @param imageType one of the imageTypes specified in BufferedImage
     * @param timeBetweenFramesMS the time between frames in milliseconds
     * @param loopContinuously whether the GIF should loop repeatedly
     * @throws IIOException if no GIF ImageWriters are found
     */
    public GifSequenceWriter(
            ImageOutputStream outputStream,
            int imageType,
            int timeBetweenFramesMS,
            boolean loopContinuously) throws IIOException, IOException {
        
        // Get GIF writer
        gifWriter = getWriter();
        imageWriteParam = gifWriter.getDefaultWriteParam();
        
        // Create metadata
        ImageTypeSpecifier imageTypeSpecifier = 
                ImageTypeSpecifier.createFromBufferedImageType(imageType);
        
        imageMetaData = gifWriter.getDefaultImageMetadata(imageTypeSpecifier, imageWriteParam);
        
        // Configure GIF metadata (loop count, timing)
        configureRootMetadata(timeBetweenFramesMS, loopContinuously);
        
        // Initialize writer
        gifWriter.setOutput(outputStream);
        gifWriter.prepareWriteSequence(null);
    }
    
    private ImageWriter getWriter() throws IIOException {
        Iterator<ImageWriter> iter = ImageIO.getImageWritersBySuffix("gif");
        if (!iter.hasNext()) {
            throw new IIOException("No GIF Image Writers Found");
        }
        return iter.next();
    }
    
    private void configureRootMetadata(int timeBetweenFramesMS, boolean loopContinuously) throws IIOException {
        String metaFormatName = imageMetaData.getNativeMetadataFormatName();
        IIOMetadataNode root = (IIOMetadataNode) imageMetaData.getAsTree(metaFormatName);
        
        // Get Graphics Control Extension node
        IIOMetadataNode graphicsControlExtensionNode = getNode(root, "GraphicControlExtension");
        graphicsControlExtensionNode.setAttribute("disposalMethod", "none");
        graphicsControlExtensionNode.setAttribute("userInputFlag", "FALSE");
        graphicsControlExtensionNode.setAttribute("transparentColorFlag", "FALSE");
        graphicsControlExtensionNode.setAttribute("delayTime", Integer.toString(timeBetweenFramesMS / 10)); // In 1/100 sec
        graphicsControlExtensionNode.setAttribute("transparentColorIndex", "0");
        
        // Set loop count if requested
        if (loopContinuously) {
            IIOMetadataNode applicationExtensions = getNode(root, "ApplicationExtensions");
            IIOMetadataNode applicationExtension = new IIOMetadataNode("ApplicationExtension");
            
            applicationExtension.setAttribute("applicationID", "NETSCAPE");
            applicationExtension.setAttribute("authenticationCode", "2.0");
            
            // Loop continuously (0 means forever)
            byte[] loopBytes = new byte[] { 0x1, 0x0, 0x0 };
            applicationExtension.setUserObject(loopBytes);
            applicationExtensions.appendChild(applicationExtension);
        }
        
        try {
            imageMetaData.setFromTree(metaFormatName, root);
        } catch (IIOInvalidTreeException e) {
            throw new IIOException("Invalid metadata tree", e);
        }
    }
    
    private static IIOMetadataNode getNode(IIOMetadataNode rootNode, String nodeName) {
        for (int i = 0; i < rootNode.getLength(); i++) {
            if (rootNode.item(i).getNodeName().equalsIgnoreCase(nodeName)) {
                return (IIOMetadataNode) rootNode.item(i);
            }
        }
        
        // Node doesn't exist, create and add it
        IIOMetadataNode node = new IIOMetadataNode(nodeName);
        rootNode.appendChild(node);
        return node;
    }
    
    /**
     * Writes a frame to the GIF sequence
     * @param img the image to write
     * @throws IOException if errors occur during writing
     */
    public void writeToSequence(RenderedImage img) throws IOException {
        gifWriter.writeToSequence(
                new IIOImage(img, null, imageMetaData),
                imageWriteParam
        );
    }
    
    /**
     * Closes the GIF writer
     * @throws IOException if an error occurs during closing
     */
    public void close() throws IOException {
        gifWriter.endWriteSequence();
    }
}