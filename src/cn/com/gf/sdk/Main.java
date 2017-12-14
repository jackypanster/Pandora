package cn.com.gf.sdk;

class Main {
    public static void main(String[] args) {
        try {
            // initialize the SDK
            Pandora instance = Pandora.getInstance();
            // encode the given text
            String result = instance.encode(InfoType.PHONE, "13751761288");

            // decode the given cipher
            System.out.println(instance.decode(result));

        } catch (PandoraException e) {

            // your error handling
            System.out.println(e.getMessage() + ", " + e.getCause());
        }
    }
}
