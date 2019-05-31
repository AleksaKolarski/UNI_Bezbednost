package com.projekat.bezbednostWeb.certificate;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;

public class KeyStoreUtil {

	public static KeyStore read(String file, char[] storePassword) {
		try (BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(file))) {
			
			KeyStore keyStore = KeyStore.getInstance("JKS", "SUN");
			keyStore.load(bufferedInputStream, storePassword);
			
			return keyStore;
			
		} catch (KeyStoreException e) {
			e.printStackTrace();
		} catch (NoSuchProviderException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (CertificateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void write(String email, String password, Certificate certificate, PrivateKey privateKey) {
		try (FileOutputStream fOutputStream = new FileOutputStream("data/" + email + ".jks")) {
			
			KeyStore keyStore = KeyStore.getInstance("JKS");
			keyStore.load(null, password.toCharArray());
			keyStore.setKeyEntry(email, privateKey, password.toCharArray(), new Certificate[] {certificate});
			keyStore.store(fOutputStream, new char[0]);
			
		} catch (KeyStoreException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (CertificateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
