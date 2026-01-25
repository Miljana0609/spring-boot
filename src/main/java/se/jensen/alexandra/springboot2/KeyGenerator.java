package se.jensen.alexandra.springboot2;


/**
 * En klass som ansvarar för att generera ett RSA-nyckelpar (privat och offentlig nyckel).
 * <p>
 * Klassen använder Java's inbyggda `KeyPairGenerator` för att skapa ett RSA-nyckelpar med en nyckellängd på 2048 bitar.
 * Nycklarna kodas i Base64-format och kan användas för att kryptera och dekryptera data, eller för att skapa digitala signaturer.
 */
public class KeyGenerator {

//    public static void main(String[] args) throws Exception {
//        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
//        generator.initialize(2048);
//        KeyPair keyPair = generator.generateKeyPair();
//
//        String privateKey = Base64.getEncoder()
//                .encodeToString(keyPair.getPrivate().getEncoded());
//
//        String publicKey = Base64.getEncoder()
//                .encodeToString(keyPair.getPublic().getEncoded());
//
//        System.out.println("PRIVATE_KEY:");
//        System.out.println(privateKey);
//
//        System.out.println();
//        System.out.println("PUBLIC_KEY:");
//        System.out.println(publicKey);
//    }
}