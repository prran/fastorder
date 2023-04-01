package com.kindlesstory.www.module;

import javax.crypto.SecretKey;
import java.security.spec.KeySpec;
import com.kindlesstory.www.exception.DecryptException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.spec.PBEKeySpec;
import org.apache.commons.codec.binary.Hex;
import javax.crypto.SecretKeyFactory;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import java.security.spec.RSAPublicKeySpec;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.io.UnsupportedEncodingException;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.NoSuchAlgorithmException;
import java.security.InvalidKeyException;
import javax.crypto.Cipher;
import com.kindlesstory.www.data.vo.PublicKeyInformation;
import org.springframework.beans.factory.annotation.Autowired;
import java.security.KeyPair;
import com.kindlesstory.www.data.vo.global.Key;

public class Crypt
{
    @Autowired
    private KeyPair keypair;
    private PublicKeyInformation publicKeyInfo;
    
    public String decryptRsa(final Object target) {
        try {
            final Cipher cipher = Cipher.getInstance("RSA");
            final byte[] encryptedBytes = hexToByteArray(target.toString());
            cipher.init(2, keypair.getPrivate());
            final byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
            final String decryptedValue = new String(decryptedBytes, "utf-8");
            return decryptedValue;
        }
        catch (NullPointerException | InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException ex3) {
            return null;
        }
        catch (IllegalBlockSizeException | BadPaddingException | UnsupportedEncodingException ex4) {
            return null;
        }
    }
    
    private byte[] hexToByteArray(final String hex) {
        if (hex == null || hex.length() % 2 != 0) {
            return new byte[0];
        }
        final byte[] bytes = new byte[hex.length() / 2];
        for (int i = 0; i < hex.length(); i += 2) {
            final byte value = (byte)Integer.parseInt(hex.substring(i, i + 2), 16);
            bytes[(int)Math.floor(i / 2)] = value;
        }
        return bytes;
    }
    
    public String encryptSha256(final String target) {
        try {
            final MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(target.getBytes());
            return byteToHexForSha256(md.digest());
        }
        catch (NoSuchAlgorithmException e) {
            return null;
        }
    }
    
    private String byteToHexForSha256(final byte[] bytes) {
        final StringBuilder builder = new StringBuilder();
        for (final byte b : bytes) {
            builder.append(String.format("%02x", b));
        }
        return builder.toString();
    }
    
    public String encryptMD5(final String target) {
        try {
            final MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(target.getBytes());
            return byteToHexforMD5(md.digest());
        }
        catch (NoSuchAlgorithmException e) {
            return null;
        }
    }
    
    private String byteToHexforMD5(final byte[] bytes) {
        final StringBuilder builder = new StringBuilder();
        for (final byte b : bytes) {
            builder.append(String.format("%02x", b));
        }
        return builder.toString();
    }
    
    public PublicKeyInformation getPublicKey() {
        if (this.publicKeyInfo == null) {
            this.publicKeyInfo = new PublicKeyInformation();
            try {
                final PublicKey key = keypair.getPublic();
                final KeyFactory keyFactory = KeyFactory.getInstance("RSA");
                final RSAPublicKeySpec spec = keyFactory.getKeySpec(key, RSAPublicKeySpec.class);
                this.publicKeyInfo.setPublicKeyModulus(spec.getModulus().toString(16));
                this.publicKeyInfo.setPublicKeyExponent(spec.getPublicExponent().toString(16));
                this.publicKeyInfo.setPublicEncode(Base64.getEncoder().encodeToString(key.getEncoded()));
            }
            catch (InvalidKeySpecException ex) {}
            catch (NoSuchAlgorithmException ex2) {}
        }
        return this.publicKeyInfo;
    }
    
    public String decryptAes(final String encode, final String iv, final String salt, final String passPhrase) throws DecryptException {
        final int keySize = 128;
        final int iterationCount = 1000;
        try {
            final SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            final KeySpec spec = new PBEKeySpec(passPhrase.toCharArray(), Hex.decodeHex(salt.toCharArray()), iterationCount, keySize);
            final SecretKey key = new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");
            final Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(2, key, new IvParameterSpec(Hex.decodeHex(iv.toCharArray())));
            final byte[] decrypted = cipher.doFinal(org.apache.commons.codec.binary.Base64.decodeBase64(encode));
            return new String(decrypted, "UTF-8");
        }
        catch (Exception e) {
            throw new DecryptException();
        }
    }
    
    public String encryptSimpleAes(final String plaintext) {
        try {
            final Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            final SecretKeySpec keySpec = new SecretKeySpec(Key.getIv().getBytes(), "AES");
            final IvParameterSpec ivParamSpec = new IvParameterSpec(Key.getIv().getBytes());
            cipher.init(1, keySpec, ivParamSpec);
            final byte[] encrypted = cipher.doFinal(plaintext.getBytes("UTF-8"));
            return Base64.getEncoder().encodeToString(encrypted);
        }
        catch (Exception e) {
            return null;
        }
    }
    
    public String decryptSimpleAes(final String cipherText) throws DecryptException {
        try {
            final Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            final SecretKeySpec keySpec = new SecretKeySpec(Key.getIv().getBytes(), "AES");
            final IvParameterSpec ivParamSpec = new IvParameterSpec(Key.getIv().getBytes());
            cipher.init(2, keySpec, ivParamSpec);
            final byte[] decodedBytes = Base64.getDecoder().decode(cipherText);
            final byte[] decrypted = cipher.doFinal(decodedBytes);
            return new String(decrypted, "UTF-8");
        }
        catch (Exception e) {
            throw new DecryptException();
        }
    }
}