package cn.com.gf.sdk;

class Main {
    public static void main(String[] args) {
        try {
            // initialize the SDK
            Pandora instance = Pandora.getInstance("10.2.130.155:32030", "demo", false);
            // encode the given text
            String result = instance.encode(InfoType.PHONE, "13751761288");

            // decode the given cipher
            instance.decode(result);

        } catch (PandoraException e) {

            // your error handling
            System.out.println(e.getMessage() + ", " + e.getCause());
        }
    }
}
