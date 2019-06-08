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
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.Security;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.imageio.ImageIO;
import javax.security.cert.CertificateParsingException;
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
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.io.IOCase;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.w3c.dom.Document;

import com.projekat.bezbednostDesktop.xml.XmlGenerator;
import com.projekat.bezbednostDesktop.xml.XmlGeneratorException;
import com.projekat.bezbednostDesktop.xml.XmlSigner;
import com.projekat.bezbednostDesktop.xml.XmlSignerException;

public class MainWindow extends JFrame {
	private static final long serialVersionUID = 1L;
	
	private JToolBar toolbar;
	private JButton buttonBrowseFolder;
	private JButton buttonBrowseJKS;
	private JButton buttonSignAndCompress;
	private JButton buttonUpload;
	
	private JFileChooser fileChooser;
	
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
		buttonUpload = new JButton("Upload");
		
		fileChooser = new JFileChooser();
		fileChooser.setAcceptAllFileFilterUsed(false);
		
		buttonBrowseFolder.addActionListener(new BrowseFolderActionListener());
		buttonBrowseJKS.addActionListener(new BrowseJKSActionListener());
		buttonSignAndCompress.addActionListener(new SignAndCompressActionListener());
		buttonUpload.addActionListener(new UploadActionListener());
		
		toolbar.add(buttonBrowseFolder);
		toolbar.add(buttonBrowseJKS);
		toolbar.add(buttonSignAndCompress);
		toolbar.add(buttonUpload);
		
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
					fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
					
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
			fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
			
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
			
			if(jksFilePath == null || jksFilePath.isEmpty()) {
				System.out.println("Java key store not set.");
				return;
			}
			
			FileOutputStream fos;
			ZipOutputStream zipOut = null;
			try {
				fos = new FileOutputStream("compressed.zip");
				zipOut = new ZipOutputStream(fos);
				    
				
				XmlGenerator xmlGenerator = new XmlGenerator();
				
				xmlGenerator.setEmail(email);
				
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
		            
		            xmlGenerator.addImage(file.getName(), (Integer)bytes.length, hashString);
				}
				
				
				// Sign xml document
				KeyStore keyStore = KeyStore.getInstance("JKS", "SUN");
				BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(jksFilePath));
				keyStore.load(inputStream, new char[0]);
				PrivateKey privateKey = (PrivateKey) keyStore.getKey(email, password.toCharArray());
				Certificate certificate = keyStore.getCertificate(email);
				
				if(certificate == null) {
					throw new CertificateParsingException("Could not load certificate by email: " + email);
				}
				
				Document document = xmlGenerator.generate();
				
				document = XmlSigner.SignDocument(document, privateKey, certificate);
				
				System.out.println("Signed document verification: " + XmlSigner.VerifyDocument(document));
				
				// write the content into xml file
				TransformerFactory transformerFactory = TransformerFactory.newInstance();
				Transformer transformer = transformerFactory.newTransformer();
				DOMSource source = new DOMSource(document);
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
				
				
			} catch (TransformerException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (XmlGeneratorException e) {
				e.printStackTrace();
			} catch (CertificateParsingException e) {
				e.printStackTrace();
			} catch (KeyStoreException e) {
				e.printStackTrace();
			} catch (NoSuchProviderException e) {
				e.printStackTrace();
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			} catch (CertificateException e) {
				e.printStackTrace();
			} catch (UnrecoverableKeyException e) {
				e.printStackTrace();
			} catch (XmlSignerException e) {
				e.printStackTrace();
			}
			finally {
				try {
					zipOut.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	// Upload
	class UploadActionListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent arg0) {
			try {
				
				HttpPost post = new HttpPost("https://localhost:8443/image/upload");
				//post.setHeader("", "");
				MultipartEntityBuilder builder = MultipartEntityBuilder.create();
				builder.addPart("file", new FileBody(new File("compressed.zip")));
				post.setEntity(builder.build());
				
				HttpResponse response = HttpClients.createDefault().execute(post);
				
				int httpStatus = response.getStatusLine().getStatusCode();
				String httpResponseMsg = EntityUtils.toString(response.getEntity(), "UTF-8");
				
			    System.out.println("HTTP " + httpStatus + " " + httpResponseMsg);
			    
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
