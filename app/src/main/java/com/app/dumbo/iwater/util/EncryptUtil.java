package com.app.dumbo.iwater.util;

import com.app.dumbo.iwater.constant.SecretKey;

import org.bouncycastle.util.encoders.Base64;

import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

/**
 * --加密工具类--
 *
 * Created by dumbo on 2018/5/19
 **/

public class EncryptUtil {
    private static String EncryptByMD5(String s) {
        char hexDigits[] = { '0', '1', '2', '3', '4','5', '6', '7',
                           '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
        try {
            byte[] btInput = s.getBytes();
            //获得MD5摘要算法的 MessageDigest 对象
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            //使用指定的字节更新摘要
            mdInst.update(btInput);
            //获得密文
            byte[] md = mdInst.digest();
            //把密文转换成十六进制的字符串形式
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 加密
     * @param srcData 待加密的数据
     * @return 以Base64编码的加密后的数据String
     */
    private static String encryptByRSA(String srcData){
        String encryptedStr=null;

        //将字符串形式的公钥转换为公钥对象
        PublicKey publicKey = null;
        byte[] keyBytes = Base64.decode(SecretKey.RSA_PUBLIC_KEY);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            publicKey = keyFactory.generatePublic(keySpec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
        }

        //加密
        try {
            //获取Cipher实例，
            Cipher cipher = Cipher.getInstance("RSA",
                    new org.bouncycastle.jce.provider.BouncyCastleProvider());
            //初始化Cipher，mode指定是加密还是解密，key为公钥或私钥
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            //处理数据
            byte[] bytes=cipher.doFinal(srcData.getBytes());
            encryptedStr=Base64.toBase64String(bytes);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | BadPaddingException
                | IllegalBlockSizeException | InvalidKeyException e) {
            e.printStackTrace();
        }
        return encryptedStr;
    }

    /**
     * 加密
     * @param password 待加密的数据
     * @return 以Base64编码的加密后的数据String
     */
    public static String encrypt(String password){
        //使用MD5加密密码password
        String md5Pasw = EncryptByMD5(password);
        //使用RSA加密md5Pasw(以Base64编码的RSA加密的密码)
        return encryptByRSA(md5Pasw);
    }
}
