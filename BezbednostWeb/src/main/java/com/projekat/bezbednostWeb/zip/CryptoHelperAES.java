package com.projekat.bezbednostWeb.zip;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.xml.security.utils.JavaUtils;
import org.bouncycastle.crypto.CryptoException;
import org.springframework.stereotype.Component;

@Component
public class CryptoHelperAES {

	private Cipher aesCipherEnc;
	private Cipher aesCipherDec;
	
	private SecretKey secretKey;
	private IvParameterSpec ivParameterSpec;
	
	
	public CryptoHelperAES() {
		try {
			secretKey = new SecretKeySpec(JavaUtils.getBytesFromFile("./data/session.key"), "AES");
			ivParameterSpec = new IvParameterSpec(JavaUtils.getBytesFromFile("./data/session.iv"));
			
			aesCipherEnc = Cipher.getInstance("AES/CBC/PKCS5Padding");
			aesCipherEnc.init(Cipher.ENCRYPT_MODE, secretKey, ivParameterSpec);
			
			aesCipherDec = Cipher.getInstance("AES/CBC/PKCS5Padding");
			aesCipherDec.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec);
		} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | InvalidAlgorithmParameterException | IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void generateKey() {
		try {
			KeyGenerator keyGenerator = KeyGenerator.getInstance("AES"); 
			byte[] iv = new byte[16];
            SecureRandom secureRandom = new SecureRandom();
            secureRandom.nextBytes(iv);
			
			JavaUtils.writeBytesToFilename("./data/session.key", keyGenerator.generateKey().getEncoded());
            JavaUtils.writeBytesToFilename("./data/session.iv", iv.clone());
        } catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}
	
	public byte[] encrypt(byte[] data) throws CryptoException {
		try {
			return aesCipherEnc.doFinal(data);
		} catch (IllegalBlockSizeException | BadPaddingException e) {
			e.printStackTrace();
			throw new CryptoException("Could not encrypt data.");
		}
	}
	
	public byte[] decrypt(byte[] data) throws CryptoException {
		try {
			return aesCipherDec.doFinal(data);
		} catch (IllegalBlockSizeException | BadPaddingException e) {
			e.printStackTrace();
			throw new CryptoException("Could not decrypt data.");
		}
	}
}
