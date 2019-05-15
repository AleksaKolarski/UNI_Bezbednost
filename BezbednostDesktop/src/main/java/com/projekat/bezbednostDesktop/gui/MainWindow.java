package com.projekat.bezbednostDesktop.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.SwingWorker;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.io.IOCase;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.projekat.bezbednostDesktop.model.ImageMetadata;


public class MainWindow extends JFrame {
	private static final long serialVersionUID = 1L;
	
	private JToolBar toolbar;
	private JButton buttonBrowseFolder;
	private JButton buttonBrowseJKS;
	private JButton buttonSignAndCompress;
	
	JFileChooser fileChooser;
	
	private JScrollPane scroll;
	private JPanel panel;
	
	private List<File> files;
	
	public MainWindow() {
		
		setTitle("Informaciona Bezbednost");
		setSize(new Dimension(600, 600));
		setMinimumSize(new Dimension(600, 600));
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		toolbar = new JToolBar();
		toolbar.setFloatable(false);
		
		buttonBrowseFolder = new JButton("Browse folder");
		buttonBrowseJKS = new JButton("Browse JKS");
		buttonSignAndCompress = new JButton("Sign and compress");
		
		fileChooser = new JFileChooser();
		fileChooser.setAcceptAllFileFilterUsed(false);
		
		buttonBrowseFolder.addActionListener(new BrowseFolderActionListener());
		buttonBrowseJKS.addActionListener(new BrowseJKSActionListener());
		buttonSignAndCompress.addActionListener(new SignAndCompressActionListener());
		
		toolbar.add(buttonBrowseFolder);
		toolbar.add(buttonBrowseJKS);
		toolbar.add(buttonSignAndCompress);
		
		add(toolbar, BorderLayout.NORTH);
		
		panel = new JPanel(new WrapLayout(WrapLayout.LEFT));
		scroll = new JScrollPane(panel);
		scroll.getVerticalScrollBar().setUnitIncrement(7);
		add(scroll, BorderLayout.CENTER);
		
		files = new ArrayList<File>();
	}
	
	
	// Browse folder
	class BrowseFolderActionListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent event) {
			
			SwingWorker<List<File>, Void> worker = new SwingWorker<List<File>, Void>() {
				@Override
				public List<File> doInBackground() {
					
					files = new ArrayList<File>();
					panel.removeAll();
					panel.revalidate();
					panel.repaint();

					fileChooser.setDialogTitle("Choose picture folder");
					fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
					fileChooser.resetChoosableFileFilters();
					fileChooser.setCurrentDirectory(new java.io.File("."));
					
					List<File> filesTMP = new ArrayList<>();
					
					if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
						System.out.println("Folder: " + fileChooser.getSelectedFile());
						
						FileFilter fileFilter = new WildcardFileFilter(new String[]{"*.JPG", "*.PNG"}, IOCase.INSENSITIVE);
						File[] listOfFiles = fileChooser.getSelectedFile().listFiles(fileFilter);
						
						for(File file: listOfFiles) {
							if(file.isFile()) {
								System.out.println(file.getAbsolutePath());
								try {
									filesTMP.add(file);
									
									BufferedImage bi = ImageIO.read(file);
								    ImageIcon imageIcon = new ImageIcon(bi.getScaledInstance(128, -1, Image.SCALE_SMOOTH));
								    JLabel imageLabel = new JLabel(imageIcon);
								    panel.add(imageLabel);
								    panel.revalidate();
								    
								} catch (IOException e) {
									e.printStackTrace();
								}
							}
						}
					}
					return filesTMP;
				}
				@Override
				public void done() {
					try {
						files = get();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			};
			worker.execute();
		}
	}
	
	class BrowseJKSActionListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent event) {
			
			fileChooser.setDialogTitle("Choose picture folder");
			fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			fileChooser.resetChoosableFileFilters();
			fileChooser.setFileFilter(new FileNameExtensionFilter("Java key store files", "jks"));
			fileChooser.setCurrentDirectory(new java.io.File("."));
			
			if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
				System.out.println("Sertifikat: " + fileChooser.getSelectedFile());
			}
		}
	}
	
	class SignAndCompressActionListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent event) {
			System.out.println("sign and compress");
			
			try {
				FileOutputStream fos = new FileOutputStream("compressed.zip");
				ZipOutputStream zipOut = new ZipOutputStream(fos);
				
				DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
				Document doc = dBuilder.newDocument();
				         
				// root element
				Element rootElement = doc.createElement("RootElement");
				doc.appendChild(rootElement);
				
				// username element
				Element usernameElement = doc.createElement("username");
				rootElement.appendChild(usernameElement);
				usernameElement.appendChild(doc.createTextNode("test username"));
				
				// images element
				Element imagesElement = doc.createElement("images");
				rootElement.appendChild(imagesElement);
				
				// date element
				Element dateElement = doc.createElement("date");
				rootElement.appendChild(dateElement);
				dateElement.appendChild(doc.createTextNode(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())));
				
				for(File file: files) {
					
					// ucitavamo fajl u niz bajtova
					FileInputStream fis = new FileInputStream(file);
					byte[] bytes = IOUtils.toByteArray(fis);
					fis.close();
					
					// racunamo hash i enkodujemo u base64
					MessageDigest sha = MessageDigest.getInstance("SHA-256");
					byte[] hashBytes = sha.digest(bytes);
					String hashString = Base64.getEncoder().encodeToString(hashBytes);
					
					// zipujemo fajl
		            ZipEntry zipEntry = new ZipEntry(file.getName());
		            zipOut.putNextEntry(zipEntry);
		            zipOut.write(bytes, 0, bytes.length);		            
		            
		            // grupisemo podatke koje prosledjujemo generatoru xml-a
		            ImageMetadata im = new ImageMetadata(file.getName(), bytes.length, hashString);
		            System.out.println(im.getName() + " " + im.getSize() + " " + im.getHash());
		            
		            // image element
		            Element imageElement = doc.createElement("image");
		            imagesElement.appendChild(imageElement);
		            Attr imageNameAttr = doc.createAttribute("name");
		            imageNameAttr.setValue(im.getName());
		            imageElement.setAttributeNode(imageNameAttr);
		            Attr imageSizeAttr = doc.createAttribute("size");
		            imageSizeAttr.setValue(im.getSize().toString());
		            imageElement.setAttributeNode(imageSizeAttr);
		            Attr imageHashAttr = doc.createAttribute("hash");
		            imageHashAttr.setValue(im.getHash());
		            imageElement.setAttributeNode(imageHashAttr);
				}
				
				// write the content into xml file
				TransformerFactory transformerFactory = TransformerFactory.newInstance();
				Transformer transformer = transformerFactory.newTransformer();
				DOMSource source = new DOMSource(doc);
				StreamResult result = new StreamResult(new FileOutputStream("test.xml"));
				transformer.transform(source, result);
				
				byte[] xmlBytes = IOUtils.toByteArray(new FileInputStream("test.xml"));
				zipOut.putNextEntry(new ZipEntry("contents.xml"));
				zipOut.write(xmlBytes, 0, xmlBytes.length);
				
				zipOut.close();
		        fos.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			} catch (TransformerConfigurationException e) {
				e.printStackTrace();
			} catch (TransformerException e) {
				e.printStackTrace();
			} catch (ParserConfigurationException e) {
				e.printStackTrace();
			}
		}
	}
}
