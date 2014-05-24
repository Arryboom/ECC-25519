package net.vrallev.android.ecc;

import com.github.dazoe.android.Ed25519;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import djb.Curve25519;

/**
 * @author Ralf Wondratschek
 */
@SuppressWarnings("UnusedDeclaration")
public class KeyHolder {

    protected static final MessageDigest MESSAGE_DIGEST_SHA_256;

    static {
        try {
            MESSAGE_DIGEST_SHA_256 = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            // ignore, won't happen
            throw new IllegalStateException(e);
        }
    }

    public static byte[] createPrivateKey(byte[] seed) {
        MESSAGE_DIGEST_SHA_256.reset();
        return MESSAGE_DIGEST_SHA_256.digest(seed);
    }

    protected final byte[] mPrivateKey;
    protected final byte[] mPublicKeyDiffieHellman;
    protected final byte[] mPublicKeySignature;

    public KeyHolder(byte[] privateKey) {
        if (privateKey == null || (privateKey.length != 32 && privateKey.length != 64)) {
            throw new IllegalArgumentException("private key must contain 32 or 64 bytes.");
        }

        privateKey = Arrays.copyOf(privateKey, privateKey.length);
        Curve25519.clamp(privateKey);

        mPrivateKey = privateKey;

        mPublicKeyDiffieHellman = new byte[32];
        Curve25519.keygen(mPublicKeyDiffieHellman, null, mPrivateKey);

        mPublicKeySignature = computePublicSignatureKey(mPrivateKey);
    }

    public KeyHolder(byte[] publicKeyDiffieHellman, byte[] publicKeySignature) {
        this(null, publicKeyDiffieHellman, publicKeySignature);
    }

    public KeyHolder(byte[] privateKey, byte[] publicKeyDiffieHellman, byte[] publicKeySignature) {
        mPrivateKey = privateKey;
        mPublicKeyDiffieHellman = publicKeyDiffieHellman;
        mPublicKeySignature = publicKeySignature;
    }

    protected byte[] computePublicSignatureKey(byte[] privateKey) {
        try {
            return Ed25519.PublicKeyFromPrivateKey(privateKey);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    public byte[] getPrivateKey() {
        return mPrivateKey;
    }

    public byte[] getPublicKeyDiffieHellman() {
        return mPublicKeyDiffieHellman;
    }

    public byte[] getPublicKeySignature() {
        return mPublicKeySignature;
    }
}