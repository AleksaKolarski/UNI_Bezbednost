package com.projekat.bezbednostDesktop.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
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
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.io.IOCase;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.keys.KeyInfo;
import org.apache.xml.security.keys.keyresolver.implementations.RSAKeyValueResolver;
import org.apache.xml.security.keys.keyresolver.implementations.X509CertificateResolver;
import org.apache.xml.security.signature.XMLSignature;
import org.apache.xml.security.transforms.Transforms;
import org.apache.xml.security.utils.Constants;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;


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
	
	private String jksFilePath;
	
	public String email;
	public String password;
	
	static {
		Security.addProvider(new BouncyCastleProvider());
		org.apache.xml.security.Init.init();
	}
	
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
	
	// Browse certificate
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
				jksFilePath = fileChooser.getSelectedFile().getPath();
				
				LoginWindow loginWindow = new LoginWindow(MainWindow.this);
				loginWindow.setVisible(true);
			}
		}
	}
	
	// Sign and compress
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
		            
		            // image element
		            Element imageElement = doc.createElement("image");
		            imagesElement.appendChild(imageElement);
		            Attr imageNameAttr = doc.createAttribute("name");
		            imageNameAttr.setValue(file.getName());
		            imageElement.setAttributeNode(imageNameAttr);
		            Attr imageSizeAttr = doc.createAttribute("size");
		            imageSizeAttr.setValue(((Integer) bytes.length).toString());
		            imageElement.setAttributeNode(imageSizeAttr);
		            Attr imageHashAttr = doc.createAttribute("hash");
		            imageHashAttr.setValue(hashString);
		            imageElement.setAttributeNode(imageHashAttr);
				}
				
				
				// Sign xml document
				KeyStore keyStore = KeyStore.getInstance("JKS", "SUN");
				BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(jksFilePath));
				keyStore.load(inputStream, password.toCharArray());
				PrivateKey privateKey = (PrivateKey) keyStore.getKey(email, password.toCharArray());
				Certificate certificate = keyStore.getCertificate(email);
				
				doc = signDocument(doc, privateKey, certificate);
				
				System.out.println("Signed document verification: " + verifyDocument(doc));
				
				// write the content into xml file
				TransformerFactory transformerFactory = TransformerFactory.newInstance();
				Transformer transformer = transformerFactory.newTransformer();
				DOMSource source = new DOMSource(doc);
				//StreamResult result = new StreamResult(new FileOutputStream("test.xml")); // ako ocemo xml da ispisemo na disk a ne u zip direktno
				ByteArrayOutputStream bo = new ByteArrayOutputStream();
				StreamResult result = new StreamResult(bo);
				
				// pokvari se verifikacija zbog ovoga jer se potpise pre uvlacenja redova
				//transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
				//transformer.setOutputProperty(OutputKeys.INDENT, "yes");
				
				transformer.transform(source, result);
				
				byte[] xmlBytes = bo.toByteArray();
				zipOut.putNextEntry(new ZipEntry("contents.xml"));
				zipOut.write(xmlBytes, 0, xmlBytes.length);
				
				zipOut.close();
		        fos.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private Document signDocument(Document doc, PrivateKey privateKey, Certificate certificate) {
		try {
			Element rootElement = doc.getDocumentElement();
				
			XMLSignature xmlSignature;
			xmlSignature = new XMLSignature(doc, null, XMLSignature.ALGO_ID_SIGNATURE_RSA_SHA1);
			Transforms transforms = new Transforms(doc);
			transforms.addTransform(Transforms.TRANSFORM_ENVELOPED_SIGNATURE);
			transforms.addTransform(Transforms.TRANSFORM_C14N_WITH_COMMENTS);
				
			xmlSignature.addDocument("", transforms, Constants.ALGO_ID_DIGEST_SHA1);
			xmlSignature.addKeyInfo(certificate.getPublicKey());
			xmlSignature.addKeyInfo((X509Certificate) certificate);
				
			rootElement.appendChild(xmlSignature.getElement());
				
			xmlSignature.sign(privateKey);
			return doc;
		} catch (XMLSecurityException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private boolean verifyDocument(Document doc) {
		try {
			//Pronalazi se prvi Signature element 
			NodeList signatures = doc.getElementsByTagNameNS("http://www.w3.org/2000/09/xmldsig#", "Signature");
			Element signatureEl = (Element) signatures.item(0);
			
			//kreira se signature objekat od elementa
			XMLSignature signature = new XMLSignature(signatureEl, null);
			
			//preuzima se key info
			KeyInfo keyInfo = signature.getKeyInfo();
			
			//ako postoji
			if(keyInfo != null) {
				//registruju se resolver-i za javni kljuc i sertifikat
				keyInfo.registerInternalKeyResolver(new RSAKeyValueResolver());
			    keyInfo.registerInternalKeyResolver(new X509CertificateResolver());
			    
			    //ako sadrzi sertifikat
			    if(keyInfo.containsX509Data() && keyInfo.itemX509Data(0).containsCertificate()) { 
			        Certificate cert = keyInfo.itemX509Data(0).itemCertificate(0).getX509Certificate();
			        
			        //ako postoji sertifikat, provera potpisa
			        if(cert != null) 
			        	return signature.checkSignatureValue((X509Certificate) cert);
			        else {
			        	return false;
			        }
			    }
			    else {
		        	return false;
		        }
			}
			else {
	        	return false;
	        }
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}
